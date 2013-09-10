package de.javagimmicks.games.inkognito.context.server;

import de.javagimmicks.games.inkognito.context.GameContext;

public interface ServerContext extends GameContext
{
	public MessageProcessorContext getMessageProcessorContext();
}