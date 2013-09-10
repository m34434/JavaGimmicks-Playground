package de.javagimmicks.games.inkognito.context;

import java.util.Collection;
import java.util.List;

import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public interface VisitsContext
{

	public List<Location> getVisitableLocations(Person oPerson);

	public Collection<Location> getVisitedLocations(Person oPerson);

	public boolean isLocationVisitable(Person oPerson, Location oLocation);

	public void notifyPersonMove(Person oPerson, Location oLocation);

	public void reset();

}