package za.co.wethinkcode.mnepfumb.fixme.broker;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Client
{
    private static BufferedReader input = null;
    private static String msg;
    
    public static void main( String[] args ) throws Exception
    {
        InetSocketAddress address = new InetSocketAddress( InetAddress.getByName( "localhost" ), 5000 );
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking( false );
        sc.connect( address );
        sc.register( selector, SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE );
        input = new BufferedReader( new InputStreamReader( System.in ) );
        while ( true )
        {
            try
            {
                boolean temp = processReadySet( selector.selectedKeys() );
                if ( temp )
                {
                    System.out.println("Your connection has been closed" );

                    break;
                }
                if ( selector.select() > 0 )
                    continue;
            }
            catch ( Exception e )
            {
                sc.close();
            }
            Set<SelectionKey> readySet = selector.selectedKeys();
            processReadySet( readySet );
        }
   }

    public static boolean processReadySet( Set<SelectionKey> readySet ) throws Exception
    {
        SelectionKey key;
        Iterator<SelectionKey> iterator;
        iterator = readySet.iterator();
        while ( iterator.hasNext() )
        {
            key = iterator.next();
            iterator.remove();
            
            if ( key.isConnectable() )
            {
                Boolean connected = processConnect( key );
                if ( !connected )
                {
                    return ( true );
                }
                System.out.println( "Broker connected to server\n" );
            }
            if ( key.isReadable() )
            {
                SocketChannel sc = ( SocketChannel ) key.channel();
                ByteBuffer ClientBuffer = ByteBuffer.allocate( 1024 );
                sc.read( ClientBuffer );
                ClientBuffer.flip();
                String result = new String( ClientBuffer.array() ).trim();
                System.out.println( "Message received from Server: " + result + " \n " );
                //Controlling Client shutdown
                if ( msg.trim().equalsIgnoreCase( "bye" ) )
                {
                    return ( true );
                }
                sc.register( key.selector(), SelectionKey.OP_WRITE );

            }
            if ( key.isWritable() ) 
            {
                System.out.println( "Acceptable message format: buy or sell | currency quantity (5) | currency name (USD) | price (200)\n" );
                System.out.print( "Broker message (type exit to stop): " );
                msg = input.readLine();
                SocketChannel client = ( SocketChannel ) key.channel();
                ByteBuffer ClientBuffer = ByteBuffer.wrap( msg.getBytes() );
                if ( msg != null && msg.length() > 0 )
                {
                    client.write( ClientBuffer );
                    client.register( key.selector(), SelectionKey.OP_READ );
                }


            }
        }
        return ( false );
    }

    public static Boolean processConnect( SelectionKey key )
    {

        SocketChannel client = ( SocketChannel ) key.channel();
        try
        {
            while ( client.isConnectionPending() )
            {
                client.finishConnect();
            }
        }
        catch ( IOException e )
        {
            key.cancel();
            return ( false );
        }
        return ( true );
    }
}