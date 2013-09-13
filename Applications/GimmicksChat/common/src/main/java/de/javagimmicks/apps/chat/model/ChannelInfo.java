package de.javagimmicks.apps.chat.model;

import java.io.Serializable;

public class ChannelInfo implements Serializable
{
   private static final long serialVersionUID = -3962117081645970916L;

   public static enum Type {JOINED, LEFT};
   
   protected final UserInfo _userInfo;
   protected final Type _type;
   
   public ChannelInfo(UserInfo info, Type type)
   {
      _userInfo = info;
      _type = type;
   }

   public UserInfo getUserInfo()
   {
      return _userInfo;
   }

   public Type getType()
   {
      return _type;
   }
}
