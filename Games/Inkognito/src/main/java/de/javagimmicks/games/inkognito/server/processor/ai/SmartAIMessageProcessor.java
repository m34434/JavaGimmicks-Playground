package de.javagimmicks.games.inkognito.server.processor.ai;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.context.VisitsContext;
import de.javagimmicks.games.inkognito.message.answer.LocationAnswer;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Player;

public class SmartAIMessageProcessor extends NormalAIMessageProcessor
{
	public SmartAIMessageProcessor(String sNameBase)
	{
		super(sNameBase);
	}
	
	public SmartAIMessageProcessor()
	{
		this("Smart");
	}

	
	@Override
	protected NameAnswer processAskMeetMessage(AskMeetMessage oMessage)
	{
		List<Player> oWishList = getPlayerWishList(false);
		
		for(Player oPlayer : oWishList)
		{
			if(m_oGameContext.getCardShowingContext().mayPlayerAskId(m_oPlayer, oPlayer))
			{
				return new NameAnswer(oPlayer.getName());
			}
		}
		
		return null;
	}

	@Override
	protected LocationAnswer _processAskMoveMessage(AskMoveMessage oMessage)
	{
		VisitsContext oVisitsContext = m_oGameContext.getVisitsContext();
		LocationsContext oLocationsContext = m_oGameContext.getLocationsContext();

		List<Player> oWishList = isAllKnown() ? Collections.singletonList(getMyPartner()) : getPlayerWishList(true);
		List<Location> oMyFreeLocations = oVisitsContext.getVisitableLocations(m_oPlayer);
		
		// Try to meet the favourite player's directly, if he has already moved
		for(Player oPlayer : oWishList)
		{
			Location oLocation = oLocationsContext.getCurrentLocation(oPlayer);
			if(oLocation != null && oMyFreeLocations.contains(oLocation) && oLocationsContext.getVisitorCount(oLocation) == 1)
			{
				return new LocationAnswer(oLocation);
			}
		}

		// Go to a location where the desired meet partner can also go
		for(Player oPlayer : oWishList)
		{
			if(oLocationsContext.getCurrentLocation(oPlayer) != null)
			{
				continue;
			}

			List<Location> oOtherOpenVisits = new LinkedList<Location>(oVisitsContext.getVisitableLocations(oPlayer));
			oOtherOpenVisits.retainAll(oMyFreeLocations);
			
			for(Location oLocation : oOtherOpenVisits)
			{
				if(oLocationsContext.getVisitorCount(oLocation) == 0)
				{
					return new LocationAnswer(oLocation);
				}
			}
		}

		// Try to block other pairs
		for(Location oLocation : oMyFreeLocations)
		{
			if(oLocationsContext.getVisitors(oLocation).size() == 2)
			{
				return new LocationAnswer(oLocation);
			}
		}
		
		// Try to find a safe place were no other one can move
		for(Location oLocation : oMyFreeLocations)
		{
			if(isSafeLocation(oLocation))
			{
				return new LocationAnswer(oLocation);
			}
		}
		
		return new LocationAnswer(oMyFreeLocations.get(RANDOM.nextInt(oMyFreeLocations.size())));
	}
	
	protected boolean isSafeLocation(Location oLocation)
	{
		VisitsContext oVisitsContext = m_oGameContext.getVisitsContext();
		LocationsContext oLocationsContext = m_oGameContext.getLocationsContext();
		
		int iVisitorCount = oLocationsContext.getVisitorCount(oLocation);
		
		// If there are already two players at the location, it is definitely safe
		if(iVisitorCount >= 2)
		{
			return true;
		}
		
		// If there is only one visitor, it is definitely not safe
		else if(iVisitorCount == 1)
		{
			return false;
		}
		
		// Last, we must check, if any opponent can move here
		for(Player oPlayer : m_oOpponents)
		{
			if(oVisitsContext.isLocationVisitable(oPlayer, oLocation))
			{
				return false;
			}
		}
		
		return true;
	}
	
	protected Player getMyPartner()
	{
		for(Player oPlayer : m_oOpponents)
		{
			if(isMyPartner(oPlayer))
			{
				return oPlayer;
			}
		}
		
		return null;
	}
	
	protected List<Player> getPlayerWishList(boolean bSkipNonPositive)
	{
		final ArrayList<MeetWish> oMeetList = new ArrayList<MeetWish>(m_oOpponents.size());
		
		for(Player oPlayer : m_oOpponents)
		{
			MeetWish oMeetWish = new MeetWish(oPlayer);
			
			if(!bSkipNonPositive || oMeetWish.POINTS > 0)
			{
				oMeetList.add(new MeetWish(oPlayer));
			}
		}
		
		Collections.sort(oMeetList);
		
		return new AbstractList<Player>()
		{
			public Player get(int index)
			{
				return oMeetList.get(index).PLAYER;
			}

			public int size()
			{
				return oMeetList.size();
			}
		};
	}
	
	protected class MeetWish implements Comparable<MeetWish>
	{
		public final Player PLAYER;
		public final int POINTS;
		
		public MeetWish(final Player oPlayer)
		{
			PLAYER = oPlayer;
			POINTS = getPlayerMeetPoints(oPlayer);
		}
		
		public int compareTo(MeetWish o)
		{
			return this.POINTS - o.POINTS;
		}

		private int getPlayerMeetPoints(Player oPlayer)
		{
			int iResult = 0;
			
			iResult += (m_oCardAnalysingContext.getPossiblePlayerNames(oPlayer).size() - 1) * 3;
			iResult += (m_oCardAnalysingContext.getPossiblePlayerTelephones(oPlayer).size() - 1) * 2;
			
			if(isMyPartner(oPlayer))
			{
				iResult -= 3;
			}
			
			return iResult;
		}
	}
}
