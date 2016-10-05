package de.javagimmicks.games.inkognito.server.processor.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.javagimmicks.games.inkognito.context.GameContext;
import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessor;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.message.answer.LocationAnswer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.ReportIdMessage;
import de.javagimmicks.games.inkognito.message.message.ReportMoveMessage;
import de.javagimmicks.games.inkognito.message.message.ReportNameMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeMessage;
import de.javagimmicks.games.inkognito.message.message.ReportWinLooseMessage;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.CardType;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public abstract class AbstractAIMessageProcessor implements DispatchedMessageProcessor
{
   protected final String name;

   protected final GameContext m_oGameContext;
	protected Person m_oPlayer;
	protected List<Person> m_oOpponents;
	
	private int m_iWinCounter;
	
	public AbstractAIMessageProcessor(GameContext oGameContext, String name)
	{
		m_oGameContext = oGameContext;
		this.name = name;
	}
	
	public String getName()
	{
	   return name;
	}
	
	public Person getPlayer()
	{
		return m_oPlayer;
	}

	public int getWinCount()
	{
		return m_iWinCounter;
	}
	
	protected void initNewGame()
	{
		m_oGameContext.reset();
	}
	
	protected Card getOwnNameCard()
	{
	   return m_oGameContext.getPlayerContext().getNameCard(m_oPlayer);
	}
	
	protected Card getOwnTelephoneCard()
	{
	   return m_oGameContext.getPlayerContext().getTelephoneCard(m_oPlayer);
	}
	
	protected CardPair getOwnId()
	{
	   return m_oGameContext.getPlayerContext().getId(m_oPlayer);
	}
	
	public void processReportNameMessage(ReportNameMessage oMessage)
	{
		// Get the own player object
		m_oPlayer = oMessage.getPlayer();
		
		// Build the list of opponent players
		final List<Person> oOpponents = new ArrayList<Person>(Arrays.asList(Person.values()));
		oOpponents.remove(Person.Envoy);
		oOpponents.remove(m_oPlayer);
		m_oOpponents = Collections.unmodifiableList(oOpponents);
	}

	public void processReportIdMessage(ReportIdMessage oMessage)
	{
		initNewGame();

      final PlayerContext playerContext = m_oGameContext.getPlayerContext();
      playerContext.setNameCard(m_oPlayer, oMessage.getNameCard());
      playerContext.setTelephoneCard(m_oPlayer, oMessage.getTelephoneCard());
	}

	public void processReportMoveMessage(ReportMoveMessage oMessage)
	{
		Person oPerson = oMessage.getPerson();
		Location oLocation = oMessage.getLocation();
		
		m_oGameContext.getLocationsContext().notifyPersonMove(oLocation, oPerson);
		m_oGameContext.getVisitsContext().notifyPersonMove(oPerson, oLocation);
		
		if(oPerson == Person.Envoy)
		{
			m_oGameContext.getRoundContext().roundFinished();
		}
	}

	public void processReportSeeEnvoyMessage(ReportSeeEnvoyMessage oMessage)
	{
		Person oPlayer = oMessage.getPlayer();
		Card oCard = oMessage.getCard();
		CardType oCardType = oCard.getCardType();
		
		m_oGameContext.getCardShowingContext().notifiyPlayerShow(oPlayer, m_oPlayer, oCard);
		
		if(oCardType == CardType.Telephone)
		{
		   m_oGameContext.getPlayerContext().setTelephoneCard(oPlayer, oCard);
		}
		else if(oCardType == CardType.Name)
		{
		   m_oGameContext.getPlayerContext().setNameCard(oPlayer, oCard);
		}
	}

	public void processReportSeeMessage(ReportSeeMessage oMessage)
	{
		Person oPlayer = oMessage.getPlayer();
		CardPair oCardPair = oMessage.getCardPair();
		
		m_oGameContext.getCardShowingContext().notifiyPlayerShow(oPlayer, m_oPlayer, oCardPair);
	}

	public LocationAnswer processAskMoveMessage(AskMoveMessage oMessage)
	{
		LocationAnswer oResult = _processAskMoveMessage(oMessage);
		Location oTargetLocation = oResult.getLocation();
		
		m_oGameContext.getLocationsContext().notifyPersonMove(oTargetLocation, m_oPlayer);
		m_oGameContext.getVisitsContext().notifyPersonMove(m_oPlayer, oTargetLocation);
		
		return oResult;
	}

	public CardAnswer processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage)
	{
		CardAnswer oResult = _processAskShowEnvoyMessage(oMessage);
		
		Person oAskingPlayer = oMessage.getPlayer();
		Card oShownCard = oResult.getCard();
		
		m_oGameContext.getCardShowingContext().notifiyPlayerShow(m_oPlayer, oAskingPlayer, oShownCard);
		
		return oResult;
	}

	public ShowAnswer processAskShowMessage(AskShowMessage oMessage)
	{
		ShowAnswer oResult = _processAskShowMessage(oMessage);

		if(oResult != null && !oResult.isPhoneCall())
		{
			Person oAskingPlayer = oMessage.getPlayer();
			CardPair oShownPair = oResult.getCardPair();
			
			m_oGameContext.getCardShowingContext().notifiyPlayerShow(m_oPlayer, oAskingPlayer, oShownPair);
		}
		
		
		return oResult;
	}
	
	public void processReportWinLooseMessage(ReportWinLooseMessage oMessage)
	{
		if(oMessage.isWin() && oMessage.getPlayer() == m_oPlayer)
		{
			++m_iWinCounter;
		}
	}

	protected abstract LocationAnswer _processAskMoveMessage(AskMoveMessage oMessage);
	protected abstract CardAnswer _processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage);
	protected abstract ShowAnswer _processAskShowMessage(AskShowMessage oMessage);
}
