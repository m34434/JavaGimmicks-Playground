package de.javagimmicks.games.sudoku.util;

import java.util.Iterator;

import de.javagimmicks.games.sudoku.model.Field;
import de.javagimmicks.games.sudoku.model.Position;

public class FieldBuilder
{  
   public static final Integer N = null;
   
   private final Field _field;
   private final Iterator<Position> _positionIterator;
   
   public FieldBuilder(int base)
   {
      _field = new Field(base);
      _positionIterator = _field.getAllPositions().iterator();
   }
   
   public FieldBuilder append(Integer... values)
   {
      for(Integer value : values)
      {
         _field.setValue(_positionIterator.next(), value);
      }
      
      return this;
   }
   
   public Field getField()
   {
      return _field;
   }
}
