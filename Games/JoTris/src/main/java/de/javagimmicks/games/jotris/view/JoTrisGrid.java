package de.javagimmicks.games.jotris.view;

import javax.swing.JOptionPane;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.GameEvent;
import de.javagimmicks.games.jotris.model.GridEvent;
import de.javagimmicks.games.jotris.model.JoTrisModel;
import de.javagimmicks.games.jotris.model.JoTrisModelListener;

/**
 * Renders a {@link JoTrisModel} by displaying all its {@link Block}s
 * (regarding their color) in a grid. 
 */
public class JoTrisGrid extends JoTrisGridComponent
{
   private static final long serialVersionUID = 5355327276585139536L;

   private final JoTrisModelListener _listener = new DefaultJoTrisModelListener()
   {
      protected void _gridEventOccured(GridEvent e)
      {
         updateBlocks(e.getRow(), e.getCol(), e.getRowCount(), e.getColCount());
      }

      @Override
      protected void _gameEventOccured(GameEvent e)
      {
         if(e.isGameOver())
         {
            JOptionPane.showMessageDialog(JoTrisGrid.this, "Game over!");
         }
      }
   };

   private JoTrisModel _model;
   
   /**
    * Creates a new {@link JoTrisGrid} for the given {@link JoTrisModel}
    * with the given block size.
    * @param model the {@link JoTrisModel} containing the {@link Block}s to display
    * @param blockSize the size that a single rendered {@link Block} should have
    */
   public JoTrisGrid(JoTrisModel model, int blockSize)
   {
      super(blockSize);
      
      setModel(model);
   }
   
   /**
    * Creates a new {@link JoTrisGrid} for the given {@link JoTrisModel}
    * with a standard block size of 20.
    * @param model the {@link JoTrisModel} containing the {@link Block}s to display
    */
   public JoTrisGrid(JoTrisModel model)
   {
      setModel(model);
   }
   
   /**
    * Sets a new {@link JoTrisModel} for this {@link JoTrisGrid}.
    * @param model the new {@link JoTrisModel} for this {@link JoTrisGrid}
    */
   public void setModel(JoTrisModel model)
   {
      if(_model != null)
      {
         _model.removeJoTrisModelListener(_listener);
      }
      
      _model = model;
      if(_model == null)
      {
         return;
      }
      
      _model.addJoTrisModelListener(_listener);

      super.resetGrid();
   }

   @Override
   protected Block getBlockAt(int row, int col)
   {
      return _model.getBlockAt(row, col);
   }

   @Override
   protected Format getGridFormat()
   {
      return _model.getFieldFormat();
   }
}
