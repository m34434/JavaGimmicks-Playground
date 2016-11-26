package de.javagimmicks.games.inkognito.client.net;

import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessor;
import de.javagimmicks.games.inkognito.message.MessageConstants;
import de.javagimmicks.games.inkognito.message.StringMessageProcessor;
import de.javagimmicks.games.inkognito.message.answer.Answer;

public class NetworkPlayerMessageProcessorAdapter implements NetworkPlayer, MessageConstants
{
	protected final DispatchedMessageProcessor m_oMessageProcessor;
	
   public NetworkPlayerMessageProcessorAdapter(final DispatchedMessageProcessor oMessageProcessor)
   {
      m_oMessageProcessor = oMessageProcessor;
   }

	public String process(String sMessage)
	{
	   Answer a = StringMessageProcessor.processStringMessage(m_oMessageProcessor, sMessage);
	   
	   return a != null ? a.serialize() : null;
	}
}
