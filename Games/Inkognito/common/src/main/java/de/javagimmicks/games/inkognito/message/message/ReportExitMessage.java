package de.javagimmicks.games.inkognito.message.message;

public class ReportExitMessage implements Message
{
	public static final ReportExitMessage INSTANCE = new ReportExitMessage();

	private ReportExitMessage() { };
	
	public String serialize()
	{
		return SIG_REP_EXIT;
	}
}
