package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;

public class AskMeetMessage implements AnsweredMessage<NameAnswer>
{
	public static final AskMeetMessage INSTANCE = new AskMeetMessage();
	
	private AskMeetMessage() { }
	
	public String serialize()
	{
		return SIG_ASK_MEET;
	}

	public NameAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return new NameAnswer(sAnswer);
	}
	
}
