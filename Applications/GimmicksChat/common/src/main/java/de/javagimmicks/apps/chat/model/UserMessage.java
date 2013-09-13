package de.javagimmicks.apps.chat.model;

import java.io.Serializable;

public class UserMessage implements Serializable
{
   private static final long serialVersionUID = -3034535519861397427L;

   protected final UserInfo _userInfo;
   protected final Message _message;
   
   protected final boolean _whispered;

   public UserMessage(UserInfo userInfo, Message message, boolean whispered)
   {
      _userInfo = userInfo;
      _message = message;
      _whispered = whispered;
   }
   
   public UserMessage(UserInfo userInfo, String message, boolean whispered)
   {
      this(userInfo, new Message(message), whispered);
   }

   public UserMessage(UserInfo userInfo, Message message)
   {
      this(userInfo, message, false);
   }
   
   public UserMessage(UserInfo userInfo, String message)
   {
      this(userInfo, new Message(message));
   }

   public UserInfo getUserInfo()
   {
      return _userInfo;
   }

   public Message getMessage()
   {
      return _message;
   }

   public boolean isWhispered()
   {
      return _whispered;
   }
}
