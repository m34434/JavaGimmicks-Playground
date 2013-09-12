package net.sf.javagimmicks.games.maze.player;

import net.sf.javagimmicks.games.maze.model.Command;
import net.sf.javagimmicks.games.maze.model.message.Message;
import net.sf.javagimmicks.games.maze.model.message.MessageException;

public interface Player
{
	public Command getNextCommand(Message oMessage) throws MessageException;
}
