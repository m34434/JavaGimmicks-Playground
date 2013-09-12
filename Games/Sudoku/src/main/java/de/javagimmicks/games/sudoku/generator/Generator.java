package de.javagimmicks.games.sudoku.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.javagimmicks.games.sudoku.model.Field;
import de.javagimmicks.games.sudoku.model.Position;
import de.javagimmicks.games.sudoku.solver.Solver;
import de.javagimmicks.games.sudoku.solver.SolverUtils;
import de.javagimmicks.games.sudoku.util.FieldRenderer;

public class Generator
{
   public static void main(String[] args)
   {
      final long now = System.currentTimeMillis();
      final Field field = new Generator().generateBruteForce(3);
      long duration = System.currentTimeMillis() - now;
      
      System.out.println(new FieldRenderer(field));
      System.out.println("Genration duration: " + duration + " milliseconds");
   }
   
   public Field generateBruteForce(int base)
   {
      final int size = base * base;
      final Field field = new Field(base);
      final Solver solver = new Solver(field); 
      
      final Iterator<Position> positionIterator = field.getAllPositions().iterator();
      
      // Fill the first row linearly with values starting from 1 and increasing by 1
      for(int i = 1; i <= size; ++i)
      {
         field.setValue(positionIterator.next(), i);
      }
      
      // Fill the rest of free positions into an ArrayList
      final List<Position> freePositions = new ArrayList<Position>();
      while(positionIterator.hasNext())
      {
         freePositions.add(positionIterator.next());
      }
      
      // Fill the rest of the free positions with allowed values
      if(!fillField(field, solver, freePositions, 0))
      {
         return null;
      }
      
      // Apply a transformation exchanging all values with different ones
      // (this mixes up the linear first row)
      final ArrayList<Integer> shuffledValues = new ArrayList<Integer>(field.getAllValues());
      Collections.shuffle(shuffledValues);

      for(Position p : field.getAllPositions())
      {
         field.setValue(p, shuffledValues.get(field.getValue(p) - 1));
      }
      
      cleanField(field);
      
      return field;
   }
   
   private static void cleanField(Field field)
   {
      final ArrayList<Position> positions = new ArrayList<Position>(field.getAllPositions());
      Collections.shuffle(positions);
      
      for(Position p : positions)
      {
         final Integer value = field.getValue(p);
         
         field.setValue(p, null);
         
         if(!SolverUtils.canSolve(field))
         {
            field.setValue(p, value);
            
            return;
         }
      }
   }

   private static boolean fillField(Field field, Solver solver, List<Position> freePositions, int index)
   {
      // If no more positions need to be filled, we have a result
      if(index == freePositions.size())
      {
         return true;
      }
      
      // Get the position to fill
      final Position p = freePositions.get(index);
      
      // Get all free values for this position and shuffle them
      final ArrayList<Integer> freeValues = new ArrayList<Integer>(solver.getCandidates(p));
      Collections.shuffle(freeValues);
      
      // For each free value, try to fill the current position with it and then
      // try to fill the rest of the field recursively
      for(Integer value : freeValues)
      {
         field.setValue(p, value);
         
         // If we found a solution, we can abort here
         if(fillField(field, solver, freePositions, index + 1))
         {
            return true;
         }
         
         field.setValue(p, null);
      }
      
      // None of the free values allowed to find a solution
      return false;
   }
}
