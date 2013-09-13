package de.javagimmicks.games.jotris.model.impl;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Color;

public class DefaultBlock implements Block
{
   private final Color _color;

   public DefaultBlock(Color oColor)
   {
      _color = oColor;
   }

   public Color getColor()
   {
      return _color;
   }

}
