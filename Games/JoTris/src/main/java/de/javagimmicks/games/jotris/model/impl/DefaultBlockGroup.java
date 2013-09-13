package de.javagimmicks.games.jotris.model.impl;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.BlockGroup;
import de.javagimmicks.games.jotris.model.Format;

public class DefaultBlockGroup implements BlockGroup
{
   protected final Block[][] _blocks;
   protected final Format _format;

   protected DefaultBlockGroup(Block[][] blocks, Format format)
   {
      _blocks = blocks;
      _format = format;
   }

   public Block getBlockAt(int row, int col)
   {
      return _blocks[row][col];
   }

   public boolean hasBlockAt(int row, int col)
   {
      return getBlockAt(row, col) != null;
   }

   public Block[][] getBlocks()
   {
      return _blocks.clone();
   }

   public Format getFormat()
   {
      return _format;
   }

   public String toString()
   {
      StringBuilder result = new StringBuilder();

      int rows = _format.getRows();
      int cols = _format.getCols();

      for (int row = 0; row < rows; ++row)
      {
         for (int col = 0; col < cols; ++col)
         {
            result.append(hasBlockAt(row, col) ? "X" : " ");
         }

         result.append(System.getProperty("line.separator"));
      }

      return result.toString();
   }
}
