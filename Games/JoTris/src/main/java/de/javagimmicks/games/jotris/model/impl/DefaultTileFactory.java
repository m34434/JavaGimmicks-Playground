package de.javagimmicks.games.jotris.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Color;
import de.javagimmicks.games.jotris.model.Tile;
import de.javagimmicks.games.jotris.model.TileFactory;

public class DefaultTileFactory implements TileFactory
{
   private final ArrayList<Tile> _prototypes = new ArrayList<Tile>();
   private final Random _random = new Random();

   private int _colorIndex;
   
   public static TileFactory getDefaultInstance()
   {
      TileFactory result = new DefaultTileFactory();

      result.addPrototype(createRightCorner(null));
      result.addPrototype(createLeftCorner(null));
      result.addPrototype(createSquare(null));
      result.addPrototype(createPodium(null));
      result.addPrototype(createLine(null));
      result.addPrototype(createRightStair(null));
      result.addPrototype(createLeftStair(null));
      
      return result;
   }
   
   public List<Tile> getPrototypes()
   {
      return Collections.unmodifiableList(_prototypes);
   }
   
   public void addPrototype(Tile tile)
   {
      _prototypes.add(tile);
   }
   
   public Tile nextTile()
   {
      Tile nextTile = _prototypes.get(_random.nextInt(_prototypes.size())).clone(Color.values()[_colorIndex]);
      
      _colorIndex = (_colorIndex + 1) % Color.values().length;      
      
      return nextTile;
   }
   
   public static DefaultTile createRightCorner(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createLeftCorner(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createSquare(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 2);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createPodium(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createLine(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(1, 4);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      blocks[0][3] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
   
   public static DefaultTile createRightStair(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[1][0] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[0][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }

   public static DefaultTile createLeftStair(Color color)
   {
      DefaultFormat format = DefaultFormat.fromRowsAndCols(2, 3);
      
      Block[][] blocks = format.createEmptyBlocks();
      
      blocks[0][0] = new DefaultBlock(color);
      blocks[0][1] = new DefaultBlock(color);
      blocks[1][1] = new DefaultBlock(color);
      blocks[1][2] = new DefaultBlock(color);
      
      return new DefaultTile(blocks, format);
   }
}
