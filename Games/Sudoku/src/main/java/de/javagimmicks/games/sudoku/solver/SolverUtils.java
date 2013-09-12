package de.javagimmicks.games.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import de.javagimmicks.games.sudoku.model.Field;

public class SolverUtils
{
   public static boolean canSolve(Field field)
   {
      final Solver solver = new Solver(field);
      final List<Tip> tips = new ArrayList<Tip>();
      
      for(Tip tip = solver.getNextTip(); tip != null; tip = solver.getNextTip())
      {
         tips.add(tip);
         
         tip.applyTo(field);
      }
      
      final boolean result = solver.isFieldSolved();
      
      for(Tip tip : tips)
      {
         field.setValue(tip.getPosition(), null);
      }
      
      return result;
   }
}
