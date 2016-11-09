package de.javagimmicks.games.inkognito.server.processor.ai;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.javagimmicks.games.inkognito.context.GameContext;
import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.context.ai.CardAnalysingContext;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.ReportIdMessage;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Person;

public class NormalAIMessageProcessor extends CrazyAIMessageProcessor
{
	protected final CardAnalysingContext m_oCardAnalysingContext;
	
	protected NormalAIMessageProcessor(GameContext oGameContext, String name)
	{
		super(oGameContext, name);
		
		m_oCardAnalysingContext = new CardAnalysingContext(m_oGameContext.getPlayerContext());
	}

	public NormalAIMessageProcessor(String name)
	{
		this(new GameContext(), name);
	}
	
	public NormalAIMessageProcessor()
	{
		this("Normal");
	}
	
	@Override
	protected CardAnswer _processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage)
	{
		Person oOtherPlayer = oMessage.getPlayer();
		
		// Give the partner as much info as possible
		if(isMyPartner(oOtherPlayer))
		{
			// Try with the name card first
			if(m_oGameContext.getCardShowingContext().mayPlayerShowId(m_oPlayer, oOtherPlayer, getOwnNameCard()))
			{
				return new CardAnswer(getOwnNameCard());
			}
			else
			{
				return new CardAnswer(getOwnTelephoneCard());
			}
		}
		// If partner was not met, process as normal
		else
		{
			return super._processAskShowEnvoyMessage(oMessage);
		}
	}

	@Override
	protected ShowAnswer _processAskShowMessage(AskShowMessage oMessage)
	{
		Person oOtherPlayer = oMessage.getPlayer();
		
		// Give the partner as much info as possible
		if(isMyPartner(oOtherPlayer))
		{
			// Make the phone call, if everything is known
			if(isAllKnown())
			{
				return new ShowAnswer(getSolutionPairs());
			}
			
			CardPair oMyId = getOwnId();
			
			// Try with the name card first
			if(m_oGameContext.getCardShowingContext().mayPlayerShowPair(m_oPlayer, oOtherPlayer, oMyId))
			{
				return new ShowAnswer(oMyId);
			}
		}

		// If partner was not met or partner is not known, process as normal
		return super._processAskShowMessage(oMessage);
	}

	@Override
	public void processReportIdMessage(ReportIdMessage oMessage)
	{
		super.processReportIdMessage(oMessage);

		m_oCardAnalysingContext.init(m_oOpponents, getOpponentNameCards(), getOpponentTelephoneCards());
	}

	@Override
	protected Queue<CardPair> generateShowAnswers()
	{
		Queue<CardPair>oResult = new LinkedList<CardPair>();
		
		// Get my own id cards
		Card oOwnNameCard = getOwnNameCard();
		Card oOwnTelephoneCard = getOwnTelephoneCard();
		
		// Build a list with other telephone cards
		final ArrayList<Card> oTelephoneCards = new ArrayList<Card>(getOpponentTelephoneCards());
		
		// Build a list with other name cards
		final ArrayList<Card> oNameCards = new ArrayList<Card>(getOpponentNameCards());
		
		// Tel/Tel combinations
		Collections.shuffle(oTelephoneCards);
		for(Card oTelephoneCard : oTelephoneCards)
		{
			oResult.offer(shuffledCardPair(oTelephoneCard, oOwnTelephoneCard));
		}

		// Tel/Name combinations
		Collections.shuffle(oNameCards);
		for(Card oNameCard : oNameCards)
		{
			oResult.offer(shuffledCardPair(oNameCard, oOwnTelephoneCard));
		}

		// Name/Name combinations
		Collections.shuffle(oNameCards);
		for(Card oNameCard : oNameCards)
		{
			oResult.offer(shuffledCardPair(oNameCard, oOwnNameCard));
		}

		// Name/Tel combinations
		Collections.shuffle(oTelephoneCards);
		for(Card oTelephoneCard : oTelephoneCards)
		{
			oResult.offer(shuffledCardPair(oTelephoneCard, oOwnNameCard));
		}
		
		oResult.offer(shuffledCardPair(oOwnNameCard, oOwnTelephoneCard));

		return oResult;
	}

	protected boolean isMyPartner(Person oOtherPlayer)
	{
		Card oMyNameCard = getOwnNameCard();
		Card oOtherNameCard = m_oGameContext.getPlayerContext().getNameCard(oOtherPlayer);

		return (oOtherNameCard != null && Card.getPartner(oOtherNameCard) == oMyNameCard);
	}
	
	protected boolean isAllKnown()
	{
	   PlayerContext playerContext = m_oGameContext.getPlayerContext();

	   for(Person oPlayer : m_oOpponents)
		{
         if(!playerContext.isIdKnown(oPlayer))
			{
				return false;
			}
		}
		
		return true;
	}
	
	protected List<CardPair> getSolutionPairs()
	{
		final List<Person> oPlayerList = PlayerContext.getPlayers();
		final PlayerContext playerContext = m_oGameContext.getPlayerContext();
		
		return new AbstractList<CardPair>()
		{
			public CardPair get(int index)
			{
				return playerContext.getId(oPlayerList.get(index));
			}

			public int size()
			{
				return oPlayerList.size();
			}
		};
	}
}
