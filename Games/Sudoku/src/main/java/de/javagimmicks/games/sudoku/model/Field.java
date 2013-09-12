package de.javagimmicks.games.sudoku.model;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Field implements Serializable
{  
   private static final long serialVersionUID = 4391605052808059757L;

   private final int _base;
   
   private final Integer[][] _values;

   private final PositionGroup[] _rowGroups;
   private final PositionGroup[] _columnGroups;
   private final PositionGroup[] _blockGroups;
   
   private final List<FieldListener> _listeners = new ArrayList<FieldListener>();
   
   public Field(int base)
   {
      if(base < 2)
      {
         throw new IllegalArgumentException("Base must be 2 or greater!");
      }
      
      _base = base;

      final int dimSize = _base * _base;
      _values = new Integer[dimSize][dimSize];
      
      _rowGroups = fillRowGroups(dimSize);
      _columnGroups = fillColumnGroups(dimSize);
      _blockGroups = fillBlockGroups(_base, dimSize);
   }

   public int getBase()
   {
      return _base;
   }
   
   public Set<Integer> getAllValues()
   {
      final Set<Integer> result = new TreeSet<Integer>();
      
      for(int i = 1; i <= _base * _base; ++i)
      {
         result.add(i);
      }
      
      return result;
   }
   
   public Set<Position> getAllPositions()
   {
      return new PositionSet();
   }
   
   public Integer getValue(int row, int column)
   {
      return _values[row][column];
   }
   
   public Integer getValue(Position p)
   {
      return getValue(p.getRow(), p.getColumn());
   }
   
   public void setValue(Position p, Integer value)
   {
      if(value != null && (value < 1 || value > _base * _base))
      {
         throw new IllegalArgumentException("Illegal value " + value);
      }
      
      final Integer oldValue = _values[p.getRow()][p.getColumn()];
      _values[p.getRow()][p.getColumn()] = value;
      
      for(FieldListener l : _listeners)
      {
         l.valueChanged(p, oldValue, value);
      }
   }
   
   public void setValue(int row, int column, Integer value)
   {
      setValue(new Position(row, column), value);
   }
   
   public PositionGroup[] getPositionGroups(int row, int column)
   {
      final int blockNum = (row / _base) * _base + (column / _base);
      
      final PositionGroup[] result = new PositionGroup[3];
      
      result[0] = _rowGroups[row];
      result[1] = _columnGroups[column];
      result[2] = _blockGroups[blockNum];
      
      return result;
   }
   
   public PositionGroup[] getPositionGroups(Position p)
   {
      return getPositionGroups(p.getRow(), p.getColumn());
   }

   public void addFieldListener(FieldListener l)
   {
      _listeners.add(l);
   }
   
   public void removeFieldListener(FieldListener l)
   {
      _listeners.remove(l);
   }
   
   private PositionGroup[] fillRowGroups(int dimSize)
   {
      final PositionGroup[] result = new PositionGroup[dimSize];
      
      for(int row = 0; row < dimSize; ++row)
      {
         final List<Position> positions = new ArrayList<Position>(dimSize);
         
         for(int column = 0; column < dimSize; ++column)
         {
            positions.add(new Position(row, column));
         }
         
         result[row] = new PositionGroup(this, positions);
      }
      
      return result;
   }

   private PositionGroup[] fillColumnGroups(int dimSize)
   {
      final PositionGroup[] result = new PositionGroup[dimSize];
      
      for(int column = 0; column < dimSize; ++column)
      {
         final List<Position> positions = new ArrayList<Position>(dimSize);
         
         for(int row = 0; row < dimSize; ++row)
         {
            positions.add(new Position(row, column));
         }
         
         result[column] = new PositionGroup(this, positions);
      }
      
      return result;
   }
   
   private PositionGroup[] fillBlockGroups(int base, int dimSize)
   {
      final PositionGroup[] result = new PositionGroup[dimSize];

      for(int blockRow = 0; blockRow < base; ++blockRow)
      {
         final int rowStart = blockRow * base;
         
         for(int blockColumn = 0; blockColumn < base; ++blockColumn)
         {
            final int columnStart = blockColumn * base;
            
            final List<Position> positions = new ArrayList<Position>(dimSize);
            
            for(int row = rowStart; row < rowStart + base; ++row)
            {
               for(int column = columnStart; column < columnStart + base; ++column)
               {
                  positions.add(new Position(row, column));
               }
            }

            final int index = blockRow * base + blockColumn;
            result[index] = new PositionGroup(this, positions);
         }
      }
      
      return result;
   }
   
   private class PositionSet extends AbstractSet<Position>
   {
      public Iterator<Position> iterator()
      {
         return new Iterator<Position>()
         {
            private Position _position = new Position(0, 0);

            public boolean hasNext()
            {
               return _position != null;
            }

            public Position next()
            {
               if(!hasNext())
               {
                  throw new IllegalStateException();
               }
               
               final Position result = _position;
               
               final int row = _position.getRow();
               final int column = _position.getColumn();
               
               if(column == _base * _base - 1)
               {
                  if(row == _base * _base - 1)
                  {
                     _position = null;
                  }
                  else
                  {
                     _position = new Position(row + 1, 0);
                  }
               }
               else
               {
                  _position = new Position(row, column + 1);
               }
               
               return result;
            }

            public void remove()
            {
               throw new UnsupportedOperationException();
            }
         };
      }
      
      @Override
      public int size()
      {
         return _base * _base * _base * _base;
      }
   }
}
