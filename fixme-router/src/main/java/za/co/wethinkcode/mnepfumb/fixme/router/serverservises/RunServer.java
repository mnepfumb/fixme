package za.co.wethinkcode.mnepfumb.fixme.router.serverservises;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class RunServer implements Runnable
{
    private Selector selector;
	private int port;
	private boolean broker = false;

    public RunServer( Integer port )
    {
        this.port = port;
    }
	
	public void run() 
	{
		ServerSocketChannel serverSocketChannel;
		try 
		{
			serverSocketChannel = ServerSocketChannel.open();
			InetSocketAddress address = new InetSocketAddress( "localhost", port );
			serverSocketChannel.bind( address );
			serverSocketChannel.configureBlocking( false );
			selector = Selector.open();
			serverSocketChannel.register( selector, SelectionKey.OP_ACCEPT );
			System.out.println( "Listening for connections on port " + port );
			init();
		} 
		catch ( IOException ex )
		{
			ex.printStackTrace();
			//return;
		}
	}

	public void init() 
	{
		while ( true )
		{
			try
			{
				if ( selector.selectNow() <= 0 )
				continue;
			} 
			catch ( IOException ex )
			{
				ex.printStackTrace();
				break;
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
			while ( iterator.hasNext() )
			{
				SelectionKey key = iterator.next();
				iterator.remove();
				try 
				{
					if ( key.isAcceptable() )
					{
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						client.configureBlocking( false );
						//process the connected client and add their details in the relevant lists
						ServerProcessor.ProcessSocketDetails( client, key );
						client.register( key.selector(), SelectionKey.OP_READ );
					}
					if ( key.isReadable() )
					{
						String msg = ServerProcessor.ReadMessage( key );
						SocketChannel client = ( SocketChannel ) key.channel();
						if ( msg != null && msg.length() > 0 )
						{
							if ( client.socket().getLocalPort() == 5000 && ServerProcessor.getMessage( client.socket().getPort() ) == null )
							{
								if ( MessageValidator.checkFixMessages( msg ) )
								{
									ServerProcessor.ProcessMessageService( msg, client );
									broker = true;
								}
								else 
								{
									if ( !msg.trim().equalsIgnoreCase( "bye" ) )
									{
										msg = "Your Input does not meet the FIX message standards.\n" +
												" FIX standard: buy or sell | quantity | currency name | price \n" +
												"1. You can buy or sell\n" +
												"2. quantity of the currency you want to buy\n" +
												"3. currency you want to buy e.g USD\n" +
												"4. price at which you want to buy";
									}
									ServerProcessor.ProcessMessageService( msg, client );
									broker = false;
								}
							}
							else if ( client.socket().getLocalPort() == 5001 && ServerProcessor.getMessage( client.socket().getPort() ) == null )
							{
								ServerProcessor.ProcessMessageService( msg, client );
								broker = false;
							}
							client.register( key.selector(), SelectionKey.OP_WRITE );
						}
					}
					if ( key.isWritable() )
					{
						SocketChannel client = ( SocketChannel ) key.channel();
						if ( broker )
						{
							//process msgs going to server
							MessageService messageService = ServerProcessor.getMessage( client.socket().getPort() );
							SocketDetails marketDetails = ServerProcessor.getMarketList();
							//send the message to be written in the market
							if ( ServerProcessor.WriteMessage( marketDetails.getClientSocket(), messageService.getMsg() ) )
							{
								//remove the message from the list
								ServerProcessor.DeleteMessage( client.socket().getPort() );
								marketDetails.getClientSocket().register( key.selector(), SelectionKey.OP_READ );
								broker = false;
							}
							else
							{
								broker = true;
							}
						}
						else
						{
							MessageService messageService = ServerProcessor.getMessage( client.socket().getPort() );
							SocketDetails brokerDetails = ServerProcessor.getBrokerList();

							if ( ServerProcessor.WriteMessage( brokerDetails.getClientSocket(), messageService.getMsg() ) )
							{
								//remove the message from the list
								ServerProcessor.DeleteMessage( client.socket().getPort() );
								brokerDetails.getClientSocket().register( key.selector(), SelectionKey.OP_READ );
								if ( messageService.getMsg().trim().equalsIgnoreCase( "bye" ) )
								{
									//killing the process
									key.cancel();
									brokerDetails.getClientSocket().close();
									System.out.println( "Connection closed...\n" );
									continue;
								}
								broker = true;
							}
							else
							{
								broker = false;
							}
						}
						client.register( key.selector(), SelectionKey.OP_READ );
						client.finishConnect();
					}
				}
				catch ( IOException ex )
				{
					key.cancel();
					try
					{
						key.channel().close();
					} 
					catch ( IOException e )
					{}
				}
			}
		}
	}
}