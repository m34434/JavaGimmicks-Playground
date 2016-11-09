package de.javagimmicks.games.inkognito.message.answer;

import de.javagimmicks.games.inkognito.model.Person;

public class NameAnswer implements Answer
{
	private final Person person;

	public NameAnswer(final Person person)
	{
		this.person = person;
	}

	public Person getPerson()
	{
		return person;
	}

	public String serialize()
	{
		return person.name();
	}
}
