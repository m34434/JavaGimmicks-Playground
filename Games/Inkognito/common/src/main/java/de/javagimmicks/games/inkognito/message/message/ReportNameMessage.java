package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.Person;

public class ReportNameMessage implements Message
{
	private final Person player;

	public ReportNameMessage(Person player)
	{
		this.player = player;
	}
	
	public String serialize()
	{
	   return new StringBuilder().append(SIG_REP_NAME).append(' ').append(player.name()).toString();
	}
	
	public Person getPlayer()
	{
		return player;
	}
}
