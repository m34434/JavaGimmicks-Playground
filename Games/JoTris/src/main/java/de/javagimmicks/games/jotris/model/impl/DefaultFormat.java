package de.javagimmicks.games.jotris.model.impl;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Format;

public class DefaultFormat implements Format
{
   private final int _rows;
   private final int _cols;

   public static DefaultFormat fromBlocks(Block[][] blocks)
   {
      return fromBlocks(blocks, true);
   }

   public static DefaultFormat fromBlocks(Block[][] blocks, boolean validate)
   {
      int rows = blocks.length;
      if (blocks.length == 0)
      {
         throw new IllegalArgumentException("Rows must be greater than 0!");
      }

      int cols = blocks[0].length;
      if (cols <= 0)
      {
         throw new IllegalArgumentException("Cols must be greater than 0!");
      }

      DefaultFormat result = new DefaultFormat(rows, cols);

      if (validate && !result.validate(blocks))
      {
         throw new IllegalArgumentException(
               "Block corrupted! Different row lenghts!");
      }

      return result;
   }

   public static DefaultFormat fromRowsAndCols(int rows, int cols)
   {
      if (rows <= 0)
      {
         throw new IllegalArgumentException("Rows must be greater than 0!");
      }
      if (cols <= 0)
      {
         throw new IllegalArgumentException("Cols must be greater than 0!");
      }

      return new DefaultFormat(rows, cols);
   }

   private DefaultFormat(int rows, int cols)
   {
      _rows = rows;
      _cols = cols;
   }

   public int getRows()
   {
      return _rows;
   }

   public int getCols()
   {
      return _cols;
   }

   public Format turn()
   {
      return new DefaultFormat(_cols, _rows);
   }

   public Block[][] createEmptyBlocks()
   {
      return new DefaultBlock[_rows][_cols];
   }

   private boolean validate(Block[][] blocks)
   {
      if (blocks.length != _rows)
      {
         return false;
      }

      for (Block[] arrRow : blocks)
      {
         if (arrRow.length != _cols)
         {
            return false;
         }
      }

      return true;
   }
}
