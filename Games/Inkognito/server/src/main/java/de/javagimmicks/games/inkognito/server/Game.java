package de.javagimmicks.games.inkognito.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import de.javagimmicks.games.inkognito.context.CardShowingContext;
import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.context.VisitsContext;
import de.javagimmicks.games.inkognito.context.server.ServerContext;
import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessor;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.ReportEndMessage;
import de.javagimmicks.games.inkognito.message.message.ReportExitMessage;
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

public class Game implements Runnable
{
	private final List<DispatchedMessageProcessor> m_oMessageProcessors;
	private final ServerContext m_oServerContext;
	private final Random m_oRandom = new Random();
	private final int m_iGameCount;

	public Game(ServerContext oServerContext, final List<? extends DispatchedMessageProcessor> oMessageProcessors, int iGameCount)
	{
		if(oMessageProcessors.size() != Card.getNameCount())
		{
			throw new IllegalArgumentException("Number of message processors must match number of name cards!");
		}
		
		if(iGameCount < 1)
		{
			throw new IllegalArgumentException("Number of games to play must be greater than 0!");
		}
		
		m_oMessageProcessors = new ArrayList<DispatchedMessageProcessor>(oMessageProcessors);
		m_oServerContext = oServerContext;
		m_iGameCount = iGameCount;
	}
	
	public Game(ServerContext oServerContext, final List<DispatchedMessageProcessor> oMessageProcessors)
	{
		this(oServerContext, oMessageProcessors, 1);
	}
	
	public void run()
	{
		// Shuffle message processors
      Collections.shuffle(m_oMessageProcessors);
		
		// Register the message processor in the MessageProcessorContext
		registerMessageProcessors();
		
		// Send the names! message to all players
		sendNameMessages();

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
	
	private void registerMessageProcessors()
	{
		Iterator<DispatchedMessageProcessor> iterMessageProcessors = m_oMessageProcessors.iterator();
		
		for(Person player : PlayerContext.getPlayers())
		{
		   DispatchedMessageProcessor oMessageProcessor = iterMessageProcessors.next();
			
			m_oServerContext.getMessageProcessorContext().registerMessageProcessor(player, oMessageProcessor);
		}
	}
	
	private void sendNameMessages()
	{
	   for(Person player : PlayerContext.getPlayers())
	   {
	      final ReportNameMessage oNamesMessage = new ReportNameMessage(player);
	      getPlayerHandler(player).processReportNameMessage(oNamesMessage);
	   }
	}
	
	private void assignPlayerIdentities()
	{
		List<Card> oNameCards = Card.getCardsByType(CardType.Name);
		List<Card> oTelephoneCards = Card.getCardsByType(CardType.Telephone);
		
		Collections.shuffle(oNameCards);
		Collections.shuffle(oTelephoneCards);

		Iterator<Person> iterPlayers = PlayerContext.getPlayers().iterator();
		Iterator<Card> iterNameCards = oNameCards.iterator();
		Iterator<Card> iterTelephoneCards = oTelephoneCards.iterator();
		
		PlayerContext playerContext = m_oServerContext.getPlayerContext();

		while(iterPlayers.hasNext())
		{
		   Person oPlayer = iterPlayers.next();
			Card oNameCard = iterNameCards.next();
			Card oTelephoneCard = iterTelephoneCards.next();
			
			playerContext.setNameCard(oPlayer, oNameCard);
			playerContext.setTelephoneCard(oPlayer, oTelephoneCard);
			
			getPlayerHandler(oPlayer).processReportIdMessage(new ReportIdMessage(oNameCard, oTelephoneCard));
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
			// Get meeting persons for the location
			List<Person> oMeetingPersons = oLocationsContext.getVisitors(oLocation);
			
			// If other than 2 persons meet, nothing happens
			if(oMeetingPersons.size() != 2)
			{
				continue;
			}
			
			Person oPersonA = oMeetingPersons.get(0);
			Person oPersonB = oMeetingPersons.get(1);
			
			if(oPersonB == Person.Envoy)
			{
				processMeetEnvoy(oPersonA);
			}
			else
			{
				processMeetPlayer(oPersonA, oPersonB);
			}
		}
	}
	
	private void processMeetEnvoy(Person oAskingPlayer) throws PlayerException
	{
		// Ask the asking player, which other player he wants to meet
		Person oShowingPlayer = getPlayerHandler(oAskingPlayer).processAskMeetMessage(AskMeetMessage.INSTANCE).getPerson(); 

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
		Card oShownCard = getPlayerHandler(oShowingPlayer).processAskShowEnvoyMessage(new AskShowEnvoyMessage(oAskingPlayer)).getCard();
	
		// Check, if the showing player was showing a valid card
		if(!oCardShowingContext.mayPlayerShowId(oShowingPlayer, oAskingPlayer, oShownCard))
		{
			throw new PlayerException(oShowingPlayer, "Illegal card shown via envoy!");
		}
		
		// Forward the information to the asking player
		getPlayerHandler(oAskingPlayer).processReportSeeEnvoyMessage(new ReportSeeEnvoyMessage(oShowingPlayer, oShownCard));
		
		// Register the information in the context
		oCardShowingContext.notifiyPlayerShow(oShowingPlayer, oAskingPlayer, oShownCard);
	}
	
	private void processMeetPlayer(Person oPlayer1, Person oPlayer2) throws PlayerException
	{
		processShowPair(oPlayer1, oPlayer2);
		processShowPair(oPlayer2, oPlayer1);
	}
	
	private void processShowPair(Person oAskingPlayer, Person oShowingPlayer) throws PlayerException
	{
		CardShowingContext oCardShowingContext = m_oServerContext.getCardShowingContext();

		// Ask the showing player for his card pair
		ShowAnswer oShowAnswer = getPlayerHandler(oShowingPlayer).processAskShowMessage(new AskShowMessage(oAskingPlayer));
		
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
			getPlayerHandler(oAskingPlayer).processReportSeeMessage(new ReportSeeMessage(oShowingPlayer, oShownPair));
			
			// Register the information in the context
			oCardShowingContext.notifiyPlayerShow(oShowingPlayer, oAskingPlayer, oShownPair);
		}
	}
	
