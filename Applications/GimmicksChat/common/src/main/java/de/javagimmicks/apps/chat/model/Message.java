package de.javagimmicks.apps.chat.model;

import java.io.Serializable;

public class Message implements Serializable
{
   private static final long serialVersionUID = 4879326166917242754L;

   protected final String _message;

   public Message(String message)
   {
      _message = message;
   }

   public String getMessage()
   {
      return _message;
   }
}
