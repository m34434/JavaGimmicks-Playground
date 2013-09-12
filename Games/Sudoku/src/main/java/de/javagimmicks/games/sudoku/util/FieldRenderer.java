package de.javagimmicks.games.sudoku.util;

import java.text.NumberFormat;

import de.javagimmicks.games.sudoku.model.Field;

public class FieldRenderer
{  
   private static final char TOSTRING_SEPARATOR = 'x';
   private static final String TOSTRING_LINESEP = System.getProperty("line.separator");
   
   private final Field _field;
   
   public FieldRenderer(Field field)
   {
      _field = field;
   }

   public String toString()
   {
      final int base = _field.getBase();
      final int len = String.valueOf(base * base).length();
      
      final NumberFormat format = NumberFormat.getIntegerInstance();
      format.setMinimumIntegerDigits(len);
      format.setMaximumIntegerDigits(len);
      format.setMaximumFractionDigits(0);
      
      final StringBuilder b = new StringBuilder();
      for(int rowBlock = 0; rowBlock < base; ++rowBlock)
      {
         fillLine(len, base, b);
         finishLine(b);

         for(int rowInBlock = 0; rowInBlock < base; ++rowInBlock)
         {
            final int row = rowBlock * base + rowInBlock;
            
            fillLine(len, base, b);
            finishLine(b);
            
            for(int columnBlock = 0; columnBlock < base; ++columnBlock)
            {
               b.append(TOSTRING_SEPARATOR);
               
               for(int columnInBlock = 0; columnInBlock < base; ++columnInBlock)
               {
                  final int column = columnBlock * base + columnInBlock;
                  final Integer value = _field.getValue(row, column);
                  
                  b.append(TOSTRING_SEPARATOR).append(' ');
                  if(value == null)
                  {
                     for(int i = 0; i < len; ++i)
                     {
                        b.append(' ');
                     }
                  }
                  else
                  {
                     b.append(format.format(value));
                  }
                  b.append(' ');
               }
            }

            b.append(TOSTRING_SEPARATOR);
            b.append(TOSTRING_SEPARATOR);
            finishLine(b);
         }
         
      }
      
      fillLine(len, base, b);
      finishLine(b);
      fillLine(len, base, b);
      
      return b.toString();
   }

   private static void fillLine(int len, int base, StringBuilder b)
   {
      final int lineLen = base * base * (len + 3) + base + 2;
      for(int i = 0; i < lineLen; ++i)
      {
         b.append(TOSTRING_SEPARATOR);
      }
   }

   private static void finishLine(StringBuilder b)
   {
      b.append(TOSTRING_LINESEP);
   }
   

}
