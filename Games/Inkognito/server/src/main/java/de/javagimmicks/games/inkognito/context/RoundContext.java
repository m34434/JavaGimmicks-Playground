package de.javagimmicks.games.inkognito.context;

public interface RoundContext
{
	public void roundFinished();

	public int getRoundNumber();

	public void reset();
}