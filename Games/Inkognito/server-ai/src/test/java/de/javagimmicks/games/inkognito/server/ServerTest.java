package de.javagimmicks.games.inkognito.server;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import de.javagimmicks.games.inkognito.context.server.impl.DefaultServerContext;
import de.javagimmicks.games.inkognito.server.processor.ai.AbstractAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.CrazyAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.NormalAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.SmartAIMessageProcessor;

public class ServerTest
{
	public static final Log GAME_LOG = LogFactory.getLog("Game");
	
	@Test
	public void aiServerTestRun()
	{
		AbstractAIMessageProcessor oPlayer1 = new CrazyAIMessageProcessor();
		AbstractAIMessageProcessor oPlayer2 = new CrazyAIMessageProcessor();
		AbstractAIMessageProcessor oPlayer3 = new NormalAIMessageProcessor();
		AbstractAIMessageProcessor oPlayer4 = new SmartAIMessageProcessor();
		
		List<AbstractAIMessageProcessor> oPlayers = Arrays.asList(new AbstractAIMessageProcessor[] {oPlayer1, oPlayer2, oPlayer3, oPlayer4});
		GameHandler oGameHandler = new GameHandler(new DefaultServerContext(), oPlayers, 20000);
//		oGameHandler.setGameLogger(GAME_LOG);
		
		oGameHandler.run();
		
		for(AbstractAIMessageProcessor oProcessor : oPlayers)
		{
			System.out.printf("%1$10s: %2$s%n", oProcessor.getPlayerName(), oProcessor.getWinCount());
		}
	}
}
