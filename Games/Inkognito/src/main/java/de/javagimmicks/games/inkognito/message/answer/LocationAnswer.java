package de.javagimmicks.games.inkognito.message.answer;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.model.Location;

public class LocationAnswer implements Answer
{
	public static LocationAnswer fromName(String sName) throws UnexpectedAnswerException
	{
		try
		{
			Location oLocation = Location.valueOf(sName);
			return new LocationAnswer(oLocation);
		}
		catch (IllegalArgumentException e)
		{
			throw new UnexpectedAnswerException("Unknown location name '" + sName + "'!", e);
		}
	}

	private final Location m_oLocation;

	public LocationAnswer(final Location location)
	{
		m_oLocation = location;
	}

	public Location getLocation()
	{
		return m_oLocation;
	}

	public String serialize()
	{
		return m_oLocation.toString();
	}
}
