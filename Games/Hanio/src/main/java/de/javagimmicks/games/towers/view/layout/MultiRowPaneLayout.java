package de.javagimmicks.games.towers.view.layout;

import java.awt.Dimension;
import java.awt.Point;

public class MultiRowPaneLayout extends TowerGamePaneLayout
{
	private final int _rowLength;
	
    public MultiRowPaneLayout(int diskCount, int stickCount, int rowLength)
    {
        super(diskCount, stickCount);
        
        _rowLength = rowLength;
    }

    public Dimension getPaneSize()
    {
    	int numRows = ((_stickCount - 1) / _rowLength) + 1;
        int numCols = (numRows == 1) ? _stickCount : _rowLength;

        return new Dimension(numCols * getSectionWidth(), numRows * getSectionHeight());
    }
    
    public Point getSectionPoint(int section, int height)
    {
        int sectionWidth = getSectionWidth();
        int sectionXIndent = (section % _rowLength) * sectionWidth;
        int discXIndent = sectionWidth / 2;
        
        int sectionYIndent = (section / _rowLength) * getSectionHeight();
        int discYIndent = (_discCount - height + 1) * _discHeight;
        
        return new Point(sectionXIndent + discXIndent, sectionYIndent + discYIndent);
    }

    public int getSectionForPostition(Point point)
    {
        int column = point.x / getSectionWidth();
        int row = point.y / getSectionHeight();

        int section = row * _rowLength + column;
        
        if(section < _stickCount)
        {
            return section;
        }
        else
        {
            return -1;
        }
    }
}
