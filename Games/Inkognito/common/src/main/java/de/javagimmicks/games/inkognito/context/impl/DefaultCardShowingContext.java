package de.javagimmicks.games.inkognito.context.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.javagimmicks.games.inkognito.context.CardShowingContext;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.CardType;
import de.javagimmicks.games.inkognito.model.Player;

public class DefaultCardShowingContext implements CardShowingContext
{
	private final Map<Player, Map<Player, Set<CardPair>>> m_oShownCards = new HashMap<Player, Map<Player, Set<CardPair>>>();
	private final Map<Player, Map<Player, Set<CardType>>> m_oShownIds = new HashMap<Player, Map<Player, Set<CardType>>>();
	
	public void reset()
	{
		m_oShownCards.clear();
		m_oShownIds.clear();
	}
	
	public boolean mayPlayerShowPair(Player oShowingPlayer, Player oAskingPlayer, CardPair oCardPair)
	{
		if(oCardPair.getCard1() == oCardPair.getCard2())
		{
			return false;
		}
		
		if(!oCardPair.containsCard(oShowingPlayer.getTelephoneCard()) && !oCardPair.containsCard(oShowingPlayer.getNameCard()))
		{
			return false;
		}
		
		return !getCreatePlayerShownCards(oShowingPlayer, oAskingPlayer).contains(oCardPair);
	}
	
	public void notifiyPlayerShow(Player oShowingPlayer, Player oAskingPlayer, CardPair oCardPair)
	{
		getCreatePlayerShownCards(oShowingPlayer, oAskingPlayer).add(oCardPair);
	}
	
	public boolean mayPlayerAskId(Player oAskingPlayer, Player oShowingPlayer)
	{
		return getCreatePlayerShownIds(oShowingPlayer, oAskingPlayer).size() <= 1;
	}
	
	public boolean mayPlayerShowId(Player oShowingPlayer, Player oAskingPlayer, Card oCard)
	{
		if(!oShowingPlayer.getId().containsCard(oCard))
		{
			return false;
		}
		
		return !getCreatePlayerShownIds(oShowingPlayer, oAskingPlayer).contains(oCard.getCardType());
	}
	
	public void notifiyPlayerShow(Player oShowingPlayer, Player oAskingPlayer, Card oCard)
	{
		getCreatePlayerShownIds(oShowingPlayer, oAskingPlayer).add(oCard.getCardType());
	}
	
	private Set<CardPair> getCreatePlayerShownCards(Player oShowingPlayer, Player oAskingPlayer)
	{
		Map<Player, Set<CardPair>> oPlayerShowCards = getCreatePlayerShownCards(oShowingPlayer);
		Set<CardPair> oResult = oPlayerShowCards.get(oAskingPlayer);
		
		if(oResult == null)
		{
			oResult = new HashSet<CardPair>();
			oPlayerShowCards.put(oAskingPlayer, oResult);
		}
		
		return oResult;
	}
	
	private Map<Player, Set<CardPair>> getCreatePlayerShownCards(Player oPlayer)
	{
		Map<Player, Set<CardPair>> oResult = m_oShownCards.get(oPlayer);
		
		if(oResult == null)
		{
			oResult = new HashMap<Player, Set<CardPair>>();
			m_oShownCards.put(oPlayer, oResult);
		}
		
		return oResult;
	}
	
	private Set<CardType> getCreatePlayerShownIds(Player oShowingPlayer, Player oAskingPlayer)
	{
		Map<Player, Set<CardType>> oPlayerShowCards = getCreatePlayerShownIds(oShowingPlayer);
		Set<CardType> oResult = oPlayerShowCards.get(oAskingPlayer);
		
		if(oResult == null)
		{
			oResult = new HashSet<CardType>();
			oPlayerShowCards.put(oAskingPlayer, oResult);
		}
		
		return oResult;
	}
	
	private Map<Player, Set<CardType>> getCreatePlayerShownIds(Player oPlayer)
	{
		Map<Player, Set<CardType>> oResult = m_oShownIds.get(oPlayer);
		
		if(oResult == null)
		{
			oResult = new HashMap<Player, Set<CardType>>();
			m_oShownIds.put(oPlayer, oResult);
		}
		
		return oResult;
	}
		
}
