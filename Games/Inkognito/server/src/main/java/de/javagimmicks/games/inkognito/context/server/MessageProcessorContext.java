package de.javagimmicks.games.inkognito.context.server;

import java.util.HashMap;
import java.util.Map;

import de.javagimmicks.games.inkognito.message.MessageProcessor;

public class MessageProcessorContext
{
   private Map<String, MessageProcessor> m_oMessageProcessors = new HashMap<String, MessageProcessor>();
   
   public void registerMessageProcessor(String sPlayerName, MessageProcessor oMessageProcessor)
   {
      m_oMessageProcessors.put(sPlayerName, oMessageProcessor);
   }
   
   public MessageProcessor getMessageProcessor(String sPlayerName)
   {
      return m_oMessageProcessors.get(sPlayerName);
   }
}