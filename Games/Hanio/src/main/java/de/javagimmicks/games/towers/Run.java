package de.javagimmicks.games.towers;
import javax.swing.JFrame;

import de.javagimmicks.games.towers.model.GameException;
import de.javagimmicks.games.towers.view.components.TowerGameWindow;


public class Run
{
    public static void main(String[] args) throws GameException
    {
        JFrame window = new TowerGameWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
