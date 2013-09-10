package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Player;

public class ReportWinLooseMessage implements Message
{
	private final String m_sPlayerName;
	private final CardPair m_oPlayerId;
	private final boolean m_bWin;

	public static ReportWinLooseMessage fromPlayer(final Player oPlayer, boolean bWin)
	{
		return new ReportWinLooseMessage(oPlayer.getName(), oPlayer.getId(), bWin);
	}
	
	public ReportWinLooseMessage(final String sPlayerName, final CardPair oPlayerId, final boolean bWin)
	{
		m_sPlayerName = sPlayerName;
		m_oPlayerId = oPlayerId;
		m_bWin = bWin;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(m_bWin ? SIG_REP_WINNER : SIG_REP_LOOSER)
			.append(' ')
			.append(m_sPlayerName)
			.append(' ')
			.append(m_oPlayerId)
			.toString();
	}

	public boolean isWin()
	{
		return m_bWin;
	}

	public String getPlayerName()
	{
		return m_sPlayerName;
	}
	
	public CardPair getPlayerId()
	{
		return m_oPlayerId;
	}
}
