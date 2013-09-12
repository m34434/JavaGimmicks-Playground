package de.javagimmicks.games.inkognito.context.impl;

import de.javagimmicks.games.inkognito.context.CardShowingContext;
import de.javagimmicks.games.inkognito.context.GameContext;
import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.context.RoundContext;
import de.javagimmicks.games.inkognito.context.VisitsContext;

public class DefaultGameContext implements GameContext
{
	private final PlayerContext m_oPlayerContext;
	private final CardShowingContext m_oCardShowingContext;
	private final LocationsContext m_oLocationsContext;
	private final VisitsContext m_oVisitsContext;
	private final RoundContext m_oRoundContext;
	
	public DefaultGameContext(final PlayerContext oPlayerContext, final CardShowingContext oCardShowingContext, final LocationsContext oLocationsContext, final VisitsContext oVisitsContext, final RoundContext oRoundContext)
	{
		m_oPlayerContext = oPlayerContext;
		m_oCardShowingContext = oCardShowingContext;
		m_oLocationsContext = oLocationsContext;
		m_oVisitsContext = oVisitsContext;
		m_oRoundContext = oRoundContext;
	}
	
	public DefaultGameContext()
	{
		m_oPlayerContext = new DefaultPlayerContext();
		m_oCardShowingContext = new DefaultCardShowingContext();
		m_oLocationsContext = new DefaultLocationsContext();
		m_oVisitsContext = new DefaultVisitsContext(m_oLocationsContext);
		m_oRoundContext = new DefaultRoundContext(m_oVisitsContext, m_oLocationsContext, m_oPlayerContext);
	}
	
	public void reset()
	{
		m_oPlayerContext.reset();
		m_oCardShowingContext.reset();
		m_oLocationsContext.reset();
		m_oVisitsContext.reset();
		m_oRoundContext.reset();
	}
	
	public CardShowingContext getCardShowingContext()
	{
		return m_oCardShowingContext;
	}
	public LocationsContext getLocationsContext()
	{
		return m_oLocationsContext;
	}
	public PlayerContext getPlayerContext()
	{
		return m_oPlayerContext;
	}
	public VisitsContext getVisitsContext()
	{
		return m_oVisitsContext;
	}
	
	public RoundContext getRoundContext()
	{
		return m_oRoundContext;
	}
	
}
