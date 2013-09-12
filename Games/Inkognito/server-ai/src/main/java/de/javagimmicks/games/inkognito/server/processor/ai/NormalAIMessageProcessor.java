package de.javagimmicks.games.inkognito.server.processor.ai;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.javagimmicks.games.inkognito.context.CardShowingContext;
import de.javagimmicks.games.inkognito.context.GameContext;
import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.context.RoundContext;
import de.javagimmicks.games.inkognito.context.VisitsContext;
import de.javagimmicks.games.inkognito.context.ai.AICardShowingContext;
import de.javagimmicks.games.inkognito.context.ai.CardAnalysingContext;
import de.javagimmicks.games.inkognito.context.ai.impl.AICardShowingContextDecorator;
import de.javagimmicks.games.inkognito.context.ai.impl.DefaultCardAnalysingContext;
import de.javagimmicks.games.inkognito.context.impl.DefaultCardShowingContext;
import de.javagimmicks.games.inkognito.context.impl.DefaultGameContext;
import de.javagimmicks.games.inkognito.context.impl.DefaultLocationsContext;
import de.javagimmicks.games.inkognito.context.impl.DefaultPlayerContext;
import de.javagimmicks.games.inkognito.context.impl.DefaultRoundContext;
import de.javagimmicks.games.inkognito.context.impl.DefaultVisitsContext;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.ReportIdMessage;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Player;

public class NormalAIMessageProcessor extends CrazyAIMessageProcessor
{
	protected final CardAnalysingContext m_oCardAnalysingContext;
	
	protected NormalAIMessageProcessor(GameContext oGameContext, String sPlayerNameBase)
	{
		super(decorateGameContext(oGameContext), sPlayerNameBase);
		m_oCardAnalysingContext = ((AICardShowingContext)m_oGameContext.getCardShowingContext()).getCardAnalysingContext();
	}

	public NormalAIMessageProcessor(String sPlayerNameBase)
	{
		this(createGameContext(), sPlayerNameBase);
	}
	
	public NormalAIMessageProcessor()
	{
		this("Normal");
	}
	
	@Override
	protected CardAnswer _processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage)
	{
		Player oOtherPlayer = getPlayerByName(oMessage.getPlayerName());
		
		// Give the partner as much info as possible
		if(isMyPartner(oOtherPlayer))
		{
			// Try with the name card first
			if(m_oGameContext.getCardShowingContext().mayPlayerShowId(m_oPlayer, oOtherPlayer, m_oPlayer.getNameCard()))
			{
				return new CardAnswer(m_oPlayer.getNameCard());
			}
			else
			{
				return new CardAnswer(m_oPlayer.getTelephoneCard());
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
		Player oOtherPlayer = getPlayerByName(oMessage.getPlayerName());
		
		// Give the partner as much info as possible
		if(isMyPartner(oOtherPlayer))
		{
			// Make the phone call, if everything is known
			if(isAllKnown())
			{
				return new ShowAnswer(getSolutionPairs());
			}
			
			CardPair oMyId = m_oPlayer.getId();
			
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
	protected void processReportIdMessage(ReportIdMessage oMessage)
	{
		super.processReportIdMessage(oMessage);

		m_oCardAnalysingContext.init(m_oOpponents, getOpponentNameCards(), getOpponentTelephoneCards());
	}

	@Override
	protected Queue<CardPair> generateShowAnswers()
	{
		Queue<CardPair>oResult = new LinkedList<CardPair>();
		
		// Get my own id cards
		Card oOwnNameCard = m_oPlayer.getNameCard();
		Card oOwnTelephoneCard = m_oPlayer.getTelephoneCard();
		
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

	protected boolean isMyPartner(Player oOtherPlayer)
	{
		Card oMyNameCard = m_oPlayer.getNameCard();
		Card oOtherNameCard = oOtherPlayer.getNameCard();

		return (oOtherNameCard != null && Card.getPartner(oOtherNameCard) == oMyNameCard);
	}
	
	protected boolean isMyPartner(String sPlayerName)
	{
		return isMyPartner(getPlayerByName(sPlayerName));
	}
	
	protected boolean isAllKnown()
	{
		for(Player oPlayer : m_oOpponents)
		{
			if(!oPlayer.isIdKnown())
			{
				return false;
			}
		}
		
		return true;
	}
	
	protected List<CardPair> getSolutionPairs()
	{
		final ArrayList<Player> oPlayerList = new ArrayList<Player>(m_oGameContext.getPlayerContext().getInitialPlayers());
		
		return new AbstractList<CardPair>()
		{
			public CardPair get(int index)
			{
				return oPlayerList.get(index).getId();
			}

			public int size()
			{
				return oPlayerList.size();
			}
		};
	}

	private static GameContext createGameContext()
	{
		PlayerContext oPlayerContext = new DefaultPlayerContext();
		LocationsContext oLocationsContext = new DefaultLocationsContext();
		AICardShowingContext oCardShowingContext = new AICardShowingContextDecorator(new DefaultCardShowingContext(), new DefaultCardAnalysingContext());
		VisitsContext oVisitsContext = new DefaultVisitsContext(oLocationsContext);
		RoundContext oRoundContext = new DefaultRoundContext(oVisitsContext, oLocationsContext, oPlayerContext);
		
		return new DefaultGameContext(oPlayerContext, oCardShowingContext, oLocationsContext, oVisitsContext, oRoundContext);
	}
	
	private static GameContext decorateGameContext(final GameContext oGameContext)
	{
		if(oGameContext.getCardShowingContext() instanceof AICardShowingContext)
		{
			return oGameContext;
		}

		return new AICardShowingContextGameContextDecorator(oGameContext, new DefaultCardAnalysingContext());
	}
	
	protected static class AICardShowingContextGameContextDecorator implements GameContext
	{
		private final GameContext m_oBaseContext;
		private final CardAnalysingContext m_oCardAnalysingContext;
		
		public AICardShowingContextGameContextDecorator(final GameContext oBaseContext, CardAnalysingContext oCardAnalysingContext)
		{
			m_oBaseContext = oBaseContext;
			m_oCardAnalysingContext = oCardAnalysingContext;
		}

		public CardShowingContext getCardShowingContext()
		{
			return new AICardShowingContextDecorator(m_oBaseContext.getCardShowingContext(), m_oCardAnalysingContext);
		}

		public LocationsContext getLocationsContext()
		{
			return m_oBaseContext.getLocationsContext();
		}

		public PlayerContext getPlayerContext()
		{
			return m_oBaseContext.getPlayerContext();
		}

		public RoundContext getRoundContext()
		{
			return m_oBaseContext.getRoundContext();
		}

		public VisitsContext getVisitsContext()
		{
			return m_oBaseContext.getVisitsContext();
		}

		public void reset()
		{
			m_oBaseContext.reset();
			m_oCardAnalysingContext.reset();
		}
		
		public GameContext getBaseContext()
		{
			return m_oBaseContext;
		}
	}
}
