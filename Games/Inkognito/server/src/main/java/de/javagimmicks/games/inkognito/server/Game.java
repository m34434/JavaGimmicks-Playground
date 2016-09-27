package de.javagimmicks.games.inkognito.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.apache.commons.logging.Log;

import de.javagimmicks.games.inkognito.context.CardShowingContext;
import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.context.VisitsContext;
import de.javagimmicks.games.inkognito.context.server.ServerContext;
import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AnsweredMessage;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskNameMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.Message;
import de.javagimmicks.games.inkognito.message.message.ReportEndMessage;
import de.javagimmicks.games.inkognito.message.message.ReportExitMessage;
import de.javagimmicks.games.inkognito.message.message.ReportIdMessage;
import de.javagimmicks.games.inkognito.message.message.ReportMoveMessage;
import de.javagimmicks.games.inkognito.message.message.ReportNamesMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeMessage;
import de.javagimmicks.games.inkognito.message.message.ReportWinLooseMessage;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.CardType;
import de.javagimmicks.games.inkognito.model.Envoy;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;
import de.javagimmicks.games.inkognito.model.Player;

public class Game implements Runnable
{
	private final List<MessageProcessor> m_oMessageProcessors;
	private final ServerContext m_oServerContext;
	private final Random m_oRandom = new Random();
	private final int m_iGameCount;

	private Log m_oGameLog;
	
	public Game(ServerContext oServerContext, final List<? extends MessageProcessor> oMessageProcessors, int iGameCount)
	{
		if(oMessageProcessors.size() != Card.getNameCount())
		{
			throw new IllegalArgumentException("Number of message processors must match number of name cards!");
		}
		
		if(iGameCount < 1)
		{
			throw new IllegalArgumentException("Number of games to play must be greater than 0!");
		}
		
		m_oMessageProcessors = new ArrayList<MessageProcessor>(oMessageProcessors);
		m_oServerContext = oServerContext;
		m_iGameCount = iGameCount;
	}
	
	public Game(ServerContext oServerContext, final List<MessageProcessor> oMessageProcessors)
	{
		this(oServerContext, oMessageProcessors, 1);
	}
	
	public void setGameLogger(Log oGameLogger)
	{
		m_oGameLog = oGameLogger;
	}

