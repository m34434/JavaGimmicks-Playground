package de.javagimmicks.games.inkognito.context.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.javagimmicks.games.inkognito.context.LocationsContext;
import de.javagimmicks.games.inkognito.context.VisitsContext;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public class DefaultVisitsContext implements VisitsContext
{
	private final Map<Person, Set<Location>> m_oVisitedLocations = new HashMap<Person, Set<Location>>();
	private final LocationsContext m_oLocationsContext;
	
	public DefaultVisitsContext(final LocationsContext oLocationsContext)
	{
		m_oLocationsContext = oLocationsContext;
	}

	public List<Location> getVisitableLocations(Person oPerson)
	{
		List<Location> oResult = new ArrayList<Location>(Arrays.asList(Location.values()));
		oResult.removeAll(getVisitedLocations(oPerson));
		
		return oResult;
	}
	
	public Set<Location> getVisitedLocations(Person oPerson)
	{
		Set<Location> oResult = m_oVisitedLocations.get(oPerson);
		
		if(oResult == null)
		{
			oResult = new HashSet<Location>();
			m_oVisitedLocations.put(oPerson, oResult);
		}
		
		return oResult;
	}

	public boolean isLocationVisitable(Person oPerson, Location oLocation)
	{
		return !getVisitedLocations(oPerson).contains(oLocation);
	}
	
	public void notifyPersonMove(Person oPerson, Location oLocation)
	{
		m_oLocationsContext.notifyPersonMove(oLocation, oPerson);
		
		getVisitedLocations(oPerson).add(oLocation);
	}
	
	public void reset()
	{
		for(Set<Location> oVisitedLocations : m_oVisitedLocations.values())
		{
			oVisitedLocations.clear();
		}
	}
}
