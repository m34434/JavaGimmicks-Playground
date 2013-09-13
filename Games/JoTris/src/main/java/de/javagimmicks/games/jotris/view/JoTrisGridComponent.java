package de.javagimmicks.games.jotris.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.javagimmicks.games.jotris.model.Block;
import de.javagimmicks.games.jotris.model.Format;
import de.javagimmicks.games.jotris.model.JoTrisModel;

/**
 * Renders a {@link JoTrisModel} by displaying all its {@link Block}s
 * (regarding their color) in a grid. 
 */
public abstract class JoTrisGridComponent extends JComponent
{
   private static final long serialVersionUID = 7577663349009099877L;

   private final GridBagConstraints _CONSTRAINTS = new GridBagConstraints();
   private int _blockSize;

   /**
    * Creates a new {@link JoTrisGridComponent} with the given block size.
    * @param blockSize the size that a single rendered {@link Block} should have
    */
   protected JoTrisGridComponent(int blockSize)
   {
      _CONSTRAINTS.fill = GridBagConstraints.BOTH;
      
      _blockSize = blockSize;
   
      addComponentListener(new BlockSizeComponentListener());
   }
   
   /**
    * Creates a new {@link JoTrisGridComponent} with a standard block size of 20.
    */
   protected JoTrisGridComponent()
   {
      this(20);
   }
   
   abstract protected Format getGridFormat();
   abstract protected Block getBlockAt(int row, int col);

   public int getBlockSize()
   {
      return _blockSize;
   }
   
   /**
    * Sets a new block size for this {@link JoTrisGridComponent}.
    * <p>
    * This action does not resize the {@link JoTrisGridComponent} automatically. 
    * @param size the new block size
    */
   public void setBlockSize(int size)
   {
      if(size <= 0)
      {
         throw new IllegalArgumentException("Size must be greater than 0!");
      }
      
      _blockSize = size;
      
      updateAll();
   }

   /**
    * Completely resets the grid content forcing the component to build it up
    * again from scratch.
    */
   protected void resetGrid()
   {
      this.removeAll();
      this.setLayout(new GridBagLayout());
      
      updateAll();
   }
      
   /**
    * Updates the grid blocks in the specified rectangle.
    * @param row the row of the rectangle's upper left corner
    * @param col the column of the rectangle's upper left corner
    * @param rowCount the number of rows the rectangle to update spans
    * @param colCount the number of columns the rectangle to update spans
    */
   protected void updateBlocks(int row, int col, int rowCount, int colCount)
   {
      final Format gridFormat = getGridFormat();

      for(int currentRow = row; currentRow < row + rowCount; ++currentRow)
      {
         for(int currentCol = col; currentCol < col + colCount; ++currentCol)
         {
            JPanel guiBlock = new JPanel();
            Dimension size = new Dimension(_blockSize, _blockSize);
            guiBlock.setSize(size);
            guiBlock.setPreferredSize(size);
            
            Block block = getBlockAt(currentRow, currentCol);
            
            if(block != null)
            {
               guiBlock.setBackground(getColor(block));
               guiBlock.setBorder(BorderFactory.createRaisedBevelBorder());
            }
            
            final int componentIndex = gridFormat.getCols() * currentRow + currentCol;
            if(this.getComponentCount() > componentIndex)
            {
               this.remove(componentIndex);
            }
            
            _CONSTRAINTS.gridx = currentCol;
            _CONSTRAINTS.gridy = currentRow;
            
            this.add(guiBlock, _CONSTRAINTS, componentIndex);
         }
      }
      
      this.validate();
   }
   
   /**
    * Updates all blocks of this grid.
    */
   protected void updateAll()
   {
      Format gridFormat = getGridFormat();
      
      updateBlocks(0, 0, gridFormat.getRows(), gridFormat.getCols());
   }
   
   /**
    * Converts the {@link de.javagimmicks.games.jotris.model.Color}
    * of a given {@link Block} into an AWT {@link Color}.
    * @param block the {@link Block} to get the AWT {@link Color} for
    * @return the resulting AWT {@link Color}
    */
   protected static Color getColor(Block block)
   {
      switch(block.getColor())
      {
         case BLUE: return new Color(127,127, 255);
         case RED: return new Color(255, 127, 127);
         case YELLOW: return new Color(255, 255, 127);
         case GREEN: return new Color(127, 255, 127);
         case PURPLE: return new Color(255, 127, 255);
         case CYAN: return new Color(127, 255, 255);
         default: return new Color(127, 127, 127);
      }
   }

   /**
    * A {@link ComponentListener} implementation internally used for automatically
    * adjusting the block size when the component is resized.
    */
   protected final class BlockSizeComponentListener extends ComponentAdapter
   {
      public void componentResized(ComponentEvent e)
      {
         // Automatically adjust the block size when the component is resized
         Rectangle bounds = getBounds();
         Insets insets = getInsets();
         Format gridFormat = getGridFormat();

         // Calculate the block width and height according to the new size
         int blockWidth = (bounds.width - insets.left - insets.right) / gridFormat.getCols();
         int blockHeight = (bounds.height - insets.top - insets.bottom) / gridFormat.getRows();
         
         // Get the smaller one of both values
         int blockSize = Math.min(blockWidth, blockHeight);
         
         // Update the block size, if it has changed
         if(blockSize != _blockSize)
         {
            setBlockSize(blockSize);
         }
      }
   }
}
