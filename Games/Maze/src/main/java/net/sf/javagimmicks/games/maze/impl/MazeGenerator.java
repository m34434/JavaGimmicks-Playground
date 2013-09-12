package net.sf.javagimmicks.games.maze.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import net.sf.javagimmicks.games.maze.model.Cell;


public class MazeGenerator
{
	public static <T extends Cell<T>> Map<T, boolean[]> createBreadthFirst(Set<T> cells)
	{
		Map<T, boolean[]> result = new TreeMap<T, boolean[]>();
		if(cells.isEmpty())
			return result;
		
		List<CellInfo<T>> unfinished = new LinkedList<CellInfo<T>>();
		T init = cells.iterator().next();
		CellInfo<T> current = new CellInfo<T>(init);
		current.putTo(result);
		unfinished.add(current);

		while(unfinished.size() > 0)
		{
			current = unfinished.remove(random.nextInt(unfinished.size()));

			ArrayList<Integer> validNeighbors = getValidNeighbors(current, result.keySet(), cells);
			for(int direction : validNeighbors)
			{
				CellInfo<T> next = current.getNeighborInfo(direction);
				next.putTo(result);

				// Mauer entfernen
				breakWall(current, next, direction);
				
				unfinished.add(next);
			}
		}
		
		return result;
	}
	
	public static <T extends Cell<T>> Map<T, boolean[]> createDepthFirst(Set<T> cells)
	{
		Map<T, boolean[]> result = new TreeMap<T, boolean[]>();
		if(cells.isEmpty())
			return result;

		Stack<CellInfo<T>> stack = new Stack<CellInfo<T>>();
		
		CellInfo<T> current = new CellInfo<T>(cells.iterator().next());
		current.putTo(result);

		while(true)
		{
			// Unbesuchte Nachbarn der Zelle suchen
			ArrayList<Integer> validNeighbors = getValidNeighbors(current, result.keySet(), cells);

			if(validNeighbors.size() == 0)
			{
				// Abbruch, wenn keine Zelle mehr übrig
				if(stack.size() == 0)
					break;
				else
				{
					// Letzte Zell vom Stack holen
					current = stack.pop();
				}
			}
			else
			{
				// Zufääligen unbesuchten Nachbar auswählen
				// und Variablen anlegen
				int direction = validNeighbors.get(random.nextInt(validNeighbors.size()));
				CellInfo<T> next = current.getNeighborInfo(direction);
				next.putTo(result);

				// Mauer entfernen
				breakWall(current, next, direction);
				
				// Mit nächster Zelle weiter
				stack.push(current);
				current = next;
			}
		}
		
		return result;
	}
	
	private static ArrayList<Integer> getValidNeighbors(CellInfo cellInfo, Collection excluded, Collection cells)
	{
		ArrayList<Integer> validNeighbors = new ArrayList<Integer>(cellInfo.geometry);
		for(int d = 0; d < cellInfo.geometry; ++d)
		{
			Cell neighbor = cellInfo.cell.getNeighbor(d);
			if(!excluded.contains(neighbor) && cells.contains(neighbor))
				validNeighbors.add(d);
		}
		
		return validNeighbors;
	}
	
	private static class CellInfo<T extends Cell<T>>
	{
		public final T cell;
		public final int geometry;
		public final boolean[] connections;
		
		public CellInfo(final T cell, final boolean[] connections)
		{
			this.cell = cell;
			this.geometry = cell.getGeometry();
			this.connections = connections;
		}

		public CellInfo(final T cell)
		{
			this.cell = cell;
			this.geometry = cell.getGeometry();
			this.connections = new boolean[this.geometry];
		}
		
		public CellInfo<T> getNeighborInfo(int direction)
		{
			return new CellInfo<T>(cell.getNeighbor(direction));
		}
		
		public void putTo(Map<T, boolean[]> target)
		{
			target.put(cell, connections);
		}
		
		public String toString()
		{
			return cell.toString();
		}
	}
	
	private static void breakWall(CellInfo from, CellInfo to, int direction)
	{
		from.connections[direction] = true;
		to.connections[to.cell.getIncomingDirection(direction)] = true;
	}
	
	private static Random random = new Random();
}
