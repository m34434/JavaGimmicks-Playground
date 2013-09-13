package de.javagimmicks.games.jotris.model.impl;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Field;
import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.Tile;

public class DefaultField extends DefaultBlockGroup implements Field
{
   public DefaultField(Format format)
   {
      super(format.createEmptyBlocks(), format);
   }

   public void addTileAt(int row, int col, Tile tile)
   {
      Format tileFormat = tile.getFormat();
      int tileRowCount = tileFormat.getRows();
      int tileColCount = tileFormat.getCols();

      for (int tileRow = 0; tileRow < tileRowCount; ++tileRow)
      {
         int myRow = row + tileRow;

         for (int tileCol = 0; tileCol < tileColCount; ++tileCol)
         {
            int myCol = col + tileCol;

            Block tileBlock = tile.getBlockAt(tileRow, tileCol);

            if (tileBlock != null)
            {
               _blocks[myRow][myCol] = tileBlock;
            }
         }
      }
   }

   public boolean isTileFittingAt(int row, int col, Tile tile)
   {
      Format tileFormat = tile.getFormat();
      int tileRowCount = tileFormat.getRows();
      int tileColCount = tileFormat.getCols();

      if (row < 0 || row + tileRowCount > _format.getRows())
      {
         return false;
      }

      if (col < 0 || col + tileColCount > _format.getCols())
      {
         return false;
      }

      for (int tileRow = 0; tileRow < tileRowCount; ++tileRow)
      {
         int myRow = row + tileRow;

         for (int tileCol = 0; tileCol < tileColCount; ++tileCol)
         {
            int myCol = col + tileCol;

            if (hasBlockAt(myRow, myCol) &&
                  tile.hasBlockAt(tileRow, tileCol))
            {
               return false;
            }
         }
      }

      return true;
   }

   public void killRow(int row)
   {
      for (int currentRow = row; currentRow > 0; --currentRow)
      {
         for (int iCol = 0; iCol < _format.getCols(); ++iCol)
         {
            _blocks[currentRow][iCol] = _blocks[currentRow - 1][iCol];
         }
      }

      for (int currentCol = 0; currentCol < _format.getCols(); ++currentCol)
      {
         _blocks[0][currentCol] = null;
      }
   }

   public boolean isRowFull(int row)
   {
      for (int col = 0; col < _format.getCols(); ++col)
      {
         if (!hasBlockAt(row, col))
         {
            return false;
         }
      }

      return true;
   }
}
