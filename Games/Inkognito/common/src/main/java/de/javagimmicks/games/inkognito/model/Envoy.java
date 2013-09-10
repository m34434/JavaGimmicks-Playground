package de.javagimmicks.games.inkognito.model;

public class Envoy implements Person
{
	public static Envoy INSTANCE = new Envoy();
	
	private Envoy() { }
	
	public String getName()
	{
		return "envoy";
	}
}
