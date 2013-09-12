/**
 * 
 */
package net.sf.javagimmicks.games.maze.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

public class MazeMoveKeyListener implements KeyListener
{
    private final MazeUI mazeUI;

    public MazeMoveKeyListener(MazeUI mazeUI)
    {
        this.mazeUI = mazeUI;
    }

    public void keyPressed(KeyEvent e)
    {}

    public void keyReleased(KeyEvent e)
    {}

    public void keyTyped(KeyEvent e)
    {
        int iPosition = e.getKeyChar() - '1';

        if (iPosition >= 0 && iPosition < this.mazeUI.getMaze().getGeometry())
        {
        	try
        	{
                this.mazeUI.getMaze().move(iPosition);
                this.mazeUI.repaint();
        	}
        	catch(IllegalStateException ex)
        	{
        		JOptionPane.showMessageDialog(mazeUI, "Illegal direction!", "Move error", JOptionPane.ERROR_MESSAGE);
        	}

            System.out.println(this.mazeUI.maze.getPercentageVisited() + " %");
        }
    }
}