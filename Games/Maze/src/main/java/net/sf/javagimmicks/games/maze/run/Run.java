package net.sf.javagimmicks.games.maze.run;
import javax.swing.JFrame;

import net.sf.javagimmicks.games.maze.impl.HexagonalCell;
import net.sf.javagimmicks.games.maze.impl.MazeImpl;
import net.sf.javagimmicks.games.maze.impl.OrthogonalCell;
import net.sf.javagimmicks.games.maze.impl.TriangularCell;
import net.sf.javagimmicks.games.maze.model.DrawableCell;
import net.sf.javagimmicks.games.maze.model.Maze;
import net.sf.javagimmicks.games.maze.ui.MazeMoveKeyListener;
import net.sf.javagimmicks.games.maze.ui.MazeUI;



public class Run
{
	public static void main(String[] args)
	{
	   int iGeometry = 4;
	   
	   if(args.length > 0)
	   {
	      iGeometry = Integer.parseInt(args[0]);
	   }
	   
		MazeUI<?> oMazeUI;
		
      if(iGeometry == 3)
      {
         oMazeUI = createMaze(new TriangularCell(0, 0), 20);
      }
      else if(iGeometry == 4)
      {
         oMazeUI = createMaze(new OrthogonalCell(0, 0), 20);
      }
      else if(iGeometry == 6)
      {
         oMazeUI = createMaze(new HexagonalCell(12, 0), 12);
      }
      else
      {
         return;
      }
      
		MazeMoveKeyListener oMazeKeyListener = new MazeMoveKeyListener(oMazeUI);
		
		JFrame oWindow = new JFrame("Maze");
		oWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		oWindow.getContentPane().add(oMazeUI);
		oWindow.addKeyListener(oMazeKeyListener);
		oWindow.pack();
		oWindow.setVisible(true);
	}
	
	private static <T extends DrawableCell<T>> MazeUI<T> createMaze(T oInit, int iSize)
	{
		Maze<T> oMaze = new MazeImpl<T>(oInit, iSize);
		MazeUI<T> oMazeUI = new MazeUI<T>(oMaze);
		
		return oMazeUI;
	}
}
