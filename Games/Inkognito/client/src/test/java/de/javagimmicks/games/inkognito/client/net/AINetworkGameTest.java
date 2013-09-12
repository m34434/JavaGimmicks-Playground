package de.javagimmicks.games.inkognito.client.net;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.sf.javagimmicks.testing.MultiThreadedTestHelper;

import org.junit.Before;
import org.junit.Test;

import de.javagimmicks.games.inkognito.server.net.NetworkServer;
import de.javagimmicks.games.inkognito.server.processor.ai.SmartAIMessageProcessor;

public class AINetworkGameTest
{
    private static final int PORT = 6201;
    
    private MultiThreadedTestHelper<Void> _helper;
    
    @Before
    public void setup()
    {
        _helper = new MultiThreadedTestHelper<Void>(true);
    }
    
    @Test
	public void testAINetworkGame() throws Exception
	{
        // Start the server
        final NetworkServer server = new NetworkServer(PORT);
        server.start();
        
        // Prepare workers == players
        final List<NetworkGameThread> workers = new ArrayList<NetworkGameThread>();
		for(int i = 1; i <= 1; ++i)
		{
		    workers.add(new NetworkGameThread(i));
		}
		
		// Apply the workers to the helper and run the multi-threaded test
		_helper.addWorkers(workers);
		_helper.executeWorkers();
		
		// Wait and shutdown the game server
		Thread.sleep(200);
		server.interrupt();
	}
	
	private static class NetworkGameThread implements Callable<Void>
	{
	    private final int m_iNumber;
	    
		public NetworkGameThread(int iNumber)
        {
		    m_iNumber = iNumber;
        }

        public Void call() throws Exception
        {
            // Create a really smart player ;-)
			SmartAIMessageProcessor oMichael = new SmartAIMessageProcessor("Michael_" + m_iNumber);
			
			// Wrap the player into a NetworkPlayer adapter and let him play a network game
			NetworkClient.joinNetworkGame("localhost", 6201, new NetworkPlayerMessageProcessorAdapter(oMichael));
			
			System.out.println("Finished!");
			
			return null;
		}
	}
}
