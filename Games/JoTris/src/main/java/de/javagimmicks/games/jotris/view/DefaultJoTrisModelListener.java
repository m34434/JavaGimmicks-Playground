package de.javagimmicks.games.jotris.view;

import javax.swing.SwingUtilities;

import de.javagimmicks.games.jotris.model.GameEvent;
import de.javagimmicks.games.jotris.model.GridEvent;
import de.javagimmicks.games.jotris.model.JoTrisModelListener;



abstract class DefaultJoTrisModelListener implements JoTrisModelListener
{
   public final void gridEventOccured(final GridEvent e)
   {
      if(SwingUtilities.isEventDispatchThread())
      {
         _gridEventOccured(e);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _gridEventOccured(e);
            }
         });
      }
   }
   
   public final void gameEventOccured(final GameEvent e)
   {
      if(SwingUtilities.isEventDispatchThread())
      {
         _gameEventOccured(e);
      }
      else
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _gameEventOccured(e);
            }
         });
      }
   }

   protected void _gridEventOccured(GridEvent e)
   {
      
   }

   protected void _gameEventOccured(GameEvent e)
   {
      
   }
}
