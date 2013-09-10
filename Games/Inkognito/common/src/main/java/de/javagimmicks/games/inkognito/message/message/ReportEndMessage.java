package de.javagimmicks.games.inkognito.message.message;

public class ReportEndMessage implements Message
{
	private final String m_sText;
	
	public ReportEndMessage(final String text)
	{
		m_sText = text;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_REP_END)
			.append(' ')
			.append(m_sText)
			.toString();
	}

	public String getText()
	{
		return m_sText;
	}

}
