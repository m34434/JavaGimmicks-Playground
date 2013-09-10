package de.javagimmicks.games.inkognito.context.server.impl;

import de.javagimmicks.games.inkognito.context.impl.DefaultGameContext;
import de.javagimmicks.games.inkognito.context.server.MessageProcessorContext;
import de.javagimmicks.games.inkognito.context.server.ServerContext;

public class DefaultServerContext extends DefaultGameContext implements ServerContext
{
	private final MessageProcessorContext m_oMessageProcessorContext = new DefaultMessageProcessorContext();

	public MessageProcessorContext getMessageProcessorContext()
	{
		return m_oMessageProcessorContext;
	}

}
