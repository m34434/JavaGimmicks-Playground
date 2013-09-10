package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.Player;

public class ReportSeeEnvoyMessage implements Message
{
	private final String m_sPlayerName;
	private final Card m_oCard;
	
	public static ReportSeeEnvoyMessage fromPlayerAndCard(final Player oShowingPlayer, final Card oShownCard)
	{
		return new ReportSeeEnvoyMessage(oShowingPlayer.getName(), oShownCard);
	}
	
	public ReportSeeEnvoyMessage(final String sPlayerName, final Card oCard)
	{
		m_sPlayerName = sPlayerName;
		m_oCard = oCard;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_SEE)
			.append(' ')
			.append(m_sPlayerName)
			.append(' ')
			.append(m_oCard)
			.toString();
	}

	public String getPlayerName()
	{
		return m_sPlayerName;
	}

	public Card getCard()
	{
		return m_oCard;
	}
}
