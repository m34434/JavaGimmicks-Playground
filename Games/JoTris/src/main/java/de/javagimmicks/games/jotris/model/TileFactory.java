package de.javagimmicks.games.jotris.model;

import java.util.List;

/**
 * Represents a factory class that creates {@link Tile} instances
 * by random.
 * <p>
 * The factory is intended to work based on the "Prototype" design pattern.
 * @see Tile
 */
public interface TileFactory
{
   /**
    * Returns the {@link List} of {@link Tile} prototype this {@link TileFactory} contains.
    * @return the {@link List} of {@link Tile} prototype this {@link TileFactory} contains
    */
   public List<Tile> getPrototypes();

   /**
    * Adds a new {@link Tile} prototype to this {@link TileFactory}.
    * @param tile the {@link Tile} prototype to add
    */
   public void addPrototype(Tile tile);

   /**
    * Creates a new {@link Tile} by randomly cloning one of its prototypes
    * with a sequentially changing {@link Color}.
    * @return the resulting {@link Tile}
    */
   public Tile nextTile();
}