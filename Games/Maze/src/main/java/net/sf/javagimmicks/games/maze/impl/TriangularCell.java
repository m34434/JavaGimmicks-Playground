package net.sf.javagimmicks.games.maze.impl;

import java.awt.Point;
import java.util.Set;
import java.util.TreeSet;

import net.sf.javagimmicks.games.maze.model.DrawableCell;


/**
 * @author Michael Scholz
 */
public class TriangularCell implements DrawableCell<TriangularCell>
{
	protected final int x;
	protected final int y;
	
	public TriangularCell(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getGeometry()
	{
		return 3;
	}
	
	public int getIncomingDirection(int direction)
	{
		if(this.y % 2 == 0)
			return (direction + 1) % 3;
		else
			return (direction + 2) % 3;
	}
	
	public TriangularCell getNeighbor(int direction) throws IllegalArgumentException,
			IndexOutOfBoundsException
	{
		if(this.y % 2 == 0)
		{
			switch(direction)
			{
				case 0:
					return new TriangularCell(this.x + 1, this.y + 1);
				case 1:
					return new TriangularCell(this.x - 1, this.y + 1);
				case 2:
					return new TriangularCell(this.x, this.y - 1);
				default:
					throw new IndexOutOfBoundsException("Unallowed direction '" + direction + "'!");
			}
		}
		else
		{
			switch(direction)
			{
				case 0:
					return new TriangularCell(this.x + 1, this.y - 1);
				case 1:
					return new TriangularCell(this.x, this.y + 1);
				case 2:
					return new TriangularCell(this.x - 1, this.y - 1);
				default:
					throw new IndexOutOfBoundsException("Unallowed direction '" + direction + "'!");
			}
		}
	}
	
	public boolean isNeighbor(TriangularCell other)
	{
		if(this.y % 2 == 0)
		{
			if(other.y - 1 == this.y && Math.abs(other.x - this.x) == 1)
				return true;
			else if(other.y + 1 == this.y && this.x == other.x)
				return true;
			else
				return false;
		}
		else
		{
			if(other.y + 1 == this.y && Math.abs(other.x - this.x) == 1)
				return true;
			else if(other.y - 1 == this.y && this.x == other.x)
				return true;
			else
				return false;
		}
	}
	
	public int compareTo(TriangularCell other)
	{
		if(other.y != this.y)
			return this.y - other.y;
		else
			return this.x - other.x;
	}

	public boolean equals(TriangularCell o)
	{
		TriangularCell other = (TriangularCell)o;
		return (this.x == other.x && this.y == other.y);
	}
	
	public Set<TriangularCell> createCellArea(int sizeFactor)
	{
		Set<TriangularCell> result = new TreeSet<TriangularCell>();
		
        TriangularCell current = this.getNeighbor(getIncomingDirection(0));
		int direction = 0;
		
		for(int length = sizeFactor * 2; length >= 0; length = length - 2)
		{
			for(int i = 0; i < length; ++i)
			{
				current = current.getNeighbor(direction);
				result.add(current);
			}
			
			result.add(current.getNeighbor(direction));
			
			direction = (direction + 1) % getGeometry();
		}
		
		return result;
	}

	public String toString()
	{
		return "" + this.x + "/" + this.y;
	}
	
	public Point[] getEdgePoints(int lineLength)
	{
		double xUnitLength = 0.5 * lineLength;
		double yUnitLength = (SQRT3 * lineLength / 2);

		int x0, y0, x1, y1, x2, y2;
		
		if(this.y % 2 == 0)
		{
			int baseXUnits = this.x;
			int baseYUnits = this.y / 2;
			
			x0 = (int)(xUnitLength * baseXUnits);
			y0 = (int)(yUnitLength * baseYUnits);
			
			x1 = (int)(xUnitLength * (baseXUnits + 2));
			y1 = y0;
			
			x2 = (int)(xUnitLength * (baseXUnits + 1));
			y2 = (int)(yUnitLength * (baseYUnits + 1));
		}
		else
		{
			int baseX = this.x;
			int baseY = (this.y + 1) / 2;
			
			x0 = (int)(xUnitLength * baseX);
			y0 = (int)(yUnitLength * baseY);
			
			x1 = (int)(xUnitLength * (baseX + 1));
			y1 = (int)(yUnitLength * (baseY - 1));
			
			x2 = (int)(xUnitLength * (baseX + 2));
			y2 = y0;
		}
		
		Point[] result = new Point[getGeometry()];
		result[0] = new Point(x1, y1);
		result[1] = new Point(x2, y2);
		result[2] = new Point(x0, y0);
		
		return result;
	}
	
	private static double SQRT3 = Math.sqrt(3.0);
}
