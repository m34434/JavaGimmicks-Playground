package de.javagimmicks.games.inkognito.server.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;

import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.message.AnsweredMessage;
import de.javagimmicks.games.inkognito.message.message.Message;
import de.javagimmicks.games.inkognito.message.message.ReportExitMessage;

public class StreamMessageProcessor implements MessageProcessor
{
	private final BufferedReader m_oReader;
	private final PrintWriter m_oWriter;

	private Log m_oMessageLogger;	
	
	public StreamMessageProcessor(InputStream oInputStream, OutputStream oOutputStream)
	{
		m_oReader = new BufferedReader(new InputStreamReader(oInputStream));
		m_oWriter = new PrintWriter(new OutputStreamWriter(oOutputStream));
	}
	
	public void setMessageLogger(Log oLogger)
	{
		m_oMessageLogger = oLogger;
	}
	
	public <A extends Answer> A processAnsweredMessage(
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

	public void processMessage(Message oMessage)
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
