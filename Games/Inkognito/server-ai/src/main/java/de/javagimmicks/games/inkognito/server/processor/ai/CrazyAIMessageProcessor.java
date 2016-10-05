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
import de.javagimmicks.games.inkognito.model.Person;

public class CrazyAIMessageProcessor extends AbstractAIMessageProcessor
{
	protected static final Random RANDOM = new Random();
	protected final Map<Person, Queue<CardPair>> m_oShowAnswers = new HashMap<Person, Queue<CardPair>>();
	
	private List<Card> m_oOpponentTelephoneCards;
	private List<Card> m_oOppenentNameCards;

	protected CrazyAIMessageProcessor(GameContext oGameContext, String name)
	{
		super(oGameContext, name);
	}

	public CrazyAIMessageProcessor(String name)
	{
		this(new GameContext(), name);
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
		Person oOtherPlayer = oMessage.getPlayer();
		
		Card oOwnNameCard = getOwnNameCard();
		Card oOwnTelephoneCard = getOwnTelephoneCard();
		
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
		Person oOtherPlayer = oMessage.getPlayer();
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
		for(Person oOtherPlayer : m_oOpponents)
		{
			if(m_oGameContext.getCardShowingContext().mayPlayerAskId(m_oPlayer, oOtherPlayer))
			{
				return new NameAnswer(oOtherPlayer);
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
		
		Card ownNameCard = getOwnNameCard();
		Card ownTelephoneCard = getOwnTelephoneCard();
      
      for(Card oWrongCard : oWrongCards)
		{
			oResult.add(shuffledCardPair(ownNameCard, oWrongCard));
			oResult.add(shuffledCardPair(ownTelephoneCard, oWrongCard));
		}
		oResult.add(shuffledCardPair(ownNameCard, ownTelephoneCard));
		
		Collections.shuffle(oResult);
		
		return new LinkedList<CardPair>(oResult);
	}
	
	protected List<Card> getOpponentTelephoneCards()
	{
		if(m_oOpponentTelephoneCards == null)
		{
			List<Card> oTelephoneCards = Card.getCardsByType(CardType.Telephone);
			oTelephoneCards.remove(getOwnTelephoneCard());
			m_oOpponentTelephoneCards = Collections.unmodifiableList(oTelephoneCards);
		}
		
		return m_oOpponentTelephoneCards;
	}
	
	protected List<Card> getOpponentNameCards()
	{
		if(m_oOppenentNameCards == null)
		{
			List<Card> oNameCards = Card.getCardsByType(CardType.Name);
			oNameCards.remove(getOwnNameCard());
			m_oOppenentNameCards = Collections.unmodifiableList(oNameCards);
		}
		
		return m_oOppenentNameCards;
	}
}
