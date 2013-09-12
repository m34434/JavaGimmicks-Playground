package net.sf.javagimmicks.games.maze.model;

import net.sf.javagimmicks.games.maze.model.message.Message;
import net.sf.javagimmicks.games.maze.ui.MazeUI;

public class UIRobot<T extends DrawableCell<T>> extends Robot<T>
{
	private final MazeUI<T> m_oMazeUI;

	public UIRobot(Maze<T> oMaze)
	{
		super(oMaze);
		
		m_oMazeUI = new MazeUI<T>(oMaze);
	}

	@Override
	public Message executeCommand(Command oCommand)
	{
		Message oResult = super.executeCommand(oCommand);
		
		m_oMazeUI.repaint();
		
		return oResult;
	}
	
	public MazeUI<T> getMazeUI()
	{
		return m_oMazeUI;
	}
}
