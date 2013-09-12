package de.javagimmicks.games.towers.model;

public class Disc
{
    private int _width;

    public Disc(int width)
    {
        if(width < 1)
        {
            throw new IllegalArgumentException("Discs must have a width of '1' or greater!");
        }
        
        _width = width;
    }
    
    public int getWidth()
    {
        return _width;
    }
    
    public boolean fitsOn(Disc other)
    {
        return other._width > _width;
    }
}
