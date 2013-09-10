package de.javagimmicks.games.inkognito.client.net;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.message.AnsweredMessage;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskNameMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.Message;
import de.javagimmicks.games.inkognito.message.message.MessageConstants;
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
import de.javagimmicks.games.inkognito.model.Envoy;
import de.javagimmicks.games.inkognito.model.Location;

public class NetworkPlayerMessageProcessorAdapter implements NetworkPlayer, MessageConstants
{
	protected final MessageProcessor m_oMessageProcessor;
	
	public NetworkPlayerMessageProcessorAdapter(final MessageProcessor oMessageProcessor)
	{
		m_oMessageProcessor = oMessageProcessor;
	}

	@SuppressWarnings("unchecked")
	public String process(String sMessage)
	{
		Message oMessage;
		try
		{
			oMessage = parseMessage(sMessage);

			if(oMessage instanceof AnsweredMessage)
			{
				Answer oAnswer = m_oMessageProcessor.processAnsweredMessage((AnsweredMessage)oMessage);
				return (oAnswer == null) ? null : oAnswer.serialize();
			}
			else
			{
				m_oMessageProcessor.processMessage(oMessage);
				return null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	private Message parseMessage(String sMessage) throws Exception
	{
		StringTokenizer oTokenizer = new StringTokenizer(sMessage);
		
		String sMessageType = oTokenizer.nextToken();
		
		if(SIG_REP_NAMES.equals(sMessageType))
		{
			List<String> oNames = new LinkedList<String>();
			while(oTokenizer.hasMoreTokens())
			{
				oNames.add(oTokenizer.nextToken());
			}
			
			return new ReportNamesMessage(oNames);
		}
		else if(SIG_REP_ID.equals(sMessageType))
		{
			Card oNameCard = Card.fromId(oTokenizer.nextToken());
			Card oTelephoneCard = Card.fromId(oTokenizer.nextToken());
			
			return new ReportIdMessage(oNameCard, oTelephoneCard);
		}
		else if(SIG_REP_MOVE.equals(sMessageType))
		{
			String sPersonName = oTokenizer.nextToken();
			Location oLocation = Location.valueOf(oTokenizer.nextToken());
			
			return new ReportMoveMessage(sPersonName, oLocation);
		}
		else if(SIG_REP_SEE.equals(sMessageType))
		{
			String sPersonName = oTokenizer.nextToken();
			Card oCard1 = Card.fromId(oTokenizer.nextToken());
			
			if(oTokenizer.hasMoreTokens())
			{
				Card oCard2 = Card.fromId(oTokenizer.nextToken());
				
				return new ReportSeeMessage(sPersonName, new CardPair(oCard1, oCard2));
			}
			else
			{
				return new ReportSeeEnvoyMessage(sPersonName, oCard1);
			}
		}
		else if(SIG_REP_END.equals(sMessageType))
		{
			StringBuffer oMessage = new StringBuffer();
			while(oTokenizer.hasMoreTokens())
			{
				oMessage.append(' ').append(oTokenizer.nextToken());
			}
			
			return new ReportEndMessage(oMessage.substring(1));
		}
		else if(SIG_REP_WINNER.equals(sMessageType))
		{
			String sPersonName = oTokenizer.nextToken();
			Card oNameCard = Card.fromId(oTokenizer.nextToken());
			Card oTelephoneCard = Card.fromId(oTokenizer.nextToken());
			
			return new ReportWinLooseMessage(sPersonName, new CardPair(oNameCard, oTelephoneCard), true);
		}
		else if(SIG_REP_LOOSER.equals(sMessageType))
		{
			String sPersonName = oTokenizer.nextToken();
			Card oNameCard = Card.fromId(oTokenizer.nextToken());
			Card oTelephoneCard = Card.fromId(oTokenizer.nextToken());
			
			return new ReportWinLooseMessage(sPersonName, new CardPair(oNameCard, oTelephoneCard), true);
		}
		else if(SIG_REP_EXIT.equals(sMessageType))
		{
			return ReportExitMessage.INSTANCE;
		}
		else if(SIG_ASK_MEET.equals(sMessageType))
		{
			return AskMeetMessage.INSTANCE;
		}
		else if(SIG_ASK_MOVE.equals(sMessageType))
		{
			return AskMoveMessage.INSTANCE;
		}
		else if(SIG_ASK_NAME.equals(sMessageType))
		{
			return AskNameMessage.INSTANCE;
		}
		else if(SIG_ASK_SHOW.equals(sMessageType))
		{
			String sPersonName = oTokenizer.nextToken();
			
			if(Envoy.INSTANCE.getName().equals(sPersonName))
			{
				sPersonName = oTokenizer.nextToken();
				
				return new AskShowEnvoyMessage(sPersonName);
			}
			else
			{
				return new AskShowMessage(sPersonName);
			}
		}
		
		throw new IllegalArgumentException("Unknown message type!");
	}
}
