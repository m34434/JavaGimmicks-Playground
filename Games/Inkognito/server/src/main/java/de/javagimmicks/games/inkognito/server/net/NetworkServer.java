package de.javagimmicks.games.inkognito.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sf.javagimmicks.lang.Factory;
import net.sf.javagimmicks.lang.ReflectionFactory;

import org.apache.commons.logging.LogFactory;

import de.javagimmicks.games.inkognito.context.server.ServerContext;
import de.javagimmicks.games.inkognito.context.server.impl.DefaultServerContext;
import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.server.Game;
import de.javagimmicks.games.inkognito.server.processor.net.SocketMessageProcessor;

public class NetworkServer extends Thread
{
	private final int m_iPort;
	private final ExecutorService m_oExecutor;
    
	private final Factory<MessageProcessor> m_oFactoryPlayerB;
    private final Factory<MessageProcessor> m_oFactoryPlayerC;
    private final Factory<MessageProcessor> m_oFactoryPlayerD;
	
	public NetworkServer(final int iPort, Factory<MessageProcessor> oFactoryPlayerB, Factory<MessageProcessor> oFactoryPlayerC, Factory<MessageProcessor> oFactoryPlayerD)
	{
		m_iPort = iPort;
//		m_oExecutor = Executors.newCachedThreadPool();
		m_oExecutor = Executors.newSingleThreadExecutor();
		
		m_oFactoryPlayerB = oFactoryPlayerB;
		m_oFactoryPlayerC = oFactoryPlayerC;
		m_oFactoryPlayerD = oFactoryPlayerD;
	}
	
	public NetworkServer(final int iPort, final Class<? extends MessageProcessor> oClassPlayerB, final Class<? extends MessageProcessor> oClassPlayerC, final Class<? extends MessageProcessor> oClassPlayerD) throws IllegalArgumentException, SecurityException, NoSuchMethodException
	{
	    this(iPort, new ReflectionFactory<MessageProcessor>(oClassPlayerB), new ReflectionFactory<MessageProcessor>(oClassPlayerC), new ReflectionFactory<MessageProcessor>(oClassPlayerD));
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
			    if(!isInterrupted())
			    {
			        System.err.println("Error while accepting new connection: " + e.getMessage());
			    }
				continue;
			}
			
			// Create list of message processor:
			// - The network player
			// - Three AI player (different levels)
			List<MessageProcessor> oMessageProcessors = new ArrayList<MessageProcessor>();
			oMessageProcessors.add(oSocketMessageProcessor);
			oMessageProcessors.add(m_oFactoryPlayerB.create());
			oMessageProcessors.add(m_oFactoryPlayerC.create());
			oMessageProcessors.add(m_oFactoryPlayerD.create());
			
			Game oGameHandler = new Game(oServerContext, oMessageProcessors);
			oGameHandler.setGameLogger(LogFactory.getLog("Game"));
			
			m_oExecutor.submit(oGameHandler);
		}
	}
}
