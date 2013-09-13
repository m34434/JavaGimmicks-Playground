package de.javagimmicks.games.jotris.model.impl;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Color;
import de.javagimmicks.games.jotris.model.TileFactory;

public class QuinTrisTileFactory
{
   public static TileFactory getDefaultInstance()
   {
      TileFactory result = new DefaultTileFactory();

      result.addPrototype(createBlockLeft(null));
      result.addPrototype(createBlockRight(null));
      result.addPrototype(createCorner(null));
//      result.addPrototype(createCrossCenter(null));
      result.addPrototype(createCrossLeft(null));
      result.addPrototype(createCrossRight(null));
      result.addPrototype(createLine(null));
      result.addPrototype(createLLeft(null));
      result.addPrototype(createLRight(null));
      result.addPrototype(createLongStairLeft(null));
      result.addPrototype(createLongStairRight(null));
      result.addPrototype(createPistolLeft(null));
      result.addPrototype(createPistolRight(null));
      result.addPrototype(createStairs(null));
//      result.addPrototype(createStairsLeft(null));
//      result.addPrototype(createStairsRight(null));
      result.addPrototype(createT(null));
      result.addPrototype(createU(null));
      
      return result;
   }
   
   public static DefaultTile createCorner(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[2][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createBlockLeft(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createBlockRight(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createStairs(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[2][1] = new DefaultBlock(color);
      blocks[2][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createStairsLeft(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[2][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createStairsRight(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[2][0] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createCrossLeft(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[2][1] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createCrossCenter(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[2][1] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createCrossRight(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[2][1] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createLongStairLeft(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[1][3] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createLongStairRight(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][2] = new DefaultBlock(color);
      blocks[0][3] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createT(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(3, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[2][1] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createU(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createLLeft(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[1][3] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createLRight(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][3] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[1][3] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createPistolLeft(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[1][3] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createPistolRight(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][2] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[1][3] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createLine(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(1, 5);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      blocks[0][3] = new DefaultBlock(color);
      blocks[0][4] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
}
