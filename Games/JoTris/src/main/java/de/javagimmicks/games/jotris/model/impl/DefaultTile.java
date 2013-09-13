package de.javagimmicks.games.jotris.model.impl;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Color;
import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.Tile;

public class DefaultTile extends DefaultBlockGroup implements Cloneable, Tile
{
   public DefaultTile(Format format)
   {
      this(format.createEmptyBlocks(), format);
   }

   public DefaultTile(Block[][] blocks)
   {
      this(blocks, DefaultFormat.fromBlocks(blocks));
   }
   
   DefaultTile(Block[][] blocks, Format format)
   {
      super(blocks, format);
   }
   
   public DefaultTile clone()
   {
      Block[][] clonedBlocks = _format.createEmptyBlocks();
      
      int rows = _format.getRows();
      int cols = _format.getCols();
      
      for(int row = 0; row < rows; ++row)
      {
         for(int col = 0; col < cols; ++col)
         {
            clonedBlocks[row][col] = _blocks[row][col];
         }
      }
      
      return new DefaultTile(clonedBlocks, _format);
   }
   
   public DefaultTile clone(Color newColor)
   {
      Block[][] clonedBlocks = _format.createEmptyBlocks();
      
      int rows = _format.getRows();
      int cols = _format.getCols();
      
      for(int row = 0; row < rows; ++row)
      {
         for(int col = 0; col < cols; ++col)
         {
            if(hasBlockAt(row, col))
            {
               clonedBlocks[row][col] = new DefaultBlock(newColor);
            }
         }
      }
      
      return new DefaultTile(clonedBlocks, _format);
   }
   
   public DefaultTile turnClockWise()
   {
      Format turnedFormat = _format.turn();
      Block[][] turnedBlocks = turnedFormat.createEmptyBlocks();

      int rows = _format.getRows();
      int cols = _format.getCols();
      
      for(int row = 0; row < rows; ++row)
      {
         for(int col = 0; col < cols; ++col)
         {
            turnedBlocks[col][rows - row - 1] = _blocks[row][col];
         }
      }
      
      return new DefaultTile(turnedBlocks, turnedFormat);
   }
   
   public DefaultTile turnAntiClockWise()
   {
      Format turnedFormat = _format.turn();
      Block[][] turnedBlocks = turnedFormat.createEmptyBlocks();

      int rows = _format.getRows();
      int cols = _format.getCols();
      
      for(int row = 0; row < rows; ++row)
      {
         for(int col = 0; col < cols; ++col)
         {
            turnedBlocks[cols - col - 1][row] = _blocks[row][col];
         }
      }
      
      return new DefaultTile(turnedBlocks, turnedFormat);
   }
   
}
