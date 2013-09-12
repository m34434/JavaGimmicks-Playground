package de.javagimmicks.games.towers.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import de.javagimmicks.games.towers.model.Game;
import de.javagimmicks.games.towers.model.GameException;
import de.javagimmicks.games.towers.view.components.DiscLabel;
import de.javagimmicks.games.towers.view.components.TowerGamePane;
import de.javagimmicks.games.towers.view.components.TowerGameWindow;


public class UserGameMouseListener implements MouseListener
{
    private final TowerGameWindow _gameWindow;
    private final TowerGamePane _gamePane;
    private final Game _game;
    
    private DiscMovementMouseMotionListener _discMovementListener;
    private DiscLabel _movedDisc;
    private int _originalSection;
    private boolean _solved = false;
    
    public UserGameMouseListener(TowerGameWindow gameWindow, TowerGamePane gamePane)
    {
        _gameWindow = gameWindow;
        _gamePane = gamePane;
        _game = _gamePane.getGame();
    }

    public void mouseClicked(MouseEvent e)
    {
        if(_solved)
        {
            return;
        }
        
        int section = _gamePane.getPaneLayout().getSectionForPostition(e.getPoint());
        
        if(section == -1)
        {
            return;
        }

        if(_discMovementListener == null)
        {
            Component clickedComponent = _gamePane.getComponentAt(e.getPoint());

            // Only DiscLabel components may be clicked!
            if(clickedComponent == _gamePane || !(clickedComponent instanceof DiscLabel))
            {
                return;
            }
            
            DiscLabel disc = (DiscLabel)clickedComponent;
            if(_game.getStick(section).peek().getWidth() == disc.getWidthIndex())
            {
                _originalSection = section;
                _movedDisc = disc;
                _gamePane.setLayer(_movedDisc, JLayeredPane.DRAG_LAYER);

                _discMovementListener = new DiscMovementMouseMotionListener(_movedDisc);
                _gamePane.addMouseMotionListener(_discMovementListener);
            }
        }
        else
        {
            _gamePane.setLayer(_movedDisc, JLayeredPane.DEFAULT_LAYER);
            _gamePane.removeMouseMotionListener(_discMovementListener);
            _discMovementListener = null;

            if(_originalSection != section)
            {
                try
                {
                    _game.moveDisc(_originalSection, section);
                    _gameWindow.incStepCounter();
                }
                catch(GameException ex)
                {
                    JOptionPane.showMessageDialog(_gamePane, ex.getMessage());
                }
            }
            _gamePane.redraw();
            
            if(_game.isSolved())
            {
                _solved = true;
                
                JOptionPane.showMessageDialog(_gamePane, "Congratulations!");
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    private static class DiscMovementMouseMotionListener implements MouseMotionListener
    {
        private final DiscLabel _disc;
        private final Dimension _discSize;
        
        public DiscMovementMouseMotionListener(DiscLabel currentDisc)
        {
            _disc = currentDisc;
            _discSize = _disc.getSize();
        }

        public void mouseMoved(MouseEvent e)
        {
            _disc.setLocation(e.getX() - _discSize.width / 2, e.getY() - _discSize.height / 2);
        }

        public void mouseDragged(MouseEvent e) {}
     
    }
}
