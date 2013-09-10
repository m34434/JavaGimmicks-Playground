package de.javagimmicks.games.inkognito.server.processor.net;

import java.io.IOException;
import java.net.Socket;

import de.javagimmicks.games.inkognito.server.processor.StreamMessageProcessor;

public class SocketMessageProcessor extends StreamMessageProcessor
{
	private final Socket m_oSocket;
	
	public SocketMessageProcessor(Socket oSocket) throws IOException
	{
		super(oSocket.getInputStream(), oSocket.getOutputStream());
		
		m_oSocket = oSocket;
	}

	public void destroy()
	{
		super.destroy();
		
		try
		{
			m_oSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
