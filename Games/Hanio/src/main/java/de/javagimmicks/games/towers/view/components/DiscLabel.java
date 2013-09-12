package de.javagimmicks.games.towers.view.components;

import java.io.Serializable;

import javax.swing.JLabel;

public class DiscLabel extends JLabel implements Serializable
{
    private static final long serialVersionUID = -8880239580151626333L;

    private final int _width;
    
    public DiscLabel(int width)
    {
        super(String.valueOf(width));
        _width = width;
    }
    
    public int getWidthIndex()
    {
        return _width;
    }
}

