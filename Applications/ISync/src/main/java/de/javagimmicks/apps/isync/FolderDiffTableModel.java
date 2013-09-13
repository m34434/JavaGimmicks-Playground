package de.javagimmicks.apps.isync;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.table.AbstractTableModel;

import net.sf.javagimmicks.io.folderdiff.FolderDiff;
import net.sf.javagimmicks.io.folderdiff.PathInfo;

class FolderDiffTableModel extends AbstractTableModel
{
   private static final long serialVersionUID = 5691259182692721127L;

   private final FolderDiff _folderDiff;
   private final ArrayList<RowInfo> _rowInfos;
   
   public FolderDiffTableModel()
   {
      _folderDiff = null;
      _rowInfos = new ArrayList<RowInfo>();
   }
   
   public FolderDiffTableModel(FolderDiff folderDiff)
   {
      _folderDiff = folderDiff;
      
      SortedSet<PathInfo> allPaths = _folderDiff.getAll();
      
      _rowInfos = new ArrayList<RowInfo>(allPaths.size());
      for(PathInfo pathInfo : allPaths)
      {
         RowInfo rowInfo = new RowInfo(pathInfo);
         
         if(!rowInfo.isEqual())
         {
            _rowInfos.add(rowInfo);
         }
      }
   }
   
   public int getColumnCount()
   {
      return 4;
   }

   public int getRowCount()
   {
      return _rowInfos.size();
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      RowInfo rowInfo = _rowInfos.get(rowIndex);
      
      switch(columnIndex)
      {
         case 0: return rowInfo.isChecked();
         case 1: return rowInfo.getSourcePathInfo();
         case 2: return rowInfo.getType();
         case 3: return rowInfo.getTargetPathInfo();
         default: return null;
      }
   }
   

   @Override
   public Class<?> getColumnClass(int columnIndex)
   {
      switch (columnIndex)
      {
         case 0: return Boolean.class;
         case 1: return PathInfo.class;
         case 2: return DiffType.class;
         case 3: return PathInfo.class;
         default: return null;
      }
   }

   @Override
   public String getColumnName(int column)
   {
      switch (column)
      {
         case 0: return "Modify";
         case 1: return _folderDiff != null ? _folderDiff.getSourceFolder().getAbsolutePath() : "Source folder";
         case 2: return "Type";
         case 3: return _folderDiff != null ? _folderDiff.getTargetFolder().getAbsolutePath() : "Target folder";
         default: return null;
      }
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return columnIndex == 0;
   }
   
   @Override
   public void setValueAt(Object value, int rowIndex, int columnIndex)
   {
      if(columnIndex == 0)
      {
         _rowInfos.get(rowIndex).setChecked((Boolean)value);
         
         fireTableCellUpdated(rowIndex, columnIndex);
      }
   }
   
   public List<RowInfo> getRowInfos()
   {
      return _rowInfos;
   }
   
   public FolderDiff getFolderDiff()
   {
      return _folderDiff;
   }

   public class RowInfo
   {
      private final PathInfo _pathInfo;
      private final DiffType _type;
      private boolean _checked;
      
      public RowInfo(PathInfo pathInfo)
      {
         _pathInfo = pathInfo;
         
         DiffType type;
         if(_folderDiff.getEqual().contains(_pathInfo))
         {
            type = DiffType.EQUAL;
         }
         else if(_folderDiff.getSourceOnly().contains(_pathInfo))
         {
            type = DiffType.SOURCE_ONLY;
         }
         else if(_folderDiff.getTargetOnly().contains(_pathInfo))
         {
            type = DiffType.TARGET_ONLY;
         }
         else
         {
            type = DiffType.DIFFERENT;
         }
         
         _type = type;
      }

      public boolean isChecked()
      {
         return _checked;
      }

      public void setChecked(boolean checked)
      {
         _checked = checked;
      }
      
      public DiffType getType()
      {
         return _type;
      }
      
      public boolean isEqual()
      {
         return _type == DiffType.EQUAL;
      }
      
      public boolean isDifferent()
      {
         return _type == DiffType.DIFFERENT;
      }
      
      public boolean isSourceOnly()
      {
         return _type == DiffType.SOURCE_ONLY;
      }
      
      public boolean isTargetOnly()
      {
         return _type == DiffType.TARGET_ONLY;
      }
      
      public PathInfo getSourcePathInfo()
      {
         return _type == DiffType.TARGET_ONLY ? null : _pathInfo;
      }

      public PathInfo getTargetPathInfo()
      {
         return _type == DiffType.SOURCE_ONLY ? null : _pathInfo;
      }
   }
   
}
