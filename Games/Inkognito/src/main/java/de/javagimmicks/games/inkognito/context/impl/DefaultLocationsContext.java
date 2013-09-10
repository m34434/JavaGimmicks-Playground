package de.javagimmicks.games.inkognito.context.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public class DefaultLocationsContext implements LocationsContext
{
	private final Map<Location, List<Person>> m_oLocationToPersons = new HashMap<Location, List<Person>>();
	
	private final Map<Person, Location> m_oPersonToLocation = new HashMap<Person, Location>();
	
	public void reset()
	{
		for(List<Person> oPersons : m_oLocationToPersons.values())
		{
			oPersons.clear();
		}
		
		m_oPersonToLocation.clear();
	}
	
	public void notifyPersonMove(Location oLocation, Person oPerson)
	{
		getCreatePersons(oLocation).add(oPerson);

		m_oPersonToLocation.put(oPerson, oLocation);
	}
	
	public List<Person> getVisitors(Location oLocation)
	{
		return getCreatePersons(oLocation);
	}
	
	public int getVisitorCount(Location oLocation)
	{
		return getCreatePersons(oLocation).size();
	}

	public Location getCurrentLocation(Person oPerson)
	{
		return m_oPersonToLocation.get(oPerson);
	}
	
	private List<Person> getCreatePersons(Location oLocation)
	{
		List<Person> oPersons = m_oLocationToPersons.get(oLocation);
		
		if(oPersons == null)
		{
			oPersons = new LinkedList<Person>();
			m_oLocationToPersons.put(oLocation, oPersons);
		}

		return oPersons;
	}
}
