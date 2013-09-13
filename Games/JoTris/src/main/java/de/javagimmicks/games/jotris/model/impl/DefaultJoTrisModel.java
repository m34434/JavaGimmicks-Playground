package de.javagimmicks.games.jotris.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Field;
import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.GameEvent;
import de.javagimmicks.games.jotris.model.GridEvent;
import de.javagimmicks.games.jotris.model.JoTrisModel;
import de.javagimmicks.games.jotris.model.JoTrisModelListener;
import de.javagimmicks.games.jotris.model.Tile;
import de.javagimmicks.games.jotris.model.TileFactory;
import de.javagimmicks.games.jotris.model.GameEvent.EventType;

public class DefaultJoTrisModel implements JoTrisModel
{
   private final List<JoTrisModelListener> _listeners = new ArrayList<JoTrisModelListener>();
   
   private final Format _fieldFormat;
   private final TileFactory _factory;
   
   private boolean _gameRunning = false;
   private long _score = 0L;
   private int _rowsCompleted;
   
   private Timer _autoMoveTimer;
   private long _autoMoveInterval = 1000L;
   
   protected Field _field;
   protected Tile _tile; 
   protected int _tileRow;
   protected int _tileCol;
   protected Tile _nextTile;
   
   public DefaultJoTrisModel(Format format, TileFactory factory)
   {
      _fieldFormat = format;
      _factory = factory;
      
      _field = new DefaultField(_fieldFormat);
      _nextTile = _factory.nextTile();
   }
   
   public DefaultJoTrisModel(Format format)
   {
      this(format, DefaultTileFactory.getDefaultInstance());
   }
   
   public DefaultJoTrisModel(int rows, int cols, TileFactory factory)
   {
      this(DefaultFormat.fromRowsAndCols(rows, cols), factory);
   }
   
   public DefaultJoTrisModel(int rows, int cols)
   {
      this(rows, cols, DefaultTileFactory.getDefaultInstance());
   }

   public boolean isAutoMoveEnabled()
   {
      return _autoMoveTimer != null;
   }

   public void setAutoMoveEnabled(boolean enabled)
   {
      if(isAutoMoveEnabled() == enabled)
      {
         return;
      }
      
      if(enabled)
      {
         startAutoMoveTimer();
      }
      else
      {
         stopAutoMoveTimer();
      }
   }

   public void setAutoMoveInterval(long millis)
   {
      _autoMoveInterval = millis;
      
      if(!isAutoMoveEnabled())
      {
         return;
      }
      
      if(_autoMoveInterval <= 0L)
      {
         stopAutoMoveTimer();
      }
      else
      {
         startAutoMoveTimer();
      }
   }
   
   public void addJoTrisModelListener(JoTrisModelListener listener)
   {
      _listeners.add(listener);
   }
   
   public void removeJoTrisModelListener(JoTrisModelListener listener)
   {
      _listeners.remove(listener);
   }

   public Format getFieldFormat()
   {
      return _fieldFormat;
   }
   
//   public Field getField()
//   {
//      return _field;
//   }
//
//   public int getCurrentTileRow()
//   {
//      return _tileRow;
//   }
//
//   public int getCurrentTileCol()
//   {
//      return _tileCol;
//   }
//
//   public Tile getCurrentTile()
//   {
//      return _tile;
//   }
   
   public Block getBlockAt(int row, int col)
   {
      Block result = _field.getBlockAt(row, col);
      
      return result != null ? result : getTileBlock(row, col);
   }

   public Tile getNextTile()
   {
      return _nextTile;
   }

   public void tileMoveDown()
   {
      if(!checkTileModification())
      {
         return;
      }
      
      int newRow = _tileRow + 1;
      
      if(!_field.isTileFittingAt(newRow, _tileCol, _tile))
      {
         _field.addTileAt(_tileRow, _tileCol, _tile);
         _tile = null;

         killRows();
         dropNextTile();
      }
      else
      {
         _tileRow = newRow;
         
         fireGridChanged(
            _tileRow - 1, _tileCol,
            _tile.getFormat().getRows() + 1, _tile.getFormat().getCols());
      }
   }

   public void tileMoveLeft()
   {
      if(!checkTileModification())
      {
         return;
      }

      int newCol = _tileCol - 1;
      
      if(_field.isTileFittingAt(_tileRow, newCol, _tile))
      {
         _tileCol = newCol;
         
         fireGridChanged(
            _tileRow, _tileCol,
            _tile.getFormat().getRows(), _tile.getFormat().getCols() + 1);
      }
   }
   
   public void tileMoveRight()
   {
      if(!checkTileModification())
      {
         return;
      }

      int newCol = _tileCol + 1;
      
      if(_field.isTileFittingAt(_tileRow, newCol, _tile))
      {
         _tileCol = newCol;
         
         fireGridChanged(
            _tileRow, _tileCol - 1,
            _tile.getFormat().getRows(), _tile.getFormat().getCols() + 1);
      }
   }
   
   public void tileTurnAntiClockWise()
   {
      if(!checkTileModification())
      {
         return;
      }

      Tile newTile = _tile.turnAntiClockWise();
      
      if(_field.isTileFittingAt(_tileRow, _tileCol, newTile))
      {
         Format oOldFormat = _tile.getFormat();
         Format oNewFormat = newTile.getFormat();
         _tile = newTile;

         fireGridChanged(
            _tileRow, _tileCol,
            Math.max(oOldFormat.getRows(), oNewFormat.getRows()),
            Math.max(oOldFormat.getCols(), oNewFormat.getCols()));
      }
   }
   
