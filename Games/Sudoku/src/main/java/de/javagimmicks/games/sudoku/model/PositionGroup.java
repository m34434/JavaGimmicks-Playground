package de.javagimmicks.games.sudoku.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class PositionGroup implements Serializable
{  
   private static final long serialVersionUID = -4067375102854067531L;

   private final Field _field;
   private final Set<Position> _positions = new TreeSet<Position>();
   
   public PositionGroup(Field field, Collection<Position> positions)
   {
      _field = field;
      _positions.addAll(positions);
   }
   
   public Set<Integer> getValues()
   {
      final Set<Integer> result = new HashSet<Integer>();
      
      for(Position p : _positions)
      {
         final Integer value = _field.getValue(p);
         
         if(value != null)
         {
            result.add(value);
         }
      }
      
      return result;
   }
   
   public Set<Position> getPositions()
   {
      return Collections.unmodifiableSet(_positions);
   }
   
   public String toString()
   {
      return _positions.toString();
   }
}
