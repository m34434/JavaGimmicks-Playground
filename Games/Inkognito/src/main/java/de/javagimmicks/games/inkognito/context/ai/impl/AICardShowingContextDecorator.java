package de.javagimmicks.games.inkognito.context.ai.impl;

import de.javagimmicks.games.inkognito.context.CardShowingContext;
import de.javagimmicks.games.inkognito.context.ai.AICardShowingContext;
import de.javagimmicks.games.inkognito.context.ai.CardAnalysingContext;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Player;

public class AICardShowingContextDecorator implements AICardShowingContext
{
	private final CardAnalysingContext m_oCardAnalysingContext;
	private final CardShowingContext m_oCardShowingContext;
	
	public AICardShowingContextDecorator(CardShowingContext oCardShowingContext, CardAnalysingContext oCardAnalysingContext)
	{
		m_oCardShowingContext = oCardShowingContext;
		m_oCardAnalysingContext = oCardAnalysingContext;
	}
	
	public AICardShowingContextDecorator(CardShowingContext oCardShowingContext)
	{
		this(oCardShowingContext, new DefaultCardAnalysingContext());
	}
	
	public CardShowingContext getCardShowingContext()
	{
		return m_oCardShowingContext;
	}
	
	public CardAnalysingContext getCardAnalysingContext()
	{
		return m_oCardAnalysingContext;
	}

	public void notifiyPlayerShow(Player oShowingPlayer, Player oAskingPlayer, CardPair oCardPair)
	{
		m_oCardShowingContext.notifiyPlayerShow(oShowingPlayer, oAskingPlayer, oCardPair);
		
		m_oCardAnalysingContext.notifyCardPairSeen(oShowingPlayer, oCardPair);
	}

	public void notifiyPlayerShow(Player oShowingPlayer, Player oAskingPlayer, Card oCard)
	{
		m_oCardShowingContext.notifiyPlayerShow(oShowingPlayer, oAskingPlayer, oCard);

		m_oCardAnalysingContext.notifyCardSeen(oShowingPlayer, oCard);
	}

	public void reset()
	{
		m_oCardShowingContext.reset();
		
		m_oCardAnalysingContext.reset();
	}

	public boolean mayPlayerAskId(Player oAskingPlayer, Player oShowingPlayer)
	{
		return m_oCardShowingContext.mayPlayerAskId(oAskingPlayer, oShowingPlayer);
	}

	public boolean mayPlayerShowId(Player oShowingPlayer, Player oAskingPlayer, Card oCard)
	{
		return m_oCardShowingContext.mayPlayerShowId(oShowingPlayer, oAskingPlayer, oCard);
	}

	public boolean mayPlayerShowPair(Player oShowingPlayer, Player oAskingPlayer, CardPair oCardPair)
	{
		return m_oCardShowingContext.mayPlayerShowPair(oShowingPlayer, oAskingPlayer, oCardPair);
	}

}