	public void run()
	{
		// Initialize the players (ask name from everyone and init PlayerContext)
		try
		{
			initPlayers();
		}
		catch (InitializationException e)
		{
			sendEndMessage(e.getMessage());
			sendExitMessage();
			return;
		}
		
		// Register the message processor in the MessageProcessorContext
		registerMessageProcessors();
		
		// Send the names! message to all players
		sendNamesMessage();

		// Run the series of games according to number of games to play
		try
		{
			for(int i = 0; i < m_iGameCount; ++i)
			{
				runGame();
				if(Thread.interrupted())
				{
					break;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		// Send the exit message
		sendExitMessage();
	}
	
	private void runGame() throws Exception
	{
		m_oServerContext.reset();
		assignPlayerIdentities();
		
		// There will be some exception
		try
		{
			while(!Thread.interrupted())
			{
				processRound();
			}
			
			// When we are here, the thread was interrupted
			sendEndMessage("Server shutdown!");
		}
		catch(PlayerException ex)
		{
			handlePlayerException(ex);
		}
		catch(Exception ex)
		{
			sendEndMessage("Internal server error!");
			throw ex;
		}
	}
	
	private void initPlayers() throws InitializationException
	{
		List<String> oNames = new ArrayList<String>(m_oMessageProcessors.size());
		
		AskNameMessage oAskNameMessage = AskNameMessage.INSTANCE;
		
		for(ListIterator<MessageProcessor> iterProcessors = m_oMessageProcessors.listIterator(); iterProcessors.hasNext();)
		{
			MessageProcessor oMessageProcessor = iterProcessors.next();
			
			String sName;
			int iCount = 0;
			do
			{
				if(iCount++ > oNames.size())
				{
					throw new InitializationException(oMessageProcessor, "Some player was not able to provide a unique name!");
				}
				
				try
				{
					if(m_oGameLog != null)
					{
						logSend("Player" + String.valueOf(iterProcessors.previousIndex() + 1), oAskNameMessage.serialize());
					}
					
					sName = oMessageProcessor.processAnsweredMessage(oAskNameMessage).getName();

					if(m_oGameLog != null)
					{
						logReceive("Player" + String.valueOf(iterProcessors.previousIndex() + 1), sName);
					}

					for(char c : sName.toCharArray())
					{
						if(Character.isWhitespace(c))
						{
							throw new UnexpectedAnswerException("Player name may not contain whitespaces!");
						}
					}
					
					if(Envoy.INSTANCE.getName().equals(sName))
					{
						throw new UnexpectedAnswerException("Envoy name not allowed as player name!");
					}
				}
				catch (UnexpectedAnswerException e)
				{
					throw new InitializationException(oMessageProcessor, "Some player provided an illegal name!", e);
				}
			}
			while(oNames.contains(sName));
			
			oNames.add(sName);
		}
		
		List<Player> oPlayers = new ArrayList<Player>(oNames.size());
		for(String sName : oNames)
		{
			oPlayers.add(new Player(sName));
		}
		
		m_oServerContext.getPlayerContext().init(oPlayers);
	}
	
	private void registerMessageProcessors()
	{
		Iterator<MessageProcessor> iterMessageProcessors = m_oMessageProcessors.iterator();
		Iterator<Player> iterPlayer = m_oServerContext.getPlayerContext().getInitialPlayers().iterator();
		
		while(iterMessageProcessors.hasNext())
		{
			MessageProcessor oMessageProcessor = iterMessageProcessors.next();
			Player oPlayer = iterPlayer.next();
			
			m_oServerContext.getMessageProcessorContext().registerMessageProcessor(oPlayer.getName(), oMessageProcessor);
		}
	}
	
	private void sendNamesMessage()
	{
		List<Player> oPlayers = m_oServerContext.getPlayerContext().getPlayersRotated();
		ReportNamesMessage oNamesMessage = ReportNamesMessage.fromPlayerList(oPlayers);
		for(Player oPlayer : oPlayers)
		{
			processMessage(oPlayer, oNamesMessage);
		}
	}
	
	private void assignPlayerIdentities()
	{
		List<Card> oNameCards = Card.getCardsByType(CardType.Name);
		List<Card> oTelephoneCards = Card.getCardsByType(CardType.Telephone);
		
		Collections.shuffle(oNameCards);
		Collections.shuffle(oTelephoneCards);

		Iterator<Player> iterPlayers = m_oServerContext.getPlayerContext().getInitialPlayers().iterator();
		Iterator<Card> iterNameCards = oNameCards.iterator();
		Iterator<Card> iterTelephoneCards = oTelephoneCards.iterator();
		
		while(iterPlayers.hasNext())
		{
			Player oPlayer = iterPlayers.next();
			Card oNameCard = iterNameCards.next();
			Card oTelephoneCard = iterTelephoneCards.next();
			
			oPlayer.setNameCard(oNameCard);
			oPlayer.setTelephoneCard(oTelephoneCard);

			processMessage(oPlayer, ReportIdMessage.fromPlayer(oPlayer));
		}
	}

	private void processRound() throws PlayerException
	{
		processMovements();
		processMeetings();
		
		m_oServerContext.getRoundContext().roundFinished();
	}
	
	private void processMeetings() throws PlayerException
	{
		LocationsContext oLocationsContext = m_oServerContext.getLocationsContext();
		
		for(Location oLocation : Location.values())
		{
			// Get meeting perons for the location
			List<Person> oMeetingPersons = oLocationsContext.getVisitors(oLocation);
			
			// If other than 2 persons meet, nothing happens
			if(oMeetingPersons.size() != 2)
			{
				continue;
			}
			
			Player oPersonA = (Player)oMeetingPersons.get(0);
			Person oPersonB = oMeetingPersons.get(1);
			
			if(oPersonB instanceof Envoy)
			{
				processMeetEnvoy(oPersonA);
			}
			else
			{
				processMeetPlayer(oPersonA, (Player)oPersonB);
			}
		}
	}
	
	private void processMeetEnvoy(Player oAskingPlayer) throws PlayerException
	{
		// Ask the asking player, which other player he wants to meet
		String sShowingPlayerName = processAnsweredMessage(oAskingPlayer, AskMeetMessage.INSTANCE).getName();
		Player oShowingPlayer = m_oServerContext.getPlayerContext().getPlayer(sShowingPlayerName); 

		if(oShowingPlayer == null)
		{
			throw new PlayerException(oAskingPlayer, "Unknown player name '" + sShowingPlayerName + "'!");
		}
		
		CardShowingContext oCardShowingContext = m_oServerContext.getCardShowingContext();
		
		// Check, if really another player was returned
		if(oAskingPlayer == oShowingPlayer)
		{
			throw new PlayerException(oAskingPlayer, "Player may not send envoy to itself!");
		}
		
		// Check, if the player may ask the other player via the envoy once more
		if(!oCardShowingContext.mayPlayerAskId(oAskingPlayer, oShowingPlayer))
		{
			throw new PlayerException(oAskingPlayer, "Envoy may not be sent to another player more than twice!");
		}
		
		// Ask the other player for a card to show
		Card oShownCard = processAnsweredMessage(oShowingPlayer, AskShowEnvoyMessage.fromPlayer(oAskingPlayer)).getCard();
	
		// Check, if the showing player was showing a valid card
		if(!oCardShowingContext.mayPlayerShowId(oShowingPlayer, oAskingPlayer, oShownCard))
		{
			throw new PlayerException(oShowingPlayer, "Illegal card shown via envoy!");
		}
		
		// Forward the information to the asking player
		processMessage(oAskingPlayer, ReportSeeEnvoyMessage.fromPlayerAndCard(oShowingPlayer, oShownCard));
		
		// Register the information in the context
		oCardShowingContext.notifiyPlayerShow(oShowingPlayer, oAskingPlayer, oShownCard);
	}
	
	private void processMeetPlayer(Player oPlayer1, Player oPlayer2) throws PlayerException
	{
		processShowPair(oPlayer1, oPlayer2);
		processShowPair(oPlayer2, oPlayer1);
	}
	
	private void processShowPair(Player oAskingPlayer, Player oShowingPlayer) throws PlayerException
	{
		CardShowingContext oCardShowingContext = m_oServerContext.getCardShowingContext();

		// Ask the showing player for his card pair
		ShowAnswer oShowAnswer = processAnsweredMessage(oShowingPlayer, AskShowMessage.fromPlayer(oAskingPlayer));
		
		// Special treatment for the phone call
		if(oShowAnswer.isPhoneCall())
		{
			processPhoneCall(oAskingPlayer, oShowingPlayer, oShowAnswer);
		}
		else
		{
			CardPair oShownPair = oShowAnswer.getCardPair();
			
			// Check if the card pair may be shown by that player
			if(!oCardShowingContext.mayPlayerShowPair(oShowingPlayer, oAskingPlayer, oShownPair))
			{
				throw new PlayerException(oShowingPlayer, "Illegal card pair shown!");
			}
			
			// Forward the information to the asking player
			processMessage(oAskingPlayer, ReportSeeMessage.fromPlayerAndCardPair(oShowingPlayer, oShownPair));
			
			// Register the information in the context
			oCardShowingContext.notifiyPlayerShow(oShowingPlayer, oAskingPlayer, oShownPair);
		}
	}
	
	private void processPhoneCall(Player oAskingPlayer, Player oShowingPlayer, ShowAnswer oShowAnswer) throws PlayerException
	{
		if(!oAskingPlayer.getNameCard().isPartner(oShowingPlayer.getNameCard()))
		{
			throw new PlayerException(oShowingPlayer, "The partner was not met to make the phone call!");
		}
		
		// Get iterators for all players and the returned solution
		Iterator<CardPair> oSolution = oShowAnswer.getSolution().iterator();
		Iterator<Player> oPlayers = m_oServerContext.getPlayerContext().getInitialPlayers().iterator();
		
		// Iterator over all players
		while(oPlayers.hasNext())
		{
			// There must also be a next solution pair
			if(!oSolution.hasNext())
			{
				throw new PlayerException(oShowingPlayer, "The phone call contained too few information!");
			}
			
			// Get the next player and solution pair
			Player oPlayer = oPlayers.next();
			CardPair oCardPair = oSolution.next();
			
			// If the pair is wrong, the phone call is wrong
			if(!oPlayer.getId().equals(oCardPair))
			{
				throw new PlayerException(oShowingPlayer, "The solution was wrong!");
			}
		}
		
		// Check, if the solution isn't too long
		if(oSolution.hasNext())
		{
			throw new PlayerException(oShowingPlayer, "The phone call contained too much information!");
		}
		
		// If everything was right, the game is finished
		throw new PlayerException(oShowingPlayer, "Right solution found!", true);
	}
	
	private void processMovements() throws PlayerException
	{
		VisitsContext oVisitsContext = m_oServerContext.getVisitsContext();
		
		List<Player> oPlayers = m_oServerContext.getPlayerContext().getPlayersRotated();
		for(Player oPlayer : oPlayers)
		{
			// Ask the player where to move
			Location oLocation = processAnsweredMessage(oPlayer, AskMoveMessage.INSTANCE).getLocation();
			
			// Check if he may move there
			if(!oVisitsContext.isLocationVisitable(oPlayer, oLocation))
			{
				throw new PlayerException(oPlayer, "Player may not visit specified location '" + oLocation + "'!");
			}
			
			// Notify other players of movement
			ReportMoveMessage oMoveMessage = ReportMoveMessage.fromPersonAndLocation(oPlayer, oLocation);
			for(Player oOtherPlayer : oPlayers)
			{
				if(oOtherPlayer != oPlayer)
				{
					processMessage(oOtherPlayer, oMoveMessage);
				}
			}

			// Register movement in the context
			oVisitsContext.notifyPersonMove(oPlayer, oLocation);
		}
		
		// Generate envoy movement
		Person oEnvoy = Envoy.INSTANCE;
		List<Location> oEnvoyVisitableLocations = oVisitsContext.getVisitableLocations(oEnvoy);
		Location oEnvoyLocation = oEnvoyVisitableLocations.get(m_oRandom.nextInt(oEnvoyVisitableLocations.size()));
		
		// Notify other players of movement
		ReportMoveMessage oMoveMessage = ReportMoveMessage.fromPersonAndLocation(oEnvoy, oEnvoyLocation);
		for(Player oPlayer : oPlayers)
		{
			processMessage(oPlayer, oMoveMessage);
		}

		// Register movement in the context
		oVisitsContext.notifyPersonMove(oEnvoy, oEnvoyLocation);
	}
	
	private void sendEndMessage(String sMessage)
	{
		ReportEndMessage oEndMessage = new ReportEndMessage(sMessage);

		Collection<Player> oPlayers = m_oServerContext.getPlayerContext().getInitialPlayers(); 
		if(!oPlayers.isEmpty())
		{
			for(Player oPlayer : oPlayers)
			{
				processMessage(oPlayer, oEndMessage);
			}
		}
		else
		{
			for(ListIterator<MessageProcessor> iterProcessors = m_oMessageProcessors.listIterator(); iterProcessors.hasNext();)
			{
				MessageProcessor oMessageProcessor = iterProcessors.next();
				
				if(m_oGameLog != null)
				{
					logSend("Player" + String.valueOf(iterProcessors.previousIndex() + 1), oEndMessage.serialize());
				}
				
				oMessageProcessor.processMessage(oEndMessage);
			}
		}
	}
	
	private void sendExitMessage()
	{
		final ReportExitMessage oExitMessage = ReportExitMessage.INSTANCE;
		
		Collection<Player> oPlayers = m_oServerContext.getPlayerContext().getInitialPlayers(); 
		if(!oPlayers.isEmpty())
		{
			for(Player oPlayer : oPlayers)
			{
				processMessage(oPlayer, oExitMessage);
			}
		}
		else
		{
			for(ListIterator<MessageProcessor> iterProcessors = m_oMessageProcessors.listIterator(); iterProcessors.hasNext();)
			{
				MessageProcessor oMessageProcessor = iterProcessors.next();
				
				if(m_oGameLog != null)
				{
					logSend("Player" + String.valueOf(iterProcessors.previousIndex() + 1), oExitMessage.serialize());
				}
				
				oMessageProcessor.processMessage(oExitMessage);
			}
		}
	}
	
	private <A extends Answer> A processAnsweredMessage(Player oPlayer, AnsweredMessage<A> oMessage) throws PlayerException
	{
		MessageProcessor oProcessor = m_oServerContext.getMessageProcessorContext().getMessageProcessor(oPlayer.getName());
		
		if(m_oGameLog != null)
		{
			logSend(oPlayer.getName(), oMessage.serialize());
		}

		try
		{
			A oAnswer = oProcessor.processAnsweredMessage(oMessage);
			
			if(m_oGameLog != null)
			{
				logReceive(oPlayer.getName(), (oAnswer == null) ? "null" : oAnswer.serialize());
			}
			
			if(oAnswer == null)
			{
				throw new PlayerException(oPlayer, "Null answer given!");
			}
			
			return oAnswer;
		}
		catch (UnexpectedAnswerException e)
		{
			throw new PlayerException(oPlayer, e);
		}
	}
	
	private void processMessage(Player oPlayer, Message oMessage)
	{
		MessageProcessor oProcessor = m_oServerContext.getMessageProcessorContext().getMessageProcessor(oPlayer.getName());
		
		if(m_oGameLog != null)
		{
			logSend(oPlayer.getName(), oMessage.serialize());
		}
		
		oProcessor.processMessage(oMessage);
	}
	
	private void handlePlayerException(PlayerException oPlayerException)
	{
		// Get the causing player
		Player oCausingPlayer = oPlayerException.getPlayer();
		
		// Create a list of name cards of that player and his partner
		List<Card> oAffectedNameCards = new ArrayList<Card>(2);
		oAffectedNameCards.add(oCausingPlayer.getNameCard());
		oAffectedNameCards.add(Card.getPartner(oCausingPlayer.getNameCard()));
		
		// Send the end message to all players
		String sMessage = oPlayerException.getMessage();
		sendEndMessage(sMessage + " Player: " + oPlayerException.getPlayer().getName());

		Collection<Player> oPlayers = m_oServerContext.getPlayerContext().getInitialPlayers();
		// Send the win and loose messages
		for(Player oPlayer : oPlayers)
		{
			// The player is a winner, if the win flag is the same as the fact that he is in the list of affected players
			// (check it - it's right in that way)
			boolean bWin = (oPlayerException.isWin() == oAffectedNameCards.contains(oPlayer.getNameCard()));

			// Create the respective message and send it to all message processors
			ReportWinLooseMessage oMessage = ReportWinLooseMessage.fromPlayer(oPlayer, bWin);
			
			for(Player oReportedPlayer : oPlayers)
			{
				processMessage(oReportedPlayer, oMessage);
			}
		}
	}

	private void logSend(String sName, String sContent)
	{
		String sMessage = new StringBuffer()
		.append('[')
		.append(sName)
		.append("] <- ")
		.append(sContent)
		.toString();
		
		m_oGameLog.info(sMessage);
	}
	
	private void logReceive(String sName, String sContent)
	{
		String sMessage = new StringBuffer()
		.append('[')
		.append(sName)
		.append("] -> ")
		.append(sContent)
		.toString();
		
		m_oGameLog.info(sMessage);
	}
	
}
