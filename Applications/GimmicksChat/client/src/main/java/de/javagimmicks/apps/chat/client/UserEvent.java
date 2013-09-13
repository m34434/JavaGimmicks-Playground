package de.javagimmicks.apps.chat.client;

import java.awt.Color;

public class UserEvent
{
   private final String _name;
   private final Color _color;
   
   public UserEvent(String name, Color color)
   {
      _color = color;
      _name = name;
   }

   public String getUserName()
   {
      return _name;
   }
   
   public Color getUserColor()
   {
      return _color;
   }
}
