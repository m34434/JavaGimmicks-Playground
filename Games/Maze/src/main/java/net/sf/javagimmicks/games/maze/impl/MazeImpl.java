package net.sf.javagimmicks.games.maze.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.javagimmicks.games.maze.model.Cell;
import net.sf.javagimmicks.games.maze.model.Maze;


public class MazeImpl<T extends Cell<T>> implements Maze<T>
{
	protected final T startingPosition;
	protected final Set<T> visited = new TreeSet<T>();
	protected final Map<T, boolean[]> cellInfo;
	protected final int sizeFactor;
	
	protected T currentPosition;
	protected boolean[] currentCellInfo;
	
	public MazeImpl(T init, int sizeFactor)
	{
		this.sizeFactor = sizeFactor;
		this.startingPosition = init;
		
		//this.cellInfo = MazeGenerator.createDepthFirst(init.createCellArea(sizeFactor));
		this.cellInfo = MazeGenerator.createBreadthFirst(init.createCellArea(sizeFactor));
		
		reset();
	}
	
	public T getCurrentCell()
	{
		return this.currentPosition;
	}
	
	public boolean[] getConnections()
	{
		return this.currentCellInfo;
	}
	
	public void move(int direction) throws IndexOutOfBoundsException, IllegalStateException
	{
		if(!this.currentCellInfo[direction])
		{
			throw new IllegalStateException("Unable to move to direction '" + direction + "' from here!");
		}
		
		setPosition(this.currentPosition.getNeighbor(direction));
	}
	
	public double getPercentageVisited()
	{
		return (100.0 * this.visited.size()) / this.cellInfo.size();
	}
	
	public void reset()
	{
		this.visited.clear();
		setPosition(this.startingPosition);
	}
	
	public Set<T> getVisited()
	{
		return Collections.unmodifiableSet(this.visited);
	}
    
	public int getGeometry()
    {
       return this.currentPosition.getGeometry();
    }
    
    public Map<T, boolean[]> getCellInfo()
    {
        return Collections.unmodifiableMap(this.cellInfo);
    }

    public int getSizeFactor()
    {
        return this.sizeFactor;
    }

    protected void setPosition(T position)
	{
		this.currentCellInfo = this.cellInfo.get(position);
		this.visited.add(position);
		this.currentPosition = position;
	}
}