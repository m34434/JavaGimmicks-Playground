package de.javagimmicks.games.inkognito.server.processor;

import org.apache.commons.logging.Log;

import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.message.answer.LocationAnswer;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.message.message.AnsweredMessage;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.Message;
import de.javagimmicks.games.inkognito.message.message.ReportEndMessage;
import de.javagimmicks.games.inkognito.message.message.ReportExitMessage;
import de.javagimmicks.games.inkognito.message.message.ReportIdMessage;
import de.javagimmicks.games.inkognito.message.message.ReportMoveMessage;
import de.javagimmicks.games.inkognito.message.message.ReportNameMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeMessage;
import de.javagimmicks.games.inkognito.message.message.ReportWinLooseMessage;

public abstract class AbstractDispatchingMessageProcessor implements
		MessageProcessor
{
	private Log m_oMessageLogger;

	public void setMessageLogger(Log oMessageLogger)
	{
		m_oMessageLogger = oMessageLogger;
	}

	public final <A extends Answer> A processAnsweredMessage(
			AnsweredMessage<A> oMessage) throws UnexpectedAnswerException
	{
		if(m_oMessageLogger != null)
		{
			m_oMessageLogger.info("-> " + oMessage.serialize());
		}
		
		A oAnswer = _processAnsweredMessage(oMessage);
		
		if(m_oMessageLogger != null)
		{
			m_oMessageLogger.info((oAnswer == null) ? "<- null" : "<- " + oAnswer.serialize());
		}
		
		return oAnswer;
	}

	public final void processMessage(Message oMessage)
	{
		if(m_oMessageLogger != null)
		{
			m_oMessageLogger.info("-> " + oMessage.serialize());
		}
		
		_processMessage(oMessage);
	}

	@SuppressWarnings("unchecked")
	private <A extends Answer> A _processAnsweredMessage(
			AnsweredMessage<A> oMessage) throws UnexpectedAnswerException
	{
		if (oMessage instanceof AskMeetMessage)
		{
			return (A) processAskMeetMessage((AskMeetMessage) oMessage);
		}
		else if (oMessage instanceof AskMoveMessage)
		{
			return (A) processAskMoveMessage((AskMoveMessage) oMessage);
		}
		else if (oMessage instanceof AskShowEnvoyMessage)
		{
			return (A) processAskShowEnvoyMessage((AskShowEnvoyMessage) oMessage);
		}
		else if (oMessage instanceof AskShowMessage)
		{
			return (A) processAskShowMessage((AskShowMessage) oMessage);
		}
		else
		{
			return (A) null;
		}
	}

	private final void _processMessage(Message oMessage)
	{
		if (oMessage instanceof ReportNameMessage)
		{
			processReportNameMessage((ReportNameMessage) oMessage);
		}
		else if (oMessage instanceof ReportIdMessage)
		{
			processReportIdMessage((ReportIdMessage) oMessage);
		}
		else if (oMessage instanceof ReportMoveMessage)
		{
			processReportMoveMessage((ReportMoveMessage) oMessage);
		}
		else if (oMessage instanceof ReportSeeEnvoyMessage)
		{
			processReportSeeEnvoyMessage((ReportSeeEnvoyMessage) oMessage);
		}
		else if (oMessage instanceof ReportSeeMessage)
		{
			processReportSeeMessage((ReportSeeMessage) oMessage);
		}
		else if (oMessage instanceof ReportWinLooseMessage)
		{
			processReportWinLooseMessage((ReportWinLooseMessage) oMessage);
		}
		else if (oMessage instanceof ReportEndMessage)
		{
			processReportEndMessage((ReportEndMessage) oMessage);
		}
		else if (oMessage instanceof ReportExitMessage)
		{
			processReportExitMessage((ReportExitMessage) oMessage);
		}
	}

	abstract protected NameAnswer processAskMeetMessage(AskMeetMessage oMessage);

	abstract protected LocationAnswer processAskMoveMessage(
			AskMoveMessage oMessage);

	abstract protected CardAnswer processAskShowEnvoyMessage(
			AskShowEnvoyMessage oMessage);

	abstract protected ShowAnswer processAskShowMessage(AskShowMessage oMessage);

	abstract protected void processReportNameMessage(
			ReportNameMessage oMessage);

	abstract protected void processReportIdMessage(ReportIdMessage oMessage);

	abstract protected void processReportMoveMessage(ReportMoveMessage oMessage);

	abstract protected void processReportSeeEnvoyMessage(
			ReportSeeEnvoyMessage oMessage);

	abstract protected void processReportSeeMessage(ReportSeeMessage oMessage);

	abstract protected void processReportWinLooseMessage(
			ReportWinLooseMessage oMessage);

	abstract protected void processReportEndMessage(ReportEndMessage oMessage);

	abstract protected void processReportExitMessage(ReportExitMessage oMessage);
}
