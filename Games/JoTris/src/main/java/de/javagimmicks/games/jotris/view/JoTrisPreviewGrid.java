package de.javagimmicks.games.jotris.view;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.GameEvent;
import de.javagimmicks.games.jotris.model.JoTrisModel;
import de.javagimmicks.games.jotris.model.JoTrisModelListener;
import de.javagimmicks.games.jotris.model.Tile;

public class JoTrisPreviewGrid extends JoTrisGridComponent
{
   private static final long serialVersionUID = -5623505671348682910L;

   private Format _gridFormat;
   private JoTrisModel _model;
   private Tile _previewTile;
   
   private final JoTrisModelListener _listener = new DefaultJoTrisModelListener()
   {
      @Override
      protected void _gameEventOccured(GameEvent e)
      {
         if(e.isTileChanged() || e.isGameStarted())
         {
            _previewTile = _model.getNextTile();
            updateAll();
         }
      }
   };
   
   public JoTrisPreviewGrid(JoTrisModel model, Format gridFormat)
   {
      _gridFormat = gridFormat;
      setModel(model);
   }

   public JoTrisPreviewGrid(JoTrisModel model, Format gridFormat, int blockSize)
   {
      super(blockSize);

      _gridFormat = gridFormat;
      setModel(model);
   }

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

   public Format getGridFormat()
   {
      return _gridFormat;
   }

   public void setGridFormat(Format format)
   {
      _gridFormat = format;
      
      resetGrid();
   }

   @Override
   protected Block getBlockAt(int row, int col)
   {
      if(_previewTile == null)
      {
         return null;
      }
      
      Format tileFormat = _previewTile.getFormat();
      if(row < tileFormat.getRows() && col < tileFormat.getCols())
      {
         return _previewTile.getBlockAt(row, col);
      }
      else
      {
         return null;
      }
   }
}
