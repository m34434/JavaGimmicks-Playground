package de.javagimmicks.games.inkognito.server;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.model.Person;

public class PlayerException extends Exception
{
	private static final long serialVersionUID = 6962446743545547090L;

	private final Person m_oPlayer;
	private final boolean m_bWin;
	
	public PlayerException(Person oPlayer, UnexpectedAnswerException ex)
	{
		super(ex.getMessage(), ex);
		
		m_oPlayer = oPlayer;
		m_bWin = false;
	}
	
	public PlayerException(Person oPlayer, String sMessage)
	{
		super(sMessage);
		
		m_oPlayer = oPlayer;
		m_bWin = false;
	}

	public PlayerException(Person oPlayer, String sMessage, boolean bWin)
	{
		super(sMessage);
		
		m_oPlayer = oPlayer;
		m_bWin = bWin;
	}

	public Person getPlayer()
	{
		return m_oPlayer;
	}
	
	public UnexpectedAnswerException getUnexpectedAnswerException()
	{
		Throwable oCause = getCause();
		
		return (oCause instanceof UnexpectedAnswerException) ? ((UnexpectedAnswerException)oCause) : null;
	}
	
	public boolean isWin()
	{
		return m_bWin;
	}
}
