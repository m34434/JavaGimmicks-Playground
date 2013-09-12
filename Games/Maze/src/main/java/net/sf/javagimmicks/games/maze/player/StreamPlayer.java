package net.sf.javagimmicks.games.maze.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import net.sf.javagimmicks.games.maze.model.Command;
import net.sf.javagimmicks.games.maze.model.message.Message;
import net.sf.javagimmicks.games.maze.model.message.MessageException;
import net.sf.javagimmicks.games.maze.model.message.StandardMessage;

import org.apache.commons.logging.Log;


public class StreamPlayer implements Player
{
	private final BufferedReader m_oReader;
	private final PrintWriter m_oWriter;

	private Log m_oMessageLogger;	
	
	public StreamPlayer(InputStream oInputStream, OutputStream oOutputStream)
	{
		m_oReader = new BufferedReader(new InputStreamReader(oInputStream));
		m_oWriter = new PrintWriter(new OutputStreamWriter(oOutputStream));
	}
	
	public void setMessageLogger(Log oLogger)
	{
		m_oMessageLogger = oLogger;
	}
	
	public Command getNextCommand(Message oMessage) throws MessageException
	{
		sendMessage(oMessage);
		
		if(oMessage == StandardMessage.FINISH || oMessage == StandardMessage.CRASH)
		{
			destroy();
		}
		
		return receiveCommand();
	}

	private Command receiveCommand() throws MessageException
	{
		try
		{
			String sAnswer = m_oReader.readLine();
			
			log("<- " + sAnswer);

			return Command.valueOf(sAnswer);
		}
		catch (Exception e)
		{
			log("Exception occured while receiving command", e);
			throw new MessageException(e);
		}
	}

	private void sendMessage(Message oMessage)
	{
		// Send the message
		String sMessageString = oMessage.getMessageString();

		log("-> " + sMessageString);
		
		m_oWriter.println(sMessageString);
		m_oWriter.flush();
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
