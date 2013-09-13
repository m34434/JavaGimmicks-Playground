package de.javagimmicks.apps.isync;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class DiffTypeCellRenderer extends DefaultTableCellRenderer
{
   private static final long serialVersionUID = -5857679718113655053L;

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value,
         boolean isSelected, boolean hasFocus, int row, int column)
   {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);
      
      setText(getText((DiffType)value));
      setHorizontalAlignment(SwingConstants.CENTER);
      
      return this;
   }
   
   private static String getText(DiffType type)
   {
      switch (type)
      {
         case SOURCE_ONLY: return "->";
         case TARGET_ONLY: return "XXX";
         case EQUAL: return "==";
         case DIFFERENT: return "!=";
         default: return "  ";
      }
   }
}
