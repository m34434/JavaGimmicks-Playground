package de.javagimmicks.games.towers.solver;

import de.javagimmicks.games.towers.model.Game;
import de.javagimmicks.games.towers.model.GameException;

public class Command
{
    private String _from;
    private String _to;
    
    public Command(String from, String to)
    {
        _from = from;
        _to = to;
    }

    public String getFrom()
    {
        return _from;
    }

    public String getTo()
    {
        return _to;
    }
    
    public void executeOn(Game game) throws GameException
    {
        game.moveDisc(_from, _to);
    }
    
    public String toString()
    {
        return "Move disc from '" + _from + "' to '" + _to + "'!";
    }
}
