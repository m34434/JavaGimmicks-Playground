package de.javagimmicks.games.jotris.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import de.javagimmicks.games.jotris.model.JoTrisModel;

/**
 * An implementation of {@link Action} that starts a JoTris game.
 * <p>
 * It internally sends the respective command to a given {@link JoTrisModel}
 */
public class StartGameAction extends AbstractAction
{
   private static final long serialVersionUID = 537447805791956007L;

   private final JoTrisModel _model;

   /**
    * Creates a new {@link StartGameAction} for the given {@link JoTrisModel}
    * with the given name and icon.
    * @param model the {@link JoTrisModel} to create this {@link StartGameAction} for
    * @param name the name of this {@link StartGameAction}
    * @param icon the {@link Icon} of this {@link StartGameAction}
    */
   public StartGameAction(JoTrisModel model, String name, Icon icon)
   {
      super(name, icon);
      
      _model = model;
   }
   
   /**
    * Creates a new {@link StartGameAction} for the given {@link JoTrisModel}
    * with the given name.
    * @param model the {@link JoTrisModel} to create this {@link StartGameAction} for
    * @param name the name of this {@link StartGameAction}
    */
   public StartGameAction(JoTrisModel model, String name)
   {
      super(name);
      
      _model = model;
   }
   
   /**
    * Creates a new {@link StartGameAction} for the given {@link JoTrisModel},
    * @param model the {@link JoTrisModel} to create this {@link StartGameAction} for
    */
   public StartGameAction(JoTrisModel model)
   {
      _model = model;
   }

   public void actionPerformed(ActionEvent e)
   {
      if(!_model.isGameRunning())
      {
         _model.startGame();
      }
   }
}
