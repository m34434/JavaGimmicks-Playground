package net.sf.javagimmicks.games.maze.impl;

import java.awt.Point;
import java.util.Set;
import java.util.TreeSet;

import net.sf.javagimmicks.games.maze.model.DrawableCell;


/**
 * @author Michael Scholz
 */
public class HexagonalCell implements DrawableCell<HexagonalCell>
{
	protected final int x;
	protected final int y;
	
	public HexagonalCell(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getGeometry()
	{
		return GEOMETRY;
	}
	
	public int getIncomingDirection(int direction)
	{
		return (direction + GEOMETRY / 2) % GEOMETRY;
	}
	
	public HexagonalCell getNeighbor(int direction) throws IndexOutOfBoundsException
	{
		switch(direction)
		{
			case 0:
				return new HexagonalCell(this.x + 2, this.y);
			case 1:
				return new HexagonalCell(this.x + 1, this.y + 1);
			case 2:
				return new HexagonalCell(this.x - 1, this.y + 1);
			case 3:
				return new HexagonalCell(this.x - 2, this.y);
			case 4:
				return new HexagonalCell(this.x - 1, this.y - 1);
			case 5:
				return new HexagonalCell(this.x + 1, this.y - 1);
			default:
				throw new IndexOutOfBoundsException("Unallowed direction '" + direction + "'!");
		}
	}

	public boolean isNeighbor(HexagonalCell other)
	{
		if(other.y == this.y)
			return (Math.abs(other.x - this.x) == 2);
		else if(Math.abs(other.y - this.y) == 1)
			return (Math.abs(other.x - this.x) == 1);
		else
			return false;
	}

	public int compareTo(HexagonalCell other)
	{
		if(other.y != this.y)
			return this.y - other.y;
		else
			return this.x - other.x;
	}

	public boolean equals(HexagonalCell o)
	{
		HexagonalCell other = (HexagonalCell)o;
		return (this.x == other.x && this.y == other.y);
	}
	
	public Set<HexagonalCell> createCellArea(int size)
	{
		Set<HexagonalCell> result = new TreeSet<HexagonalCell>();
        HexagonalCell current = this.getNeighbor(getIncomingDirection(1));
		
		for(int s = size; s >= 0; --s)
		{
			current = current.getNeighbor(1);
			result.add(current);
			
			for(int direction = 0; direction < getGeometry(); ++direction)
			{
				for(int i = 0; i < s; ++i)
				{
					current = current.getNeighbor(direction);
					result.add(current);
				}
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
		double xUnitLength = (SQRT3 * lineLength * 0.15);
		double yUnitLength = 0.15 * lineLength;
		
		int baseXUnits = this.x;
		int baseYUnits = this.y * 3;
		
		int x0 = (int)(xUnitLength * baseXUnits);
		int y0 = (int)(yUnitLength * baseYUnits);
		
		int x1 = (int)(xUnitLength * (baseXUnits + 1));
		int y1 = (int)(yUnitLength * (baseYUnits - 1));
		
		int x2 = (int)(xUnitLength * (baseXUnits + 2));
		int y2 = y0;
		
		int x3 = x2;
		int y3 = (int)(yUnitLength * (baseYUnits + 2));
		
		int x4 = x1;
		int y4 = (int)(yUnitLength * (baseYUnits + 3));
		
		int x5 = x0;
		int y5 = y3;
		
		Point[] result = new Point[getGeometry()];
		result[0] = new Point(x2, y2);
		result[1] = new Point(x3, y3);
		result[2] = new Point(x4, y4);
		result[3] = new Point(x5, y5);
		result[4] = new Point(x0, y0);
		result[5] = new Point(x1, y1);
		
		return result;
	}
	
	private final static double SQRT3 = Math.sqrt(3.0); 
	private final static int GEOMETRY = 6;
}
