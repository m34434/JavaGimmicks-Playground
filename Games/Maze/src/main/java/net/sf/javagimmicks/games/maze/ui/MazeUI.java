package net.sf.javagimmicks.games.maze.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Map.Entry;

import javax.swing.JComponent;

import net.sf.javagimmicks.games.maze.model.DrawableCell;
import net.sf.javagimmicks.games.maze.model.Maze;


/**
 * @author Michael Scholz
 */
public class MazeUI<T extends DrawableCell<T>> extends JComponent
{
    private static final long serialVersionUID = -2639134019561718433L;

    final Maze<T> maze;

    public MazeUI(Maze<T> maze)
    {
        this.maze = maze;
    }
    
    public Maze<T> getMaze()
    {
        return this.maze;
    }

    public void paint(Graphics graphics)
    {
        for (Entry<T, boolean[]> entry : maze.getCellInfo().entrySet())
        {
            T drawableCell = entry.getKey();
            boolean[] connections = entry.getValue();

            Point[] edgePoints = drawableCell.getEdgePoints(500 / maze
                    .getSizeFactor());

            Polygon polygon = new Polygon();
            for (int i = 0; i < connections.length; ++i)
            {
                polygon.addPoint(edgePoints[i].x, edgePoints[i].y);
            }

            Color color;
            if (drawableCell.equals(maze.getCurrentCell()))
                color = Color.YELLOW;
            else if (maze.getVisited().contains(drawableCell))
                color = Color.LIGHT_GRAY;
            else
                color = Color.WHITE;

            graphics.setColor(color);
            graphics.fillPolygon(polygon);

            graphics.setColor(Color.BLACK);
            for (int i = 0; i < connections.length; ++i)
            {
                if (!connections[i])
                {
                    Point a = edgePoints[i];
                    Point b = edgePoints[(i + 1) % connections.length];
                    graphics.drawLine(a.x, a.y, b.x, b.y);
                }
            }
        }
    }
    
    public Dimension getPreferredSize()
    {
        return new Dimension(500, 500);
    }
    
}
