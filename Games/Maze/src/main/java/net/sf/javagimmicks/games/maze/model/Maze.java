package net.sf.javagimmicks.games.maze.model;

import java.util.Map;
import java.util.Set;

/**
 * @author Michael Scholz
 */
public interface Maze<T extends Cell>
{
    public int getGeometry();
    
	public void reset();
	
	public T getCurrentCell();
	
	public void move(int direction) throws IllegalStateException;
	
	public boolean[] getConnections();
	
	public double getPercentageVisited();
	
	public Set<T> getVisited();
    
    public Map<T, boolean[]> getCellInfo();
    
    public int getSizeFactor();
}
