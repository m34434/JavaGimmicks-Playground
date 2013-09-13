package de.javagimmicks.games.jotris.model;

/**
 * Defines a JoTris field, where {@link Tile}s can be added at different locations.
 * @see Tile
 */
public interface Field extends BlockGroup
{
   /**
    * Adds a given {@link Tile} at the given position.
    * @param row the row where to add the given {@link Tile}
    * @param col the column where to add the given {@link Tile}
    * @param tile the {@link Tile} to add
    */
   public void addTileAt(int row, int col, Tile tile);

   /**
    * Check, if a given {@link Tile} would fit to this {@link Field} at the given position.
    * @param row the row where the given {@link Tile} should fit
    * @param col the column where the given {@link Tile} should fit
    * @param tile the {@link Tile} to check for fitting
    */
   public boolean isTileFittingAt(int row, int col, Tile tile);

   /**
    * Kills the specified row of this {@link Field} allowing all {@link Block}s above it to fall down one level.
    * @param row the number of the row to kill
    */
   public void killRow(int row);
   
   /**
    * Checks, if the given row contains only {@link Block}s.
    * @param row the row to check
    * @return if the row contains only {@link Block}s
    */
   public boolean isRowFull(int row);
}