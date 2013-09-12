package net.sf.javagimmicks.games.maze.model;

import java.awt.Point;

/**
 * @author Michael Scholz
 */
public interface DrawableCell<T extends DrawableCell<T>> extends Cell<T>
{
	public Point[] getEdgePoints(int lineLength);
}
