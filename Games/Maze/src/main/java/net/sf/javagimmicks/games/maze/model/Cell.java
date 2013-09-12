package net.sf.javagimmicks.games.maze.model;

import java.util.Set;

public interface Cell<T extends Cell<T>> extends Comparable<T>
{
	public int getGeometry();
	
	public T getNeighbor(int direction) throws IndexOutOfBoundsException;

	public boolean isNeighbor(T other);

	public boolean equals(T other);
	
	public int getIncomingDirection(int direction);
	
	public Set<T> createCellArea(int size);
}