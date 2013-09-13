package de.javagimmicks.games.jotris.view;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import de.javagimmicks.games.jotris.model.GameEvent;
import de.javagimmicks.games.jotris.model.JoTrisModel;
import de.javagimmicks.games.jotris.model.JoTrisModelListener;

/**
 * Renders the game score rows of a {@link JoTrisModel}.
 */
public class JoTrisScoreField extends JFormattedTextField
{
   private static final long serialVersionUID = -3351816203305812018L;

   private JoTrisModel _model;
   private final JoTrisModelListener _listener = new DefaultJoTrisModelListener()
   {
      protected void _gameEventOccured(GameEvent e)
      {
         if(e.isRowsKilled() || e.isGameStarted())
         {
            updateText();
         }
      }
   };
   
   /**
    * Creates a new {@link JoTrisScoreField} for the given {@link JoTrisModel}.
    * @param model the {@link JoTrisModel} to create the {@link JoTrisScoreField} for
    */
   public JoTrisScoreField(JoTrisModel model)
   {
      super(NumberFormat.getIntegerInstance());
      
      setHorizontalAlignment(JTextField.CENTER);
      setFocusable(false);
      
      setModel(model);
   }
   
   /**
    * Sets a new {@link JoTrisModel} for this {@link JoTrisScoreField}.
    * @param model the new {@link JoTrisModel} for this {@link JoTrisScoreField}
    */
   public void setModel(JoTrisModel model)
   {
      if(_model != null)
      {
         _model.removeJoTrisModelListener(_listener);
      }
      
      _model = model;
      if(_model != null)
      {
         _model.addJoTrisModelListener(_listener);
         updateText();
      }
   }
   
   private void updateText()
   {
      setText(String.valueOf(_model.getScore()));
   }
}
