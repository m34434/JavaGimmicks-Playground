package de.javagimmicks.games.sudoku.model;

public interface FieldListener
{  
   public void valueChanged(Position p, Integer oldValue, Integer newValue);
}
