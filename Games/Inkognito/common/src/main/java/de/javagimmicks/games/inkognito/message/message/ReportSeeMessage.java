package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Person;

public class ReportSeeMessage implements Message
{
	private final Person player;
	private final CardPair m_oCardPair;
	
	public ReportSeeMessage(final Person player, final CardPair oCardPair)
	{
	   this.player = player;
		m_oCardPair = oCardPair;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_SEE)
			.append(' ')
			.append(player.name())
			.append(' ')
			.append(m_oCardPair)
			.toString();
	}

	public CardPair getCardPair()
	{
		return m_oCardPair;
	}

	public Person getPlayer()
	{
		return player;
	}


}
