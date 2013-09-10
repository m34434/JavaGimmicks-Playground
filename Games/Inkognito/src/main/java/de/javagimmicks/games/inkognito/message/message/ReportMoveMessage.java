package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public class ReportMoveMessage implements Message
{
	private final String m_sPerson;
	private final Location m_oLocation;
	
	public static ReportMoveMessage fromPersonAndLocation(Person oPerson, Location oLocation)
	{
		return new ReportMoveMessage(oPerson.getName(), oLocation);
	}
	
	public ReportMoveMessage(final String sPerson, final Location oLocation)
	{
		m_sPerson = sPerson;
		m_oLocation = oLocation;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_MOVE)
			.append(' ')
			.append(m_sPerson)
			.append(' ')
			.append(m_oLocation)
			.toString();
	}

	public Location getLocation()
	{
		return m_oLocation;
	}

	public String getPersonName()
	{
		return m_sPerson;
	}
	
}
