package de.javagimmicks.games.inkognito.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import de.javagimmicks.games.inkognito.context.server.ServerContext;
import de.javagimmicks.games.inkognito.context.server.impl.DefaultServerContext;
import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.server.GameHandler;
import de.javagimmicks.games.inkognito.server.processor.ai.CrazyAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.NormalAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.SmartAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.net.SocketMessageProcessor;

public class NetworkServer extends Thread
{
	public static void main(String[] args)
	{
		int iPort = (args.length == 1) ? Integer.parseInt(args[0]) : 6201;
		
		DOMConfigurator.configure("conf/log4j.xml");
		new NetworkServer(iPort).start();
	}
	
	private static final int PLAYER_COUNT = 4;
	
	private final int m_iPort;
	private final ExecutorService m_oExecutor;
	
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
			SocketMessageProcessor oSocketMessageProcessor;
			ServerContext oServerContext;
			try
			{
				Socket oSocket = oServerSocket.accept();
				
//				oSocket.setSoTimeout(5000);
				
				oServerContext = new DefaultServerContext();
				
				oSocketMessageProcessor = new SocketMessageProcessor(oSocket);
				oSocketMessageProcessor.setMessageLogger(LogFactory.getLog("Player1"));
			}
			catch (IOException e)
			{
				System.err.println("Error while accepting new connection: " + e.getMessage());
				continue;
			}
			
			// Create list of message processor:
			// - The network player
			// - Three AI player (different levels)
			List<MessageProcessor> oMessageProcessors = new ArrayList<MessageProcessor>(PLAYER_COUNT);
			oMessageProcessors.add(oSocketMessageProcessor);
			oMessageProcessors.add(new CrazyAIMessageProcessor());
			oMessageProcessors.add(new NormalAIMessageProcessor());
			oMessageProcessors.add(new SmartAIMessageProcessor());
			
			GameHandler oGameHandler = new GameHandler(oServerContext, oMessageProcessors);
			oGameHandler.setGameLogger(LogFactory.getLog("Game"));
			
			m_oExecutor.submit(oGameHandler);
		}
	}
}
