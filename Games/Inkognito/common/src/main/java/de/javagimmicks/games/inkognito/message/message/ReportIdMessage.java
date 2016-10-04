package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.Card;

public class ReportIdMessage implements Message
{
	private final Card m_oNameCard;
	private final Card m_oTelephoneCard;

	public ReportIdMessage(final Card oNameCard, final Card oTelephoneCard)
	{
		m_oNameCard = oNameCard;
		m_oTelephoneCard = oTelephoneCard;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_ID)
			.append(' ')
			.append(m_oNameCard)
			.append(' ')
			.append(m_oTelephoneCard)
			.toString();
	}
	
	public Card getNameCard()
	{
		return m_oNameCard;
	}
	
	public Card getTelephoneCard()
	{
		return m_oTelephoneCard;
	}
}