   public void tileTurnClockWise()
   {
      if(!checkTileModification())
      {
         return;
      }

      Tile newTile = _tile.turnClockWise();
      
      if(_field.isTileFittingAt(_tileRow, _tileCol, newTile))
      {
         Format oOldFormat = _tile.getFormat();
         Format oNewFormat = newTile.getFormat();
         _tile = newTile;

         fireGridChanged(
               _tileRow, _tileCol,
               Math.max(oOldFormat.getRows(), oNewFormat.getRows()),
               Math.max(oOldFormat.getCols(), oNewFormat.getCols()));
      }
   }
   
   public boolean isGameRunning()
   {
      return _gameRunning;
   }
   
   public void startGame()
   {
      if(isGameRunning())
      {
         return;
      }

      _rowsCompleted = 0;
      _score = 0L;
      _field = new DefaultField(_fieldFormat);
      dropNextTile();
      
      fireGridChanged(0, 0, _fieldFormat.getRows(), _fieldFormat.getCols());
      fireGameEvent(EventType.STARTED);

      _gameRunning = true;
      setAutoMoveEnabled(true);
   }

   public void stopGame()
   {
      if(!isGameRunning())
      {
         return;
      }
      
      _gameRunning = false;
      
      setAutoMoveEnabled(false);
   }

   public int getRowsCompleted()
   {
      return _rowsCompleted;
   }

   public long getScore()
   {
      return _score;
   }

   protected void fireGridChanged(int row, int col, int rowCount, int colCount)
   {
      GridEvent e = new GridEvent(this, row, col, rowCount, colCount);
      
      for(JoTrisModelListener oListener : _listeners)
      {
         oListener.gridEventOccured(e);
      }
   }
   
   protected void fireGameStarted()
   {
      fireGameEvent(EventType.STARTED);
   }
   
   protected void fireGameOver()
   {
      fireGameEvent(EventType.OVER);
   }
   
   protected void fireRowsKilled()
   {
      fireGameEvent(EventType.ROWS);
   }
   
   protected void fireTileChanged()
   {
      fireGameEvent(EventType.TILE);
   }
   
   protected void fireGameEvent(EventType eventType)
   {
      GameEvent e = new GameEvent(this, eventType);
      
      for(JoTrisModelListener oListener : _listeners)
      {
         oListener.gameEventOccured(e);
      }
   }
   
   protected void onGameOver()
   {
      stopGame();
      
      fireGameOver();
   }
   
   protected void onRowsKilled(int rowsKilled, int colsKilled)
   {
      _score += getScore(rowsKilled, colsKilled);
      _rowsCompleted += rowsKilled;
      
      fireRowsKilled();
   }

   protected boolean isRowToKill(int row)
   {
      return _field.isRowFull(row);
   }
   
   protected int getScore(int rowsKilled, int colsKilled)
   {
      return colsKilled * (2 * rowsKilled - 1);
   }
   
   protected Tile createTile()
   {
      return _factory.nextTile();
   }
   
   private void dropNextTile()
   {
      _tile = _nextTile;
      _nextTile = createTile();
      
      _tileRow = 0;
      _tileCol = (_fieldFormat.getCols() - _tile.getFormat().getCols()) / 2;
      
      fireTileChanged();
      
      if(!_field.isTileFittingAt(_tileRow, _tileCol, _tile))
      {
         onGameOver();
      }
      else
      {
         fireGridChanged(
            _tileRow, _tileCol,
            _tile.getFormat().getRows(), _tile.getFormat().getCols());
      }
   }
   
   private void killRows()
   {
      int rowsKilled = 0;
      final int colsKilled = _fieldFormat.getCols();
      
      for(int row = _fieldFormat.getRows() - 1; row >= 0; --row)
      {
         if(isRowToKill(row))
         {
            ++rowsKilled;
            
            _field.killRow(row);

            fireGridChanged(0, 0, row + 1, colsKilled);
            
            ++row;
         }
      }
      
      if(rowsKilled > 0)
      {
         onRowsKilled(rowsKilled, colsKilled);
      }
   }
   
   private boolean checkTileModification()
   {
      return isGameRunning() && _tile != null;
   }
   
   private Block getTileBlock(int row, int col)
   {
      if(_tile == null)
      {
        return null;
      }
      
      Format currentTileFormat = _tile.getFormat();

      if(row < _tileRow || row > _tileRow + currentTileFormat.getRows() - 1)
      {
         return null;
      }
      else if(col < _tileCol || col > _tileCol + currentTileFormat.getCols() - 1)
      {
         return null;
      }
      else
      {
         return _tile.getBlockAt(row - _tileRow, col - _tileCol);
      }
   }

   private void stopAutoMoveTimer()
   {
      _autoMoveTimer.cancel();
      _autoMoveTimer = null;
   }
   
   private void startAutoMoveTimer()
   {
      if(_autoMoveInterval <= 0L || !isGameRunning())
      {
         return;
      }
      
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            tileMoveDown();
         }
      };
      
      _autoMoveTimer = new Timer();
      _autoMoveTimer.schedule(task, new Date(System.currentTimeMillis() + _autoMoveInterval), _autoMoveInterval);
   }
   
}
