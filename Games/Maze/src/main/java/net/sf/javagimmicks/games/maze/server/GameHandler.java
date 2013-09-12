package net.sf.javagimmicks.games.maze.server;

import net.sf.javagimmicks.games.maze.model.Command;
import net.sf.javagimmicks.games.maze.model.DrawableCell;
import net.sf.javagimmicks.games.maze.model.Maze;
import net.sf.javagimmicks.games.maze.model.Robot;
import net.sf.javagimmicks.games.maze.model.message.GeometryMessage;
import net.sf.javagimmicks.games.maze.model.message.Message;
import net.sf.javagimmicks.games.maze.model.message.MessageException;
import net.sf.javagimmicks.games.maze.model.message.StandardMessage;
import net.sf.javagimmicks.games.maze.player.Player;

import org.apache.commons.logging.Log;


public class GameHandler<T extends DrawableCell<T>> implements Runnable
{
	private final Player m_oPlayer;
	private final Maze<T> m_oMaze;
	
	private Log m_oGameLog;
	
	public GameHandler(final Player oPlayer, final Maze<T> oMaze)
	{
		m_oPlayer = oPlayer;
		m_oMaze = oMaze;
	}
	
	public void setGameLogger(Log oGameLogger)
	{
		m_oGameLog = oGameLogger;
	}

	public void run()
	{
		Robot oRobot = new Robot<T>(m_oMaze);
		Message oMessage = new GeometryMessage(m_oMaze.getCurrentCell().getGeometry());
		
		while(oMessage != StandardMessage.FINISH && oMessage != StandardMessage.CRASH)
		{
			try
			{
				logSend(oMessage);
				
				Command oCommand = m_oPlayer.getNextCommand(oMessage);
				
				logReceive(oCommand);
				
				oMessage = oRobot.executeCommand(oCommand);
			}
			catch (MessageException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
//				return;
			}
		}
	}
	
	private void logSend(Message oMessage)
	{
		String sMessage = new StringBuffer()
		.append("Player <- ")
		.append(oMessage)
		.toString();
		
		m_oGameLog.info(sMessage);
	}
	
	private void logReceive(Command oCommand)
	{
		String sMessage = new StringBuffer()
		.append("Player -> ")
		.append(oCommand)
		.toString();
		
		m_oGameLog.info(sMessage);
	}
	
}
