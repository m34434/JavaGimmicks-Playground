package de.javagimmicks.games.jotris.model;

/**
 * Represents the change of an area of the grid of a {@link JoTrisModel}
 * (which means a change to the {@link Field} or current {@link Tile} inside there).
 * <p>
 * The following information is provided
 * <ul>
 * <li>The top left position of the change</li>
 * <li>The number of rows affected</li>
 * <li>The number of columns affected</li> 
 * </ul>
 */
public class GridEvent
{
   private final JoTrisModel _model;
   private final int _row;
   private final int _col;
   private final int _rowCount;
   private final int _colCount;
   
   /**
    * Creates a new {@link GridEvent} from the given model and grid information.
    * @param model the {@link JoTrisModel} causing this {@link GameEvent}
    * @param row the row of the top left corner of the changed area
    * @param col the column of the top left corner of the changed area
    * @param rowCount the number of rows the changed area consists of
    * @param colCount the number of columns the changed area consists of
    */
   public GridEvent(JoTrisModel model, int row, int col, int rowCount,
         int colCount)
   {
      _model = model;
      
      _row = row;
      _col = col;
      _rowCount = rowCount;
      _colCount = colCount;
   }
   
   /**
    * Returns the {@link JoTrisModel} causing this {@link GameEvent}.
    * @return the {@link JoTrisModel} causing this {@link GameEvent}
    */
   public JoTrisModel getModel()
   {
      return _model;
   }

   /**
    * Returns the row of the top left corner of the changed area.
    * @return the row of the top left corner of the changed area
    */
   public int getRow()
   {
      return _row;
   }
   
   /**
    * Returns the column of the top left corner of the changed area.
    * @return the column of the top left corner of the changed area
    */
   public int getCol()
   {
      return _col;
   }
   
   /**
    * Returns the number of rows the changed area consists of.
    * @return the number of rows the changed area consists of
    */
   public int getRowCount()
   {
      return _rowCount;
   }
   
   /**
    * Returns the number of columns the changed area consists of.
    * @return the number of columns the changed area consists of
    */
   public int getColCount()
   {
      return _colCount;
   }
}
