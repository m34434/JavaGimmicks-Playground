package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.Person;

public class ReportSeeEnvoyMessage implements Message
{
	private final Person player;
	private final Card m_oCard;
	
	public ReportSeeEnvoyMessage(final Person player, final Card oCard)
	{
	   this.player = player;
		m_oCard = oCard;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_SEE)
			.append(' ')
			.append(player.name())
			.append(' ')
			.append(m_oCard)
			.toString();
	}

	public Person getPlayer()
	{
		return player;
	}

	public Card getCard()
	{
		return m_oCard;
	}
}
