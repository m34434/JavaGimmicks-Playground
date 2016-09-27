package de.javagimmicks.games.inkognito.server.processor.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import de.javagimmicks.games.inkognito.context.GameContext;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.message.answer.LocationAnswer;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.ReportEndMessage;
import de.javagimmicks.games.inkognito.message.message.ReportExitMessage;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.CardType;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Player;

public class CrazyAIMessageProcessor extends AbstractAIMessageProcessor
{
	protected static final Random RANDOM = new Random();
	protected final Map<Player, Queue<CardPair>> m_oShowAnswers = new HashMap<Player, Queue<CardPair>>();
	
	private List<Card> m_oOpponentTelephoneCards;
	private List<Card> m_oOppenentNameCards;

	protected CrazyAIMessageProcessor(GameContext oGameContext, String sPlayerNameBase)
	{
		super(oGameContext, sPlayerNameBase);
	}

	public CrazyAIMessageProcessor(String sPlayerNameBase)
	{
		this(new GameContext(), sPlayerNameBase);
	}
	
	public CrazyAIMessageProcessor()
	{
		this("Crazy");
	}
	
	protected void initNewGame()
	{
		super.initNewGame();
		
		m_oShowAnswers.clear();
		m_oOppenentNameCards = null;
		m_oOpponentTelephoneCards = null;
	}
	
	protected LocationAnswer _processAskMoveMessage(AskMoveMessage oMessage)
	{
		List<Location> oLocationList = m_oGameContext.getVisitsContext().getVisitableLocations(m_oPlayer); 
		return new LocationAnswer(oLocationList.get(RANDOM.nextInt(oLocationList.size())));
	}

	protected CardAnswer _processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage)
	{
		Player oOtherPlayer = getPlayerByName(oMessage.getPlayerName());
		
		Card oOwnNameCard = m_oPlayer.getNameCard();
		Card oOwnTelephoneCard = m_oPlayer.getTelephoneCard();
		
		if(m_oGameContext.getCardShowingContext().mayPlayerShowId(m_oPlayer, oOtherPlayer, oOwnTelephoneCard))
		{
			return new CardAnswer(oOwnTelephoneCard);
		}
		else
		{
			return new CardAnswer(oOwnNameCard);
		}
	}

	protected ShowAnswer _processAskShowMessage(AskShowMessage oMessage)
	{
		Player oOtherPlayer = getPlayerByName(oMessage.getPlayerName());
		Queue<CardPair> oShowAnswers = m_oShowAnswers.get(oOtherPlayer);
		
		if(oShowAnswers == null)
		{
			oShowAnswers = generateShowAnswers();
			m_oShowAnswers.put(oOtherPlayer, oShowAnswers);
		}
		
		CardPair oCardPair = oShowAnswers.poll();
		return (oCardPair == null) ? null : new ShowAnswer(oCardPair);
	}

	protected NameAnswer processAskMeetMessage(AskMeetMessage oMessage)
	{
		for(Player oOtherPlayer : m_oOpponents)
		{
			if(m_oGameContext.getCardShowingContext().mayPlayerAskId(m_oPlayer, oOtherPlayer))
			{
				return new NameAnswer(oOtherPlayer.getName());
			}
		}
		
		return null;
	}

	protected void processReportEndMessage(ReportEndMessage oMessage)
	{
	}

	protected void processReportExitMessage(ReportExitMessage oMessage)
	{
	}

	protected static CardPair shuffledCardPair(Card oCard1, Card oCard2)
	{
		if(RANDOM.nextBoolean())
		{
			return new CardPair(oCard1, oCard2);
		}
		else
		{
			return new CardPair(oCard2, oCard1);
		}
	}
	
	protected Queue<CardPair> generateShowAnswers()
	{
		List<Card> oWrongCards = new LinkedList<Card>();
		oWrongCards.addAll(getOpponentNameCards());
		oWrongCards.addAll(getOpponentTelephoneCards());
		
		ArrayList<CardPair> oResult = new ArrayList<CardPair>(2 * oWrongCards.size() + 1);
		for(Card oWrongCard : oWrongCards)
		{
			oResult.add(shuffledCardPair(m_oPlayer.getNameCard(), oWrongCard));
			oResult.add(shuffledCardPair(m_oPlayer.getTelephoneCard(), oWrongCard));
		}
		oResult.add(shuffledCardPair(m_oPlayer.getNameCard(), m_oPlayer.getTelephoneCard()));
		
		Collections.shuffle(oResult);
		
		return new LinkedList<CardPair>(oResult);
	}
	
	protected List<Card> getOpponentTelephoneCards()
	{
		if(m_oOpponentTelephoneCards == null)
		{
			List<Card> oTelephoneCards = Card.getCardsByType(CardType.Telephone);
			oTelephoneCards.remove(m_oPlayer.getTelephoneCard());
			m_oOpponentTelephoneCards = Collections.unmodifiableList(oTelephoneCards);
		}
		
		return m_oOpponentTelephoneCards;
	}
	
	protected List<Card> getOpponentNameCards()
	{
		if(m_oOppenentNameCards == null)
		{
			List<Card> oNameCards = Card.getCardsByType(CardType.Name);
			oNameCards.remove(m_oPlayer.getNameCard());
			m_oOppenentNameCards = Collections.unmodifiableList(oNameCards);
		}
		
		return m_oOppenentNameCards;
	}
}
