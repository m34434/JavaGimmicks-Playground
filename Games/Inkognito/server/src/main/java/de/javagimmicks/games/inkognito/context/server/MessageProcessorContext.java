package de.javagimmicks.games.inkognito.context.server;

import java.util.HashMap;
import java.util.Map;

import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessor;
import de.javagimmicks.games.inkognito.model.Person;

public class MessageProcessorContext
{
   private Map<Person, DispatchedMessageProcessor> m_oMessageProcessors = new HashMap<Person, DispatchedMessageProcessor>();
   
   public void registerMessageProcessor(Person player, DispatchedMessageProcessor oMessageProcessor)
   {
      m_oMessageProcessors.put(player, oMessageProcessor);
   }
   
   public DispatchedMessageProcessor getMessageProcessor(Person player)
   {
      return m_oMessageProcessors.get(player);
   }
   
   public boolean isEmpty()
   {
      return m_oMessageProcessors.isEmpty();
   }
}