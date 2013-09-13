package de.javagimmicks.games.jotris.model.impl;

import java.util.List;

import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.Tile;

public class TileUtil
{  
   public static Format getCommonMinimalFormat(List<Tile> tiles)
   {
      int maxCols = 0;
      int maxRows = 0;
      
      for(Tile tile : tiles)
      {
         Format tileFormat = tile.getFormat();
         
         maxCols = Math.max(maxCols, tileFormat.getCols());
         maxRows = Math.max(maxRows, tileFormat.getRows());
      }
      
      return DefaultFormat.fromRowsAndCols(maxRows, maxCols);
   }
}
