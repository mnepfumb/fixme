package za.co.wethinkcode.mnepfumb.fixme.market;

import za.co.wethinkcode.mnepfumb.fixme.market.managemessages.MessageController;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;


public class Markets
{
    //private static BufferedReader input = null;
    private static String result; 
    
    public static void main( String[] args ) throws Exception
    {
        InetSocketAddress address = new InetSocketAddress( InetAddress.getByName( "localhost" ), 5001 );
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking( false );
        sc.connect( address );
        sc.register( selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ );
        while ( true )
        {
            if ( selector.select() > 0 )
            {
                Set<SelectionKey> readySet = selector.selectedKeys();
                Boolean doneStatus = processReadySet( readySet );
                if ( doneStatus )
                    break;
            }
      }
      sc.close();
   }

    public static Boolean processReadySet( Set<SelectionKey> readySet ) throws Exception
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
                    return true;
                System.out.println("Market connected to server\n");
            }
            if ( key.isReadable() )
            {
                SocketChannel sc = ( SocketChannel ) key.channel();
                ByteBuffer MarketBuffer = ByteBuffer.allocate( 1024 );
                sc.read( MarketBuffer );
                
                result = new String( MarketBuffer.array() ).trim();
                if ( result != null )
                {                    
                    System.out.println( "Message received from Server: " + result + "\n" );
                    sc.register( key.selector(), SelectionKey.OP_WRITE );
                }
            }
            if ( key.isWritable() ) 
            {
                String msg1 = MessageController.Controller( result );
                SocketChannel sc = ( SocketChannel ) key.channel();
                ByteBuffer MarketBuffer = ByteBuffer.wrap( msg1.getBytes() );
                sc.write( MarketBuffer );
                System.out.println( "Market sent a msg: " + msg1 );
                sc.register( key.selector(), SelectionKey.OP_READ );
            }
        }
        return ( false );
    }

    public static Boolean processConnect( SelectionKey key ) 
    {
        SocketChannel marketSocket = ( SocketChannel ) key.channel();
        try 
        {
            while ( marketSocket.isConnectionPending() )
            {
                marketSocket.finishConnect();
            }
        } 
        catch ( IOException e ) 
        {
            key.cancel();
            e.printStackTrace();
            return ( false );
        }
        return ( true );
    }
}