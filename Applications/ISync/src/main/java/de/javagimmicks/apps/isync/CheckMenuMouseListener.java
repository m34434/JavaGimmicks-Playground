package de.javagimmicks.apps.isync;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

class CheckMenuMouseListener extends MouseAdapter
{
   @Override
   public void mousePressed(MouseEvent e)
   {
      showMenu(e);
   }

   @Override
   public void mouseReleased(MouseEvent e)
   {
      showMenu(e);
   }

   @SuppressWarnings("serial")
   private void showMenu(MouseEvent e)
   {
      if(!e.isPopupTrigger())
      {
         return;
      }
      
      final JTable table = (JTable)e.getSource();
      final FolderDiffTableModel model = (FolderDiffTableModel)table.getModel();
      final int[] rows = table.getSelectedRows();
      
      JPopupMenu menu = new JPopupMenu("Check actions");
      menu.add(new AbstractAction("Check selected rows")
      {
         public void actionPerformed(ActionEvent e)
         {
            for(int row : rows)
            {
               model.setValueAt(Boolean.TRUE, row, 0);
            }
         }
      });
      menu.add(new AbstractAction("Uncheck selected rows")
      {
         public void actionPerformed(ActionEvent e)
         {
            for(int row : rows)
            {
               model.setValueAt(Boolean.FALSE, row, 0);
            }
         }
      });
      
      Point popupLocation = table.getPopupLocation(e);
      if(popupLocation != null)
      {
         menu.show(table, popupLocation.x, popupLocation.y);
      }
      else
      {
         menu.show(table, e.getX(), e.getY());
      }
   }
}
