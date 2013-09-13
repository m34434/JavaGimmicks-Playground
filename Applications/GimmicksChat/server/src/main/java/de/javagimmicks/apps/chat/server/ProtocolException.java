package de.javagimmicks.apps.chat.server;

public class ProtocolException extends Exception
{
   private static final long serialVersionUID = 8734135030022254072L;

   public ProtocolException(String message)
   {
      super(message);
   }
}