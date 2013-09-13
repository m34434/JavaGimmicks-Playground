package de.javagimmicks.games.jotris.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import de.javagimmicks.games.jotris.model.JoTrisModel;

/**
 * An implementation of {@link WindowListener} that pauses a given
 * JoTris game when the game window is deactivated and continues the
 * game, when the game window is activated again.
 */
public class AutoPauseWindowListener extends WindowAdapter
{
   private final JoTrisModel _model;
   private boolean _wasRunning;
   
   /**
    * Creates a new {@link AutoPauseWindowListener} for the given
    * {@link JoTrisModel}.
    * @param model the {@link JoTrisModel} on which the game should be paused
    * or continued
    */
   public AutoPauseWindowListener(JoTrisModel model)
   {
      _model = model;
   }

   @Override
   public void windowActivated(WindowEvent e)
   {
      if(_wasRunning)
      {
         _model.setAutoMoveEnabled(true);
      }
   }

   @Override
   public void windowDeactivated(WindowEvent e)
   {
      _wasRunning = _model.isAutoMoveEnabled();
      
      if(_wasRunning)
      {
         _model.setAutoMoveEnabled(false);
      }
   }
}
