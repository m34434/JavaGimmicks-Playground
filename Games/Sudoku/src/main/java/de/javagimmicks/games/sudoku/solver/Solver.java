package de.javagimmicks.games.sudoku.solver;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import de.javagimmicks.games.sudoku.model.Field;
import de.javagimmicks.games.sudoku.model.FieldListener;
import de.javagimmicks.games.sudoku.model.Position;
import de.javagimmicks.games.sudoku.model.PositionGroup;
import de.javagimmicks.games.sudoku.solver.Tip.Type;

public class Solver
{  
   private final Field _field;
   private final Set<?>[][] _candidates;

   public Solver(Field field)
   {
      _field = field;
      
      final int base = _field.getBase();
      _candidates = new Set<?>[base * base][base * base];
      
      _field.addFieldListener(new SolverFieldListener());
      
      fillCandidates();
   }
   
   public boolean isFieldSolved()
   {
      for(Position p : _field.getAllPositions())
      {
         if(_field.getValue(p) == null)
         {
            return false;
         }
      }
      
      return true;
   }
   
   public Tip getNextTip()
   {
      Tip result = findByExclusion();
      return result == null ? findGroupUniques() : result;
   }
   
   public Set<Integer> getCandidates(Position p)
   {
      return Collections.unmodifiableSet(getCandidatesInternal(p));
   }
   
   @SuppressWarnings("unchecked")
   private Set<Integer> getCandidatesInternal(Position p)
   {
      return (Set<Integer>)_candidates[p.getRow()][p.getColumn()];
   }
   
   private void fillCandidates()
   {
      for(Position p : _field.getAllPositions())
      {
         fillCandidate(p, _field.getValue(p));
      }
   }

   private void fillCandidate(Position p, Integer value)
   {
      if(value != null)
      {
         _candidates[p.getRow()][p.getColumn()] = new TreeSet<Integer>();
      }
      else
      {
         
         final PositionGroup[] positionGroups = _field.getPositionGroups(p);
         
         Set<Integer> result = _field.getAllValues();
         
         result.removeAll(positionGroups[0].getValues());
         result.removeAll(positionGroups[1].getValues());
         result.removeAll(positionGroups[2].getValues());

         _candidates[p.getRow()][p.getColumn()] = result;
      }
   }

   private Tip findByExclusion()
   {
      for(Position p : _field.getAllPositions())
      {
         final Set<Integer> candidates = getCandidatesInternal(p);
         
         if(candidates.size() == 1)
         {
            return new Tip(p, candidates.iterator().next(), Type.EXCLUDE);
         }
      }
      
      return null;
   }
   
   private Tip findGroupUniques()
   {
      for(Position p : _field.getAllPositions())
      {
         final Set<Integer> candidates = getCandidatesInternal(p);
         
         if(candidates.size() <= 1)
         {
            continue;
         }
         
         for(Integer value : candidates)
         {
            if(isValueUniqueInAnyGroup(value, p))
            {
               return new Tip(p, value, Type.UNIQUE);
            }
         }
      }
      
      return null;
   }
   
   private boolean isValueUniqueInAnyGroup(Integer value, Position p)
   {
      for(PositionGroup positionGroup : _field.getPositionGroups(p))
      {
         if(isValueUniqueInGroup(value, p, positionGroup))
         {
            return true;
         }
      }
      
      return false;
   }
   
   private boolean isValueUniqueInGroup(Integer value, Position p, PositionGroup positionGroup)
   {
      for(Position curPos : positionGroup.getPositions())
      {
         if(curPos.equals(p))
         {
            continue;
         }
         
         if(getCandidatesInternal(curPos).contains(value))
         {
            return false;
         }
      }
      
      return true;
   }
   
   private class SolverFieldListener implements FieldListener
   {
      public void valueChanged(Position p, Integer oldValue, Integer newValue)
      {
         fillCandidate(p, newValue);

         for(PositionGroup group : _field.getPositionGroups(p))
         {
            for(Position currentPosition : group.getPositions())
            {
               if(p.equals(currentPosition))
               {
                  continue;
               }
               else if(newValue != null)
               {
                  getCandidatesInternal(currentPosition).remove(newValue);
               }
               else
               {
                  getCandidatesInternal(currentPosition).add(oldValue);
               }
            }
         }
      }
   }
}
