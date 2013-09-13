package de.javagimmicks.apps.isync;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import net.sf.javagimmicks.io.folderdiff.FolderDiff;
import net.sf.javagimmicks.io.folderdiff.PathInfo;
import de.javagimmicks.apps.isync.FolderDiffTableModel.RowInfo;
import de.javagimmicks.apps.isync.compare.CompareTool;

class ShowCompareMouseListener extends MouseAdapter
{
   @Override
   public void mouseClicked(MouseEvent e)
   {
      doCompare(e);
   }

   private void doCompare(MouseEvent e)
   {
      if(!SwingUtilities.isLeftMouseButton(e) || e.getClickCount() < 2)
      {
         return;
      }
      
      final JTable table = (JTable)e.getSource();
      
      final int clickedRow = table.rowAtPoint(e.getPoint());
      final FolderDiffTableModel model = (FolderDiffTableModel)table.getModel();

      final RowInfo rowInfo = model.getRowInfos().get(clickedRow);

      if(!rowInfo.isDifferent())
      {
         return;
      }
      
      final PathInfo sourcePathInfo = rowInfo.getSourcePathInfo();
      final PathInfo targetPathInfo = rowInfo.getTargetPathInfo();
      
      final FolderDiff folderDiff = model.getFolderDiff();
      final File sourceFile = sourcePathInfo.applyToFolder(folderDiff.getSourceFolder());
      final File targetFile = targetPathInfo.applyToFolder(folderDiff.getTargetFolder());
      
      openCompareTool(sourceFile, targetFile, table.getParent());
   }
   
   private void openCompareTool(File sourceFile, File targetFile, Container container)
   {
      final CompareTool compareTool;
      try
      {
         compareTool = CompareTool.getInstance();
      }
      catch (IOException e)
      {
         JOptionPane.showMessageDialog(
            container,
            "Error while loading compare settings: " + e.getMessage() + "! Check if configuration file '" + CompareTool.FILE_NAME + "' exists in application folder or '${user.home}/.isync'!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         
         return;
      }

      try
      {
         compareTool.execute(sourceFile, targetFile);
      }
      catch (IOException e)
      {
         JOptionPane.showMessageDialog(
            container,
            "Error while opening compare tool: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
   }
}
