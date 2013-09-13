package de.javagimmicks.games.jotris.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.javagimmicks.games.jotris.model.JoTrisModel;
import de.javagimmicks.games.jotris.model.Tile;

/**
 * An implementation of {@link KeyListener} that provides
 * basic controlling of a JoTris game via keyboard commands.
 * <p>
 * The respective keyboard commands are forwarded to the
 * internal {@link JoTrisModel}.
 * <p>
 * The following keyboard commands are provided:
 * <ul>
 * <li>DOWN: Moves the current {@link Tile} one row down</li>
 * <li>LEFT: Moves the current {@link Tile} one column left</li>
 * <li>RIGHT: Moves the current {@link Tile} one column right</li>
 * <li>SPACE: Turns the current {@link Tile} in clockwise direction</li>
 * <li>Crtl + LEFT: Turns the current {@link Tile} in anti-clockwise direction</li>
 * <li>Crtl + RIGHT: Turns the current {@link Tile} in clockwise direction</li>
 * <li>Pause: Pauses/continues the game</li>
 * </ul>
 */
public class StandardControlKeyListener extends KeyAdapter
{
	private final JoTrisModel _model;
	private final boolean _pauseEnabled;

   /**
    * Creates a new {@link StandardControlKeyListener} for a given
    * {@link JoTrisModel}.
    * @param model the {@link JoTrisModel} to create the {@link StandardControlKeyListener}
    * for
    */
   public StandardControlKeyListener(JoTrisModel model, boolean pauseEnabled)
   {
      _model = model;
      _pauseEnabled = pauseEnabled;
   }

   /**
    * Creates a new {@link StandardControlKeyListener} for a given
    * {@link JoTrisModel}.
    * @param model the {@link JoTrisModel} to create the {@link StandardControlKeyListener}
    * for
    */
   public StandardControlKeyListener(JoTrisModel model)
   {
      this(model, true);
   }

	@Override
	public void keyPressed(KeyEvent e)
	{
	   if(e.getModifiers() == 0)
	   {
	      switch(e.getKeyCode())
	      {
	         case KeyEvent.VK_DOWN:
	            _model.tileMoveDown();
	            break;
	         case KeyEvent.VK_LEFT:
	            _model.tileMoveLeft();
	            break;
	         case KeyEvent.VK_RIGHT:
	            _model.tileMoveRight();
	            break;
            case KeyEvent.VK_SPACE:
               _model.tileTurnClockWise();
               break;
            case KeyEvent.VK_PAUSE:
               if(_pauseEnabled)
               {
                  _model.setAutoMoveEnabled(!_model.isAutoMoveEnabled());
               }
               break;
	         default:
	            break;
	      }
	   }
	   else if(e.getModifiers() == KeyEvent.CTRL_MASK)
	   {
         switch(e.getKeyCode())
         {
            case KeyEvent.VK_LEFT:
               _model.tileTurnAntiClockWise();
               break;
            case KeyEvent.VK_RIGHT:
               _model.tileTurnClockWise();
               break;
            default:
               break;
         }
	   }
	}
}