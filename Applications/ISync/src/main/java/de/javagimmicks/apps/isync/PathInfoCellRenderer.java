package de.javagimmicks.apps.isync;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class PathInfoCellRenderer extends DefaultTableCellRenderer
{
   private static final long serialVersionUID = -5857679718113655053L;

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value,
         boolean isSelected, boolean hasFocus, int row, int column)
   {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);
      
      setToolTipText(value == null ? "" : value.toString());
      
      return this;
   }
}
