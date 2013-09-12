package de.javagimmicks.games.inkognito.context;

import java.util.List;

import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public interface LocationsContext
{
	public void reset();

	public void notifyPersonMove(Location oLocation, Person oPerson);

	public List<Person> getVisitors(Location oLocation);
	
	public int getVisitorCount(Location oLocation);

	public Location getCurrentLocation(Person oPerson);

}