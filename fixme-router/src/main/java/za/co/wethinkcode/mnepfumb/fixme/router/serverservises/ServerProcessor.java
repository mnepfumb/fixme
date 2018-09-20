package za.co.wethinkcode.mnepfumb.fixme.router.serverservises;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class ServerProcessor 
{
    private static SocketChannel client;
    private static List<SocketDetails> brokerSocketDetails = new ArrayList<>();
	private static List<SocketDetails> marketSocketDetails = new ArrayList<>();
    private static List<MessageService> messageServiceList = new ArrayList<>();

    public static String ReadMessage( SelectionKey key )
    {
        String msg;
        try
        {
            client = ( SocketChannel ) key.channel();
            ByteBuffer marketBuffer = ByteBuffer.allocate( 1024 );
            client.register( key.selector(), SelectionKey.OP_WRITE );
            client.read( marketBuffer );
            msg = new String( marketBuffer.array() ).trim();
            return ( msg );
        } 
        catch ( Exception e )
        {
            msg = "There was an error processing your message";
            return ( msg );
        }
    }

    public static void ProcessSocketDetails( SocketChannel client, SelectionKey key )
    {
        //an instance of socket details
        SocketDetails socketDetails =  new SocketDetails( client.socket().getPort(), client, key );
        //adding the created instances to their respective lists
        if (client.socket().getLocalPort() == 5000 )
        {
            brokerSocketDetails.add( socketDetails );
        }
        else if ( client.socket().getLocalPort() == 5001 )
        {
            marketSocketDetails.add( socketDetails );
        }
    }

    public static SocketDetails getBrokerList()
    {
        for ( SocketDetails socket : brokerSocketDetails )
        {
            return ( socket );
        }
        return ( null );
    }

    public static SocketDetails getMarketList()
    {
        for ( SocketDetails  socket : marketSocketDetails )
        {
            return ( socket );
        }
        return ( null );
    }

    public static void ProcessMessageService( String msg, SocketChannel client )
    {
      MessageService messageService = new MessageService( msg, client.socket().getPort(), client );
        messageServiceList.add( messageService );
    }

    public static void DeleteMessage( int client_id )
    {
        int i = 0;
        for ( MessageService msg : messageServiceList )
        {
            if ( msg.getId() != client_id )
            {
                i++;
            }
        }
        messageServiceList.remove( i );
    }

    public static MessageService getMessage( int client_id )
    {
        for ( MessageService msg : messageServiceList )
        {
            if ( msg.getId() == client_id )
                return ( msg );
        }
        return ( null );
    }

    public static boolean WriteMessage( SocketChannel client, String msg )
    {
        String message = client.socket().getPort() + " | " + msg;
        try 
        {
            client.write( ByteBuffer.wrap( message.getBytes() ) );
            return ( true );
        } 
        catch ( Exception e )
        {
            System.out.println( "Message was not sent to: ." + client +" There was an error" );
        }
        return ( false );
        
    }
}