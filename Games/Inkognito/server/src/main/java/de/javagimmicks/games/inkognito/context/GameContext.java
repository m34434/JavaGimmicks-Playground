package de.javagimmicks.games.inkognito.context;

public interface GameContext
{
	public CardShowingContext getCardShowingContext();
	public LocationsContext getLocationsContext();
	public PlayerContext getPlayerContext();
	public VisitsContext getVisitsContext();
	public RoundContext getRoundContext();

	public void reset();
}