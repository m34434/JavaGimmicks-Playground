package net.sf.javagimmicks.games.maze.model.message;

import net.sf.javagimmicks.games.maze.model.message.Message;

public enum StandardMessage implements Message
{
	WALL("WALL"), FREE("FREE"), FINISH("FINISH"), OK("OK"), CRASH("CRASH");
	
	private final String m_sMessageString;
	
	private StandardMessage(String sMessageString)
	{
		m_sMessageString = sMessageString;
	}

	public String getMessageString()
	{
		return m_sMessageString;
	}
}
