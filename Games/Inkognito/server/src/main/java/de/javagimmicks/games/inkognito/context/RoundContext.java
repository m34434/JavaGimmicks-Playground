package de.javagimmicks.games.inkognito.context;

import de.javagimmicks.games.inkognito.model.Location;

public class RoundContext
{
   private final VisitsContext m_oVisitsContext;
   private final LocationsContext m_oLocationsContext;
   private final PlayerContext m_oPlayerContext;

   private int m_iRoundNumber = 0;
   
   public void reset()
   {
      m_iRoundNumber = 0;
   }
   
   public RoundContext(final VisitsContext oVisitsContext, final LocationsContext oLocationsContext, final PlayerContext oPlayerContext)
   {
      m_oVisitsContext = oVisitsContext;
      m_oLocationsContext = oLocationsContext;
      m_oPlayerContext = oPlayerContext;
   }
   
   public void roundFinished()
   {
      ++m_iRoundNumber;
      
      if(m_iRoundNumber % Location.values().length == 0)
      {
         m_oVisitsContext.reset();
      }
      
      m_oPlayerContext.rotatePlayers();
      m_oLocationsContext.reset();
   }
   
   public int getRoundNumber()
   {
      return m_iRoundNumber;
   }
}