package de.javagimmicks.games.inkognito.server.processor.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.javagimmicks.games.inkognito.context.GameContext;
import de.javagimmicks.games.inkognito.context.PlayerContext;
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
import de.javagimmicks.games.inkognito.server.processor.AbstractDispatchingMessageProcessor;

public abstract class AbstractAIMessageProcessor extends AbstractDispatchingMessageProcessor
{
	protected final GameContext m_oGameContext;
	protected Person m_oPlayer;
	protected List<Person> m_oOpponents;
	
	private String m_sPlayerNameBase;
	private int m_iNameCounter;
	
	private int m_iWinCounter;
	
	public AbstractAIMessageProcessor(GameContext oGameContext, String sPlayerNameBase)
	{
		m_oGameContext = oGameContext;
		
		m_sPlayerNameBase = sPlayerNameBase.replaceAll("\\s", "");
	}
	
	public String getPlayerName()
	{
		return m_sPlayerNameBase + String.valueOf(m_iNameCounter);
	}

	public int getWinCount()
	{
		return m_iWinCounter;
	}
	
	protected void initNewGame()
	{
		m_oGameContext.reset();
	}
	
	protected void processReportNameMessage(ReportNameMessage oMessage)
	{
		// Get the own player object
		m_oPlayer = oMessage.getPlayer();
		
		// Build the list of opponent players
		final List<Person> oOpponents = new ArrayList<Person>(Arrays.asList(Person.values()));
		oOpponents.remove(Person.Envoy);
		oOpponents.remove(m_oPlayer);
		m_oOpponents = Collections.unmodifiableList(oOpponents);
	}

	protected void processReportIdMessage(ReportIdMessage oMessage)
	{
		initNewGame();

      final PlayerContext playerContext = m_oGameContext.getPlayerContext();
      playerContext.setNameCard(m_oPlayer, oMessage.getNameCard());
      playerContext.setTelephoneCard(m_oPlayer, oMessage.getTelephoneCard());
	}

	protected void processReportMoveMessage(ReportMoveMessage oMessage)
	{
		String sPersonName = oMessage.getPersonName();
		Person oPerson = (Envoy.INSTANCE.getName().equals(sPersonName)) ? Envoy.INSTANCE : m_oGameContext.getPlayerContext().getPlayer(sPersonName);
		Location oLocation = oMessage.getLocation();
		
		m_oGameContext.getLocationsContext().notifyPersonMove(oLocation, oPerson);
		m_oGameContext.getVisitsContext().notifyPersonMove(oPerson, oLocation);
		
		if(oPerson == Envoy.INSTANCE)
		{
			m_oGameContext.getRoundContext().roundFinished();
		}
	}

	protected void processReportSeeEnvoyMessage(ReportSeeEnvoyMessage oMessage)
	{
		String sPlayerName = oMessage.getPlayerName();
		Person oPlayer = m_oGameContext.getPlayerContext().getPlayer(sPlayerName);
		Card oCard = oMessage.getCard();
		CardType oCardType = oCard.getCardType();
		
		m_oGameContext.getCardShowingContext().notifiyPlayerShow(oPlayer, m_oPlayer, oCard);
		
		if(oCardType == CardType.Telephone)
		{
			oPlayer.setTelephoneCard(oCard);
		}
		else if(oCardType == CardType.Name)
		{
			oPlayer.setNameCard(oCard);
		}
	}

	protected void processReportSeeMessage(ReportSeeMessage oMessage)
	{
		String sPlayerName = oMessage.getPlayerName();
		Person oPlayer = m_oGameContext.getPlayerContext().getPlayer(sPlayerName);
		CardPair oCardPair = oMessage.getCardPair();
		
		m_oGameContext.getCardShowingContext().notifiyPlayerShow(oPlayer, m_oPlayer, oCardPair);
	}

	final protected LocationAnswer processAskMoveMessage(AskMoveMessage oMessage)
	{
		LocationAnswer oResult = _processAskMoveMessage(oMessage);
		Location oTargetLocation = oResult.getLocation();
		
		m_oGameContext.getLocationsContext().notifyPersonMove(oTargetLocation, m_oPlayer);
		m_oGameContext.getVisitsContext().notifyPersonMove(m_oPlayer, oTargetLocation);
		
		return oResult;
	}

	final protected CardAnswer processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage)
	{
		CardAnswer oResult = _processAskShowEnvoyMessage(oMessage);
		
		String sAskingPlayerName = oMessage.getPlayerName();
		Person oAskingPlayer = m_oGameContext.getPlayerContext().getPlayer(sAskingPlayerName);
		Card oShownCard = oResult.getCard();
		
		m_oGameContext.getCardShowingContext().notifiyPlayerShow(m_oPlayer, oAskingPlayer, oShownCard);
		
		return oResult;
	}

	final protected ShowAnswer processAskShowMessage(AskShowMessage oMessage)
	{
		ShowAnswer oResult = _processAskShowMessage(oMessage);

		if(oResult != null && !oResult.isPhoneCall())
		{
			String sAskingPlayerName = oMessage.getPlayerName();
			Person oAskingPlayer = m_oGameContext.getPlayerContext().getPlayer(sAskingPlayerName);
			CardPair oShownPair = oResult.getCardPair();
			
			m_oGameContext.getCardShowingContext().notifiyPlayerShow(m_oPlayer, oAskingPlayer, oShownPair);
		}
		
		
		return oResult;
	}
	
	protected void processReportWinLooseMessage(ReportWinLooseMessage oMessage)
	{
		if(oMessage.isWin() && oMessage.getPlayerName().equals(getPlayerName()))
		{
			++m_iWinCounter;
		}
	}

	protected final Person getPlayerByName(String sPlayerName)
	{
		return m_oGameContext.getPlayerContext().getPlayer(sPlayerName);
	}

	protected abstract LocationAnswer _processAskMoveMessage(AskMoveMessage oMessage);
	protected abstract CardAnswer _processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage);
	protected abstract ShowAnswer _processAskShowMessage(AskShowMessage oMessage);
}
