package de.javagimmicks.games.towers.model;

import java.util.HashMap;
import java.util.Stack;
import java.util.Map.Entry;

public class Game
{
    private HashMap<String, Stack<Disc>> _sticks = new HashMap<String, Stack<Disc>>();
    private GameConfiguration _configuration;    
    
    Game(GameConfiguration configuration)
    {
        _configuration = configuration;
        
        init();
    }
    
    public void moveDisc(String from, String to) throws GameException
    {
        Stack<Disc> fromStick = _sticks.get(from);
        Stack<Disc> toStick = _sticks.get(to);
        
        if(fromStick == null)
        {
            throw new GameException("Stick '" + from + "' is unknown!");
        }
        
        if(toStick == null)
        {
            throw new GameException("Stick '" + to + "' is unknown!");
        }
        
        if(fromStick.isEmpty())
        {
            throw new GameException("Stick '" + from + "' mustn't be empty!");
        }
        
        if(!stickAccepts(toStick, fromStick.peek()))
        {
            throw new GameException("Illegal move from '" + from +"' to '" + to + "'!");
        }
        
        // Move the disc
        toStick.push(fromStick.pop());
    }
    
    public void moveDisc(int from, int to) throws GameException
    {
        String fromStickName = _configuration.getStickNames().get(from);
        String toStickName = _configuration.getStickNames().get(to);
        
        moveDisc(fromStickName, toStickName);
    }
    
    public boolean isSolved()
    {
        String stickContainingAllDiscs = getAllContainingStickName();
        
        return (stickContainingAllDiscs != null) && (!stickContainingAllDiscs.equals(_configuration.getInitialStickName()));
    }
    
    public GameConfiguration getGameConfiguration()
    {
        return _configuration;
    }
    
    public Stack<Disc> getStick(String stickName)
    {
        return _sticks.get(stickName);
    }
    
    public Stack<Disc> getStick(int position)
    {
        return _sticks.get(_configuration.getStickNames().get(position));
    }

    protected boolean stickAccepts(Stack<Disc> stick, Disc disc)
    {
        return (stick.isEmpty() || disc.fitsOn(stick.peek()));
    }
    
    protected String getAllContainingStickName()
    {
        for(Entry<String, Stack<Disc>> entry : _sticks.entrySet())
        {
            if(entry.getValue().size() == _configuration.getDiscCount())
            {
                return entry.getKey();
            }
        }
        
        return null;
    }
    
    protected void init()
    {
        
        // Prepare the sticks
        for(String stickName : _configuration.getStickNames())
        {
            _sticks.put(stickName, new Stack<Disc>());
        }

        // Put discs on initial stick
        Stack<Disc> initialStick = _sticks.get(_configuration.getInitialStickName());
        try
        {
            for(int i = _configuration.getDiscCount(); i >= 0; --i)
            {
                initialStick.push(new Disc(i));
            }
        }
        catch (Exception e)
        {
            // Cannot occur
            assert false;
        }
    }
}
