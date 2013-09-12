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
		MazeUI oMazeUI = createMaze(new TriangularCell(0, 0), 20);
//		MazeUI oMazeUI = createMaze(new OrthogonalCell(0, 0), 20);
//		MazeUI oMazeUI = createMaze(new HexagonalCell(12, 0), 12);
		
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
