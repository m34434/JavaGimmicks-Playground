package de.javagimmicks.games.towers.view.layout;

import java.awt.Dimension;
import java.awt.Point;

public abstract class TowerGamePaneLayout
{
    protected final int _discWidthBase;
    protected final int _discHeight;
    protected final int _discCount;
    protected final int _stickCount;
    
    public TowerGamePaneLayout(int discCount, int stickCount, int discWidthBase, int discHeight)
    {
        _discCount = discCount;
        _stickCount = stickCount;
        _discWidthBase = discWidthBase;
        _discHeight = discHeight;
    }
    
    public TowerGamePaneLayout(int discCount, int stickCount)
    {
        this(discCount, stickCount, 30, 20);
    }
    
    public int getSectionHeight()
    {
    	return (_discCount + 3) * _discHeight;
    }
    
    public int getSectionWidth()
    {
        return (_discCount + 2) * _discWidthBase;
    }
    
    public int getDiscWidthBase()
    {
        return _discWidthBase;
    }
    
    public int getDiscHeight()
    {
        return _discHeight;
    }
    
    public abstract Dimension getPaneSize();
    public abstract Point getSectionPoint(int section, int height);
    public abstract int getSectionForPostition(Point point);
}
