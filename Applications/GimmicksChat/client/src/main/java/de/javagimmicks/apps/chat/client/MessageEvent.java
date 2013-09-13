package de.javagimmicks.apps.chat.client;

import java.awt.Color;

public class MessageEvent extends UserEvent
{
   private final String _message;
   private final boolean _whispered;
  
   public MessageEvent(String name, Color color, String message, boolean whispered)
   {
      super(name, color);
      _message = message;
      _whispered = whispered;
   }

   public String getMessage()
   {
      return _message;
   }
   
   public boolean isWhispered()
   {
      return _whispered;
   }
}
