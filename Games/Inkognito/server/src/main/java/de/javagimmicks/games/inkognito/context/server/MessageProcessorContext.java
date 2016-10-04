package de.javagimmicks.games.inkognito.context.server;

import java.util.HashMap;
import java.util.Map;

import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.model.Person;

public class MessageProcessorContext
{
   private Map<Person, MessageProcessor> m_oMessageProcessors = new HashMap<Person, MessageProcessor>();
   
   public void registerMessageProcessor(Person player, MessageProcessor oMessageProcessor)
   {
      m_oMessageProcessors.put(player, oMessageProcessor);
   }
   
   public MessageProcessor getMessageProcessor(Person player)
   {
      return m_oMessageProcessors.get(player);
   }
   
   public boolean isEmpty()
   {
      return m_oMessageProcessors.isEmpty();
   }
}