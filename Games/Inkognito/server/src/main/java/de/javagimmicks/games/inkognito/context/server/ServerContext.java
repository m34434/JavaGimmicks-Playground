package de.javagimmicks.games.inkognito.context.server;

import de.javagimmicks.games.inkognito.context.GameContext;

public class ServerContext extends GameContext
{
   private final MessageProcessorContext m_oMessageProcessorContext = new MessageProcessorContext();

   public MessageProcessorContext getMessageProcessorContext()
   {
      return m_oMessageProcessorContext;
   }
}