package de.javagimmicks.games.sudoku;

import static de.javagimmicks.games.sudoku.util.FieldBuilder.N;

import org.junit.Test;

import de.javagimmicks.games.sudoku.model.Field;
import de.javagimmicks.games.sudoku.solver.Solver;
import de.javagimmicks.games.sudoku.solver.Tip;
import de.javagimmicks.games.sudoku.util.FieldBuilder;
import de.javagimmicks.games.sudoku.util.FieldRenderer;

public class SudokuTest
{
   @Test
   public void testField1()
   {
      findSolution(createTestField1());
   }

   @Test
   public void testField2()
   {
      findSolution(createTestField2());
   }

   @Test
   public void testField5()
   {
      findSolution(createTestField5());
   }

   private static void findSolution(Field f)
   {
      final FieldRenderer fr = new FieldRenderer(f);
      final Solver s = new Solver(f);
      
      while(true)
      {
         System.out.println(fr);
         
         Tip tip = s.getNextTip();
         if(tip == null)
         {
            break;
         }

         tip.applyTo(f);
         
         System.out.println(tip);
         System.out.println();
      }
      
      System.out.println();
      System.out.println("Solved: " + s.isFieldSolved());
      System.out.println();
   }
   
   private static Field createTestField1()
   {
      return new FieldBuilder(3)
         .append(6, N, N, 1, N, 4, 9, N, 2)
         .append(N, N, 9, N, N, N, N ,N ,N)
         .append(N, N, N, N, 2, 8, 6, N, N)
         .append(2, N, 1, N, N, N, 8, 6, N)
         .append(N, N, N, N, 6, N, N, N, N)
         .append(N, 9, 6, N, N, N, 5, N, 1)
         .append(N, N, 2, 8, 3, N, N, N, N)
         .append(N, N, N, N, N, N, 1, N, N)
         .append(3, N, 5, 7, N, 6, N, N, 4)
         .getField();
   }

   private static Field createTestField2()
   {
      return new FieldBuilder(2)
         .append(N, N, N, 4)
         .append(3, N, N, N)
         .append(N, 1, N, N)
         .append(N, N, N, 2)
         .getField();
   }

   private static Field createTestField3()
   {
      return new FieldBuilder(3)
         .append(N, N, N, 5, 7, N, N, 9, N)
         .append(N, 3, N, N, N, N, 2 ,7 ,N)
         .append(N, 9, 1, N, N, N, 8, N, N)
         .append(5, N, N, 3, 8, N, N, N, 6)
         .append(4, N, N, N, N, N, N, N, 7)
         .append(8, N, N, N, 6, 2, N, N, 5)
         .append(N, N, 2, N, N, N, 5, 1, N)
         .append(N, 6, 7, N, N, N, N, 3, N)
         .append(N, 5, N, N, 4, 9, N, N, N)
         .getField();
   }

   private static Field createTestField4()
   {
      return new FieldBuilder(3)
         .append(N, N, N, 3, 1, N, 9, N, N)
         .append(N, 3, N, N, N, N, 7 ,N ,N)
         .append(N, 4, N, N, 2, 8, 6, N, N)
         .append(2, N, N, 4, N, 9, N, N, 7)
         .append(6, N, N, N, N, N, N, N, 5)
         .append(7, N, N, 1, N, 3, N, N, 8)
         .append(N, N, 9, 6, 7, N, N, 3, N)
         .append(N, N, 1, N, N, N, N, 2, N)
         .append(N, N, 8, N, 3, 5, N, N, N)
         .getField();
   }

   private static Field createTestField5()
   {
      return new FieldBuilder(3)
         .append(N, 9, N, 6, N, N, N, N, N)
         .append(N, N, 4, 8, 9, N, N ,N ,2)
         .append(N, N, N, N, 5, 3, N, 9, N)
         .append(N, N, 1, N, N, N, N, 2, 8)
         .append(N, 7, 8, N, N, N, 4, 6, N)
         .append(3, 5, N, N, N, N, 1, N, N)
         .append(N, 1, N, 5, 2, N, N, N, N)
         .append(5, N, N, N, 8, 4, 2, N, N)
         .append(N, N, N, N, N, 1, N, 5, N)
         .getField();
   }

}
