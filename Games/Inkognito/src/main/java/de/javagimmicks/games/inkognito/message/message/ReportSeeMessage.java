package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Player;

public class ReportSeeMessage implements Message
{
	private final String m_sPlayerName;
	private final CardPair m_oCardPair;
	
	public static ReportSeeMessage fromPlayerAndCardPair(final Player oShowingPlayer, final CardPair oShownPair)
	{
		return new ReportSeeMessage(oShowingPlayer.getName(), oShownPair);
	}
	
	public ReportSeeMessage(final String sPlayerName, final CardPair oCardPair)
	{
		m_sPlayerName = sPlayerName;
		m_oCardPair = oCardPair;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_SEE)
			.append(' ')
			.append(m_sPlayerName)
			.append(' ')
			.append(m_oCardPair)
			.toString();
	}

	public CardPair getCardPair()
	{
		return m_oCardPair;
	}

	public String getPlayerName()
	{
		return m_sPlayerName;
	}


}
