package de.javagimmicks.games.inkognito.message;

import de.javagimmicks.games.inkognito.message.answer.Answer;
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

public class DispatchedMessageProcessorAdapter implements MessageProcessor
{
   private final DispatchedMessageProcessor base;

   public DispatchedMessageProcessorAdapter(DispatchedMessageProcessor base)
   {
      this.base = base;
   }

	public final <A extends Answer> A processAnsweredMessage(
			AnsweredMessage<A> oMessage) throws UnexpectedAnswerException
	{
		return _processAnsweredMessage(oMessage);
	}

	public final void processMessage(Message oMessage)
	{
		_processMessage(oMessage);
	}

	@SuppressWarnings("unchecked")
	private <A extends Answer> A _processAnsweredMessage(
			AnsweredMessage<A> oMessage) throws UnexpectedAnswerException
	{
		if (oMessage instanceof AskMeetMessage)
		{
			return (A) base.processAskMeetMessage((AskMeetMessage) oMessage);
		}
		else if (oMessage instanceof AskMoveMessage)
		{
			return (A) base.processAskMoveMessage((AskMoveMessage) oMessage);
		}
		else if (oMessage instanceof AskShowEnvoyMessage)
		{
			return (A) base.processAskShowEnvoyMessage((AskShowEnvoyMessage) oMessage);
		}
		else if (oMessage instanceof AskShowMessage)
		{
			return (A) base.processAskShowMessage((AskShowMessage) oMessage);
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
		   base.processReportNameMessage((ReportNameMessage) oMessage);
		}
		else if (oMessage instanceof ReportIdMessage)
		{
		   base.processReportIdMessage((ReportIdMessage) oMessage);
		}
		else if (oMessage instanceof ReportMoveMessage)
		{
		   base.processReportMoveMessage((ReportMoveMessage) oMessage);
		}
		else if (oMessage instanceof ReportSeeEnvoyMessage)
		{
		   base.processReportSeeEnvoyMessage((ReportSeeEnvoyMessage) oMessage);
		}
		else if (oMessage instanceof ReportSeeMessage)
		{
		   base.processReportSeeMessage((ReportSeeMessage) oMessage);
		}
		else if (oMessage instanceof ReportWinLooseMessage)
		{
		   base.processReportWinLooseMessage((ReportWinLooseMessage) oMessage);
		}
		else if (oMessage instanceof ReportEndMessage)
		{
		   base.processReportEndMessage((ReportEndMessage) oMessage);
		}
		else if (oMessage instanceof ReportExitMessage)
		{
		   base.processReportExitMessage((ReportExitMessage) oMessage);
		}
	}
}
