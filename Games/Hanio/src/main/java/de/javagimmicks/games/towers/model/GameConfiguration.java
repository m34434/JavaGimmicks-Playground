package de.javagimmicks.games.towers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameConfiguration implements Serializable
{
    private static final long serialVersionUID = -412234193052740200L;

    private final int _discCount;
    private final List<String> _stickNames;
    private final String _initialStickName;
    
    public GameConfiguration(List<String> stickNames, String initialStickName, int discCount)
    {
        if(discCount < 2)
        {
            throw new IllegalArgumentException("Height must be greater than 2!");
        }
        
        if(stickNames.size() < 3)
        {
            throw new IllegalArgumentException("At least 3 sticks are needed for playing!");
        }
        
        if(!stickNames.contains(initialStickName))
        {
            throw new IllegalArgumentException("Initial stick '" + initialStickName + "' not found in given stick names!");
        }
        
        _discCount = discCount;
        _stickNames = Collections.unmodifiableList(new ArrayList<String>(stickNames));
        _initialStickName = initialStickName;
    }

    public int getDiscCount()
    {
        return _discCount;
    }
    
    public int getStickCount()
    {
        return _stickNames.size();
    }

    public String getInitialStickName()
    {
        return _initialStickName;
    }

    public List<String> getStickNames()
    {
        return _stickNames;
    }
    
    public Game createNewGame()
    {
        return new Game(this);
    }
}
