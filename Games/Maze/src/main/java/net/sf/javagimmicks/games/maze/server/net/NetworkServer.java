package net.sf.javagimmicks.games.maze.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sf.javagimmicks.games.maze.impl.MazeImpl;
import net.sf.javagimmicks.games.maze.impl.OrthogonalCell;
import net.sf.javagimmicks.games.maze.model.Maze;
import net.sf.javagimmicks.games.maze.player.net.SocketPlayer;
import net.sf.javagimmicks.games.maze.server.GameHandler;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;


public class NetworkServer extends Thread
{
	public static void main(String[] args)
	{
		int iPort = (args.length == 1) ? Integer.parseInt(args[0]) : 6202;
		
		DOMConfigurator.configure(NetworkServer.class.getClassLoader().getResource("log4j.xml"));
		new NetworkServer(iPort).start();
	}
	
	private final ExecutorService m_oExecutor;
	private final int m_iPort;

	public NetworkServer(final int iPort)
	{
		m_iPort = iPort;
//		m_oExecutor = Executors.newCachedThreadPool();
		m_oExecutor = Executors.newSingleThreadExecutor();
	}

	public void run()
	{
		// Setup the ServerSocket
		final ServerSocket oServerSocket;
		try
		{
			oServerSocket = new ServerSocket(m_iPort);
		}
		catch (IOException e)
		{
			System.err.println("Error while listening on port " + m_iPort + "!");
			return;
		}
		
		// Register a shutdown hook for valid cleanup
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				// Interrupt the server thread
				NetworkServer.this.interrupt();
				try
				{
					// Now close the socket
					oServerSocket.close();
				}
				catch (IOException e)
				{
					System.err.println("Error while closing server socket: " + e.getMessage());
				}
				
				// Now we wait for the server thread to finish
				try
				{
					NetworkServer.this.join();
				}
				catch (InterruptedException e)
				{
					System.err.println("InterruptedException while waiting for server to shutdown!");
				}
				
				// Now, we signal all running game threads to interrupt
				m_oExecutor.shutdownNow();
				try
				{
					// We give the game threads 10 seconds to finish
					m_oExecutor.awaitTermination(10, TimeUnit.SECONDS);
				}
				catch (InterruptedException e)
				{
					System.err.println("InterruptedException while waiting for server threads to shutdown!");
				}
			}
		});

		// Accept new connections while not interrupted
		while(!Thread.interrupted())
		{
			SocketPlayer oSocketMessageProcessor;
			try
			{
				Socket oSocket = oServerSocket.accept();
				
//				oSocket.setSoTimeout(5000);
				
				oSocketMessageProcessor = new SocketPlayer(oSocket);
				oSocketMessageProcessor.setMessageLogger(LogFactory.getLog("Player"));
			}
			catch (IOException e)
			{
				System.err.println("Error while accepting new connection: " + e.getMessage());
				continue;
			}
			
			Maze<OrthogonalCell> oMaze = new MazeImpl<OrthogonalCell>(new OrthogonalCell(0,0), 20);
			
			GameHandler oGameHandler = new GameHandler<OrthogonalCell>(oSocketMessageProcessor, oMaze);
			oGameHandler.setGameLogger(LogFactory.getLog("Game"));
			
			m_oExecutor.submit(oGameHandler);
		}
	}
}
