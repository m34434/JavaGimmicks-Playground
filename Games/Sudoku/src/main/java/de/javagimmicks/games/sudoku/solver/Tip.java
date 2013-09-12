package de.javagimmicks.games.sudoku.solver;

import de.javagimmicks.games.sudoku.model.Field;
import de.javagimmicks.games.sudoku.model.Position;

public class Tip
{
   public static enum Type {EXCLUDE, UNIQUE};
   
   private final Position _position;
   private final Integer _value;
   private final Type _type;
   
   public Tip(Position position, Integer value, Type type)
   {
      _position = position;
      _value = value;
      _type = type;
   }
   
   public Tip(int row, int column, Integer value, Type type)
   {
      this(new Position(row, column), value, type);
   }
   
   public void applyTo(Field field)
   {
      field.setValue(_position, _value);
   }

   public Position getPosition()
   {
      return _position;
   }

   public Integer getValue()
   {
      return _value;
   }
   
   public Type getType()
   {
      return _type;
   }
   
   public String toString()
   {
      return new StringBuilder()
         .append(_position)
         .append(": ")
         .append(_value)
         .append(" (")
         .append(_type)
         .append(")")
         .toString();
   }
}
