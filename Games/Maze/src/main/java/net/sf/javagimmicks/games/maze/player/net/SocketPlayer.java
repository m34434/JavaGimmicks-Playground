package net.sf.javagimmicks.games.maze.player.net;

import java.io.IOException;
import java.net.Socket;

import net.sf.javagimmicks.games.maze.player.StreamPlayer;


public class SocketPlayer extends StreamPlayer
{
	private final Socket m_oSocket;
	
	public SocketPlayer(Socket oSocket) throws IOException
	{
		super(oSocket.getInputStream(), oSocket.getOutputStream());
		
		m_oSocket = oSocket;
	}

	protected void destroy()
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
