package de.javagimmicks.games.inkognito.message.answer;


public class NameAnswer implements Answer
{
	private final String m_sName;

	public NameAnswer(final String sName)
	{
		m_sName = sName;
	}

	public String getName()
	{
		return m_sName;
	}

	public String serialize()
	{
		return m_sName;
	}
}
