package de.javagimmicks.apps.chat.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class UserListCellRenderer extends DefaultListCellRenderer
{
   private static final long serialVersionUID = 370022803772579754L;

   private final Map<String, Color> _colorMap;
   
   public UserListCellRenderer(Map<String, Color> map)
   {
      _colorMap = map;
   }

   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
         boolean cellHasFocus)
   {
      Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      if(!cellHasFocus)
      {
         setForeground(_colorMap.get((String)value));
      }
      
      return result;
   }
}
