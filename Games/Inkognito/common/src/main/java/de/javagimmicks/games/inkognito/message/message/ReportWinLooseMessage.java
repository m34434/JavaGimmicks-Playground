package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Person;

public class ReportWinLooseMessage implements Message
{
	private final Person player;
	private final CardPair playerId;
	private final boolean win;

	public ReportWinLooseMessage(final Person player, final CardPair oPlayerId, final boolean bWin)
	{
		this.player = player;
		this.playerId = oPlayerId;
		this.win = bWin;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(win ? SIG_REP_WINNER : SIG_REP_LOOSER)
			.append(' ')
			.append(player)
			.append(' ')
			.append(playerId)
			.toString();
	}

	public boolean isWin()
	{
		return win;
	}

	public Person getPlayer()
	{
		return player;
	}
	
	public CardPair getPlayerId()
	{
		return playerId;
	}
}
