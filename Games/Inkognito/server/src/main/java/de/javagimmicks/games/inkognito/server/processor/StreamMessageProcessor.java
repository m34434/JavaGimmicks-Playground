package de.javagimmicks.games.inkognito.server.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;

import de.javagimmicks.games.inkognito.message.DispatchedMessageProcessor;
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

public class StreamMessageProcessor implements DispatchedMessageProcessor
{
	private final BufferedReader m_oReader;
	private final PrintWriter m_oWriter;

	private Log m_oMessageLogger;	
	
   public StreamMessageProcessor(InputStream oInputStream, OutputStream oOutputStream)
   {
      this(new BufferedReader(new InputStreamReader(oInputStream)), new PrintWriter(new OutputStreamWriter(oOutputStream)));
   }
   
   public StreamMessageProcessor(BufferedReader oReader, PrintWriter oWriter)
   {
      m_oReader = oReader;
      m_oWriter = oWriter;
   }
   
	public void setMessageLogger(Log oLogger)
	{
		m_oMessageLogger = oLogger;
	}
	
	@Override
   public NameAnswer processAskMeetMessage(AskMeetMessage oMessage)
   {
	   return processAnsweredMessage(oMessage);
   }

   @Override
   public LocationAnswer processAskMoveMessage(AskMoveMessage oMessage)
   {
      return processAnsweredMessage(oMessage);
   }

   @Override
   public CardAnswer processAskShowEnvoyMessage(AskShowEnvoyMessage oMessage)
   {
      return processAnsweredMessage(oMessage);
   }

   @Override
   public ShowAnswer processAskShowMessage(AskShowMessage oMessage)
   {
      return processAnsweredMessage(oMessage);
   }

   @Override
   public void processReportNameMessage(ReportNameMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportIdMessage(ReportIdMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportMoveMessage(ReportMoveMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportSeeEnvoyMessage(ReportSeeEnvoyMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportSeeMessage(ReportSeeMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportWinLooseMessage(ReportWinLooseMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportEndMessage(ReportEndMessage oMessage)
   {
      processMessage(oMessage);
   }

   @Override
   public void processReportExitMessage(ReportExitMessage oMessage)
   {
      processMessage(oMessage);
   }

   protected <A extends Answer> A processAnsweredMessage(
			AnsweredMessage<A> oMessage) throws UnexpectedAnswerException
	{
		processMessage(oMessage);
		
		try
		{
			String sAnswer = m_oReader.readLine();
			
			log("<- " + sAnswer);

			if(sAnswer.length() == 0)
			{
				throw new UnexpectedAnswerException("Empty answer returned!");
			}
			return oMessage.parseAnswer(sAnswer);
		}
		catch (UnexpectedAnswerException e)
		{
			log("Unexpected answer: " + e.getMessage());
			throw e;
		}
		catch (IOException e)
		{
			log("IOException", e);
			throw new UnexpectedAnswerException(e);
		}
	}

	protected void processMessage(Message oMessage)
	{
		// Send the message
		String sMessageString = oMessage.serialize();

		log("-> " + sMessageString);
		
		m_oWriter.println(sMessageString);
		m_oWriter.flush();

		if(oMessage instanceof ReportExitMessage)
		{
			destroy();
		}
	}
	
	protected void destroy()
	{
		try
		{
			m_oReader.close();
			m_oWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void log(String sMessage)
	{
		if(m_oMessageLogger != null)
		{
			m_oMessageLogger.info(sMessage);
		}
	}

	private void log(String sMessage, Throwable oThrowable)
	{
		if(m_oMessageLogger != null)
		{
			m_oMessageLogger.info(sMessage, oThrowable);
		}
	}
}
