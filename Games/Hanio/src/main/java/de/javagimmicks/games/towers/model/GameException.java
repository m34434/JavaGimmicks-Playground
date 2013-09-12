package de.javagimmicks.games.towers.model;

public class GameException extends Exception
{
    private static final long serialVersionUID = -2018998108490921248L;

    public GameException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GameException(String message)
    {
        super(message);
    }
}
