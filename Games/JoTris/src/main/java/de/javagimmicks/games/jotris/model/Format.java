package de.javagimmicks.games.jotris.model;

/**
 * Determines the format of any {@link BlockGroup} which means the number of rows and columns.
 * @see BlockGroup
 */
public interface Format
{
   /**
    * Returns the number of rows of this {@link Format}.
    * @return the number of rows of this {@link Format}
    */
   public int getRows();

   /**
    * Returns the columns of rows of this {@link Format}.
    * @return the columns of rows of this {@link Format}
    */
   public int getCols();

   /**
    * Creates a new {@link Format} which has the same number of columns than this {@link Format} has rows and vice versa.
    * @return the resulting turned {@link Format}
    */
   public Format turn();

   /**
    * Creates an empty two-dimensional array of {@link Block}s fitting to this {@link Format}.
    * @return an empty two-dimensional array of {@link Block}s fitting to this {@link Format}
    * @see Block
    */
   public Block[][] createEmptyBlocks();
}