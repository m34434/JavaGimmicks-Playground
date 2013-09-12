package net.sf.javagimmicks.games.maze.model;

import net.sf.javagimmicks.games.maze.model.message.Message;
import net.sf.javagimmicks.games.maze.model.message.StandardMessage;

public class Robot<T extends Cell<T>>
{
	private Maze<T> m_oMaze;
	private int m_iCurrentDirection;
	
	public Robot(Maze<T> oMaze)
	{
		m_oMaze = oMaze;
	}
	
	public Message executeCommand(Command oCommand)
	{
		switch(oCommand)
		{
			case LOOK:
			{
				return look();
			}
			case TURN_LEFT:
			{
				m_iCurrentDirection = (m_iCurrentDirection == 0) ? m_oMaze.getGeometry() - 1 : m_iCurrentDirection - 1;
				return StandardMessage.OK;
			}
			case TURN_RIGHT:
			{
				m_iCurrentDirection = (m_iCurrentDirection == m_oMaze.getGeometry() - 1) ? 0 : m_iCurrentDirection + 1;
				return StandardMessage.OK;
			}
			case MOVE:
			{
				Message oLookResult = look();
				
				if(oLookResult == StandardMessage.WALL)
				{
					return StandardMessage.CRASH;
				}
				else if(oLookResult == StandardMessage.FINISH)
				{
					return oLookResult;
				}
				else
				{
					m_oMaze.move(m_iCurrentDirection);
					
					if(m_oMaze.getPercentageVisited() == 100.0)
					{
						return StandardMessage.FINISH;
					}
					else
					{
						return StandardMessage.OK;
					}
				}
			}
		}
		return StandardMessage.OK;
	}
	
	private Message look()
	{
		T oCurrentCell = m_oMaze.getCurrentCell();
		boolean[] oWalls = m_oMaze.getCellInfo().get(oCurrentCell);
		
		return oWalls[m_iCurrentDirection] ? StandardMessage.FREE : StandardMessage.WALL;
	}
}
