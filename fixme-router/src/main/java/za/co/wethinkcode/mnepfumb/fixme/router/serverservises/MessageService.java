package za.co.wethinkcode.mnepfumb.fixme.router.serverservises;

import java.nio.channels.SocketChannel;

public class MessageService
{
    private String msg;
    private int client_id;
    private SocketChannel client;

    public MessageService( String msg, int client_id, SocketChannel client )
    {
        this.msg = msg;
        this.client_id = client_id;
        this.client = client;
    }

    public int getId()
    {
        return client_id;
    }

    public String getMsg()
    {
        return msg;
    }

    public SocketChannel getClientSocket()
    {
        return client;
    }

    public int getDeleteId( int client_id )
    {
        return this.client_id = client_id;
    }
}