package de.javagimmicks.games.inkognito.server;

import de.javagimmicks.games.inkognito.message.MessageProcessor;
import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;

public class InitializationException extends Exception
{
	private static final long serialVersionUID = 6752244669854639362L;

	private final MessageProcessor m_oProcessor;
	
	public InitializationException(MessageProcessor oProcessor, String sMessage)
	{
		super(sMessage);
		
		m_oProcessor = oProcessor;
	}

	public InitializationException(MessageProcessor oProcessor, UnexpectedAnswerException oCause)
	{
		super(oCause);
		
		m_oProcessor = oProcessor;
	}
	
	public InitializationException(MessageProcessor oProcessor, String sMessage, UnexpectedAnswerException oCause)
	{
		super(sMessage, oCause);
		
		m_oProcessor = oProcessor;
	}

	public MessageProcessor getProcessor()
	{
		return m_oProcessor;
	}

	public UnexpectedAnswerException getUnexpectedAnswerException()
	{
		Throwable oCause = getCause();
		
		return (oCause instanceof UnexpectedAnswerException) ? ((UnexpectedAnswerException)oCause) : null;
	}
}
