package de.javagimmicks.games.inkognito.context.server;

import de.javagimmicks.games.inkognito.message.MessageProcessor;

public interface MessageProcessorContext
{
	public void registerMessageProcessor(String sPlayerName,
			MessageProcessor oMessageProcessor);

	public MessageProcessor getMessageProcessor(String sPlayerName);

}