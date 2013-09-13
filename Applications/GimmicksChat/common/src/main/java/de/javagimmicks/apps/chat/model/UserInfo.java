package de.javagimmicks.apps.chat.model;

import java.awt.Color;
import java.io.Serializable;

public class UserInfo implements Serializable
{
   private static final long serialVersionUID = -3890249362099137531L;

   protected final String _username;
   protected final Color _color;
   
   public UserInfo(String username, Color color)
   {
      _username = username;
      _color = color;
   }
   
   public UserInfo(String username)
   {
      this(username, Color.BLACK);
   }

   public String getUsername()
   {
      return _username;
   }

   public Color getColor()
   {
      return _color;
   }
   
   public String toString()
   {
      return _username;
   }
   
   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      
      if(!(o instanceof UserInfo))
      {
         return false;
      }
      
      UserInfo other = (UserInfo)o;
      
      return this._username.equals(other._username) && this._color.equals(other._color);
   }

   @Override
   public int hashCode()
   {
      int result = 147892342;
      result += _username != null ? 5 * _username.hashCode() : 0;
      result += _color != null ? 3 * _color.hashCode() : 0;
      
      return result;
   }
}
