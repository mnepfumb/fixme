package za.co.wethinkcode.mnepfumb.fixme.router.serverservises;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketDetails
{
    private int clientID;
    private SocketChannel clientSocket;
    private SelectionKey key;
    
    public SocketDetails( int clientID, SocketChannel clientSocket, SelectionKey key )
    {
        this.clientID = clientID;
        this.clientSocket = clientSocket;
        this.key = key;
    }

    public SocketChannel getClientSocket()
    {
        return ( clientSocket );
    }

    public int getClientId()
    {
        return ( clientID );
    }

    public SelectionKey getKey()
    {
        return ( key );
    }
}