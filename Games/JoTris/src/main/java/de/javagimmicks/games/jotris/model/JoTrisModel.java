package de.javagimmicks.games.jotris.model;

/**
 * Represents all information about a JoTris game.
 */
public interface JoTrisModel
{
   /**
    * Allows to add a {@link JoTrisModelListener} to this {@link JoTrisModel}
    * which is capable of receiving special events that can happen within the game.
    * @param listener the {@link JoTrisModelListener} to add
    * @see JoTrisModelListener
    */
   public void addJoTrisModelListener(JoTrisModelListener listener);

   /**
    * Removes a specified {@link JoTrisModelListener} from this {@link JoTrisModel}
    * @param listener the {@link JoTrisModelListener} to remove
    * @see JoTrisModelListener
    */
   public void removeJoTrisModelListener(JoTrisModelListener listener);

   /**
    * Returns the {@link Format} for the {@link Field}s this {@link JoTrisModel} will generate.
    * @return the {@link Format} for the {@link Field}s this {@link JoTrisModel} will generate
    * @see Format
    */
   public Format getFieldFormat();
   
   /**
    * Returns the {@link Block} at the given position.
    * <p>
    * The resulting {@link Block} is either part of the internal {@link Field}
    * or belongs to the current {@link Tile}.
    * @param row the row to get the {@link Block} from
    * @param col the column to get the {@link Block} from
    * @return the {@link Block} at the given position
    * @see Block
    */
   public Block getBlockAt(int row, int col);
   
   /**
    * Returns the {@link Tile} which will come next.
    * @return the {@link Tile} which will come next.
    * @see Tile
    */
   public Tile getNextTile();

//   /**
//    * Returns the current {@link Field} for this game.
//    * @return the current {@link Field} for this game
//    */
//   public Field getField();
//
//
//
//   /**
//    * Returns the current {@link Tile}.
//    * @return  the current {@link Tile} or null if there is none
//    */
//   public Tile getCurrentTile();
//   
//   /**
//    * Returns the top left row of the current {@link Tile}.
//    * @return the top left row of the current {@link Tile}
//    */
//   public int getCurrentTileRow();
//
//   /**
//    * Returns the top left column of the current {@link Tile}.
//    * @return the top left column of the current {@link Tile}
//    */
//   public int getCurrentTileCol();

   
   /**
    * Start a new JoTris game, if no one is already running.
    */
   public void startGame();

   /**
    * Stops the current JoTris game, if one is running.
    */
   public void stopGame();
   
   /**
    * Returns if there is currently running a JoTris game.
    * @return if there is currently running a JoTris game
    */
   public boolean isGameRunning();
   
   /**
    * Returns the reached score of the current JoTris game.
    * @return the reached score of the current JoTris game
    */
   public long getScore();

   /**
    * Returns the number of rows killed so far in the current JoTris game.
    * @return the number of rows killed so far in the current JoTris game
    */
   public int getRowsCompleted();
   
   /**
    * Sets the interval for {@link Tile} auto-down-moving.
    * @param millis the number of milliseconds between the automatic movements of the current {@link Tile}
    */
   public void setAutoMoveInterval(long millis);
   
   /**
    * Sets the state for {@link Tile} auto-down-moving.
    * @param enabled if auto-down-moving is enabled
    */
   public void setAutoMoveEnabled(boolean enabled);

   /**
    * Returns if auto-down-moving is enabled.
    * @return if auto-down-moving is enabled
    */
   public boolean isAutoMoveEnabled();

   /**
    * Moves the current {@link Tile} one level down.
    * <p>
    * If the {@link Tile} cannot be moved down, it is permanently added to the {@link Field} at its
    * current position and a new one is dropped into the top row of the {@link Field}.
    */
   public void tileMoveDown();

   /**
    * Moves the current {@link Tile} to the left, if possible.
    */
   public void tileMoveLeft();

   /**
    * Moves the current {@link Tile} to the right, if possible.
    */
   public void tileMoveRight();

   /**
    * Turns the current {@link Tile} in anti-clockwise direction, if possible.
    */
   public void tileTurnAntiClockWise();

   /**
    * Turns the current {@link Tile} in clockwise direction, if possible.
    */
   public void tileTurnClockWise();
}