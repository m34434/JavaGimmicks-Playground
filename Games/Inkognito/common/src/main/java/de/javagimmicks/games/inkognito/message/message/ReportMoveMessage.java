package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public class ReportMoveMessage implements Message
{
	private final Person person;
	private final Location m_oLocation;
	
	public ReportMoveMessage(final Person person, final Location oLocation)
	{
	   this.person = person;
		m_oLocation = oLocation;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_MOVE)
			.append(' ')
			.append(person.name())
			.append(' ')
			.append(m_oLocation.name())
			.toString();
	}

	public Location getLocation()
	{
		return m_oLocation;
	}

	public Person getPerson()
	{
		return person;
	}
	
}
