package de.javagimmicks.games.towers.view.layout;

import java.awt.Dimension;
import java.awt.Point;

public class SingleRowPaneLayout extends TowerGamePaneLayout
{
    public SingleRowPaneLayout(int diskCount, int stickCount)
    {
        super(diskCount, stickCount);
    }

    public Dimension getPaneSize()
    {
        int sectionHeight = (_discCount + 3) * getDiscHeight();
        int paneWidth = _stickCount * getSectionWidth();

        return new Dimension(paneWidth, sectionHeight);
    }
    
    public Point getSectionPoint(int section, int height)
    {
        int sectionWidth = getSectionWidth();
        
        return new Point(sectionWidth / 2 + section * sectionWidth, (_discCount - height + 1) * _discHeight);
    }

    public int getSectionForPostition(Point point)
    {
        int sectionWidth = getSectionWidth(); 
        
        int section = point.x / sectionWidth;
        
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
