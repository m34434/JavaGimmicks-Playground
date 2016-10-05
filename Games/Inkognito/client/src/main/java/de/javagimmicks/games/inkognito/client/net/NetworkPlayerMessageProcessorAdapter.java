package de.javagimmicks.games.inkognito.client.net;

import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessor;
import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessorAdapter;
import de.javagimmicks.games.inkognito.message.MessageConstants;
import de.javagimmicks.games.inkognito.message.MessageParser;
import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.message.AnsweredMessage;
import de.javagimmicks.games.inkognito.message.message.Message;

public class NetworkPlayerMessageProcessorAdapter implements NetworkPlayer, MessageConstants
{
	protected final MessageProcessor m_oMessageProcessor;
	
   public NetworkPlayerMessageProcessorAdapter(final MessageProcessor oMessageProcessor)
   {
      m_oMessageProcessor = oMessageProcessor;
   }

   public NetworkPlayerMessageProcessorAdapter(final DispatchedMessageProcessor oMessageProcessor)
   {
      this(new DispatchedMessageProcessorAdapter(oMessageProcessor));
   }

	public String process(String sMessage)
	{
		Message oMessage;
		try
		{
			oMessage = MessageParser.parseMessage(sMessage);

			if(oMessage instanceof AnsweredMessage)
			{
				Answer oAnswer = m_oMessageProcessor.processAnsweredMessage((AnsweredMessage<?>)oMessage);
				return (oAnswer == null) ? null : oAnswer.serialize();
			}
			else
			{
				m_oMessageProcessor.processMessage(oMessage);
				return null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