	private void processPhoneCall(Person oAskingPlayer, Person oShowingPlayer, ShowAnswer oShowAnswer) throws PlayerException
	{
	   final PlayerContext playerContext = m_oServerContext.getPlayerContext();
		if(!playerContext.getNameCard(oAskingPlayer).isPartner(playerContext.getNameCard(oShowingPlayer)))
		{
			throw new PlayerException(oShowingPlayer, "The partner was not met to make the phone call!");
		}
		
		// Get iterators for all players and the returned solution
		Iterator<CardPair> oSolution = oShowAnswer.getSolution().iterator();
		Iterator<Person> oPlayers = PlayerContext.getPlayers().iterator();
		
		// Iterator over all players
		while(oPlayers.hasNext())
		{
			// There must also be a next solution pair
			if(!oSolution.hasNext())
			{
				throw new PlayerException(oShowingPlayer, "The phone call contained too few information!");
			}
			
			// Get the next player and solution pair
			Person oPlayer = oPlayers.next();
			CardPair oCardPair = oSolution.next();
			
			// If the pair is wrong, the phone call is wrong
			if(!playerContext.getId(oPlayer).equals(oCardPair))
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
		
		List<Person> oPlayers = m_oServerContext.getPlayerContext().getPlayersRotated();
		for(Person oPlayer : oPlayers)
		{
			// Ask the player where to move
			Location oLocation = getPlayerHandler(oPlayer).processAskMoveMessage(AskMoveMessage.INSTANCE).getLocation();
			
			// Check if he may move there
			if(!oVisitsContext.isLocationVisitable(oPlayer, oLocation))
			{
				throw new PlayerException(oPlayer, "Player may not visit specified location '" + oLocation + "'!");
			}
			
			// Notify other players of movement
			ReportMoveMessage oMoveMessage = new ReportMoveMessage(oPlayer, oLocation);
			for(Person oOtherPlayer : oPlayers)
			{
				if(oOtherPlayer != oPlayer)
				{
					getPlayerHandler(oOtherPlayer).processReportMoveMessage(oMoveMessage);
				}
			}

			// Register movement in the context
			oVisitsContext.notifyPersonMove(oPlayer, oLocation);
		}
		
		// Generate envoy movement
		Person oEnvoy = Person.Envoy;
		List<Location> oEnvoyVisitableLocations = oVisitsContext.getVisitableLocations(oEnvoy);
		Location oEnvoyLocation = oEnvoyVisitableLocations.get(m_oRandom.nextInt(oEnvoyVisitableLocations.size()));
		
		// Notify other players of movement
		ReportMoveMessage oMoveMessage = new ReportMoveMessage(oEnvoy, oEnvoyLocation);
		for(Person oPlayer : oPlayers)
		{
			getPlayerHandler(oPlayer).processReportMoveMessage(oMoveMessage);
		}

		// Register movement in the context
		oVisitsContext.notifyPersonMove(oEnvoy, oEnvoyLocation);
	}
	
	private void sendEndMessage(String sMessage)
	{
		ReportEndMessage oEndMessage = new ReportEndMessage(sMessage);

		if(!m_oServerContext.getMessageProcessorContext().isEmpty())
		{
			for(Person oPlayer : PlayerContext.getPlayers())
			{
			   getPlayerHandler(oPlayer).processReportEndMessage(oEndMessage);
			}
		}
		else
		{
			for(ListIterator<DispatchedMessageProcessor> iterProcessors = m_oMessageProcessors.listIterator(); iterProcessors.hasNext();)
			{
				iterProcessors.next().processReportEndMessage(oEndMessage);
			}
		}
	}
	
	private void sendExitMessage()
	{
		final ReportExitMessage oExitMessage = ReportExitMessage.INSTANCE;
		
      if(!m_oServerContext.getMessageProcessorContext().isEmpty())
      {
         for(Person oPlayer : PlayerContext.getPlayers())
         {
            getPlayerHandler(oPlayer).processReportExitMessage(oExitMessage);
         }
      }
      else
      {
         for(ListIterator<DispatchedMessageProcessor> iterProcessors = m_oMessageProcessors.listIterator(); iterProcessors.hasNext();)
         {
            iterProcessors.next().processReportExitMessage(oExitMessage);
         }
      }
	}

   private DispatchedMessageProcessor getPlayerHandler(Person oPlayer)
   {
      return m_oServerContext.getMessageProcessorContext().getMessageProcessor(oPlayer);
   }
	
	private void handlePlayerException(PlayerException oPlayerException)
	{
	   final PlayerContext playerContext = m_oServerContext.getPlayerContext();
	   
		// Get the causing player
		final Person oCausingPlayer = oPlayerException.getPlayer();
		final Card oCausingPlayerNameCard = playerContext.getNameCard(oCausingPlayer);
		
		// Create a list of name cards of that player and his partner
		List<Card> oAffectedNameCards = new ArrayList<Card>(2);
      oAffectedNameCards.add(oCausingPlayerNameCard);
      oAffectedNameCards.add(Card.getPartner(oCausingPlayerNameCard));
		
		// Send the end message to all players
		String sMessage = oPlayerException.getMessage();
		sendEndMessage(sMessage + " Player: " + oPlayerException.getPlayer());

		// Send the win and loose messages
		for(Person oPlayer : PlayerContext.getPlayers())
		{
			// The player is a winner, if the win flag is the same as the fact that he is in the list of affected players
			// (check it - it's right in that way)
			boolean bWin = (oPlayerException.isWin() == oAffectedNameCards.contains(playerContext.getNameCard(oPlayer)));

			// Create the respective message and send it to all message processors
			ReportWinLooseMessage oMessage = new ReportWinLooseMessage(oPlayer, playerContext.getId(oPlayer), bWin);
			
			for(Person oReportedPlayer : PlayerContext.getPlayers())
			{
			   getPlayerHandler(oReportedPlayer).processReportWinLooseMessage(oMessage);
			}
		}
	}
}
