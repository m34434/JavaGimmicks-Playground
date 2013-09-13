package de.javagimmicks.apps.chat.model;

import java.io.Serializable;

public class Login implements Serializable
{
   private static final long serialVersionUID = -4897779854454833049L;

   protected final UserInfo _userInfo;
   protected final String _password;
   
   public Login(UserInfo userInfo, String password)
   {
      _userInfo = userInfo;
      _password = password;
   }
   
   public String getPassword()
   {
      return _password;
   }

   public UserInfo getUserInfo()
   {
      return _userInfo;
   }
}
