package net.sf.javagimmicks.games.maze.impl;

import java.awt.Point;
import java.util.Set;
import java.util.TreeSet;

import net.sf.javagimmicks.games.maze.model.DrawableCell;


/**
 * @author Michael Scholz
 */
public class OrthogonalCell implements DrawableCell<OrthogonalCell>
{
	protected final int x;
	protected final int y;
	
	public OrthogonalCell(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getGeometry()
	{
		return 4;
	}
	
	public int getIncomingDirection(int direction)
	{
		return (direction + 2) % 4;
	}
	
	
	public OrthogonalCell getNeighbor(int direction) throws IndexOutOfBoundsException
	{
		switch(direction)
		{
			case 0:
				return new OrthogonalCell(this.x + 1, this.y);
			case 1:
				return new OrthogonalCell(this.x, this.y + 1);
			case 2:
				return new OrthogonalCell(this.x - 1, this.y);
			case 3:
				return new OrthogonalCell(this.x, this.y - 1);
			default:
				throw new IndexOutOfBoundsException("Unallowed direction '" + direction + "'!");
		}
	}
	
	public boolean isNeighbor(OrthogonalCell other)
	{
		if(this.x == other.x && Math.abs(this.y - other.y) == 1)
			return true;
		else if(this.y == other.y && Math.abs(this.x - other.x) == 1)
			return true;
		else
			return false;
	}
	
	public int compareTo(OrthogonalCell other)
	{
		if(other.y != this.y)
			return this.y - other.y;
		else
			return this.x - other.x;
	}
	
	public boolean equals(OrthogonalCell o)
	{
		OrthogonalCell other = (OrthogonalCell)o;
		return (this.x == other.x && this.y == other.y);
	}
	
	public Set<OrthogonalCell> createCellArea(int size)
	{
		Set<OrthogonalCell> result = new TreeSet<OrthogonalCell>();
		
		for(int x = 0; x < size; ++x)
		{
			for(int y = 0; y < size; ++y)
			{
				result.add(new OrthogonalCell(this.x + x, this.y + y));
			}
		}
		
		return result;
	}
	
	public String toString()
	{
		return "" + this.x + "/" + this.y;
	}
	
	public Point[] getEdgePoints(int lineLength)
	{
		int baseX = this.x * lineLength;
		int baseY = this.y * lineLength;
		
		int x0 = baseX + lineLength;
		int y0 = baseY;
		
		int x1 = x0;
		int y1 = baseY + lineLength;
		
		int x2 = baseX;
		int y2 = y1;
		
		int x3 = x2;
		int y3 = y0;
		
		Point[] result = new Point[getGeometry()];
		result[0] = new Point(x0, y0);
		result[1] = new Point(x1, y1);
		result[2] = new Point(x2, y2);
		result[3] = new Point(x3, y3);
		
		return result;
	}
}
