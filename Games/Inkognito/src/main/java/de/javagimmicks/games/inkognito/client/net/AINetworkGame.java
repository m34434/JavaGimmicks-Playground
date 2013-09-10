package de.javagimmicks.games.inkognito.client.net;

import java.io.IOException;

import de.javagimmicks.games.inkognito.server.processor.ai.SmartAIMessageProcessor;

public class AINetworkGame
{
	public static void main(String[] args) throws IOException
	{
		for(int i = 0; i < 1; ++i)
		{
			new NetworkGameThread().start();
		}
	}
	
	private static class NetworkGameThread extends Thread
	{
		public void run()
		{
			SmartAIMessageProcessor oMichael = new SmartAIMessageProcessor("Michael");
			try
			{
				NetworkClient.runNetworkGame("localhost", 6201, new NetworkPlayerMessageProcessorAdapter(oMichael));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Finished!");
		}
	}
}
