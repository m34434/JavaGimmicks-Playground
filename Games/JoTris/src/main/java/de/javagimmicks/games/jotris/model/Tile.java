package de.javagimmicks.games.jotris.model;

/**
 * Represents a simple tile which falls down in the
 * {@link Field} of a JoTris game.
 */
public interface Tile extends BlockGroup, Cloneable
{
   /**      
    * Create a clone of this {@link Tile}.
    * @return the resulting clone
    */
   public Tile clone();
   
   /**
    * Create a clone of this {@link Tile} with all {@link Block}s having the specified {@link Color}.
    * @param newColor the {@link Color} that all {@link Block}s of the clone should have
    * @return the resulting clone
    */
   public Tile clone(Color newColor);
   
   /**
    * Creates a clone of this {@link Tile} which is turned clockwise.
    * @return a clone of this {@link Tile} which is turned clockwise
    */
   public Tile turnClockWise();

   /**
    * Creates a clone of this {@link Tile} which is turned anti-clockwise.
    * @return a clone of this {@link Tile} which is turned anti-clockwise
    */
   public Tile turnAntiClockWise();
}