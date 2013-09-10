package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;

public class AskNameMessage implements AnsweredMessage<NameAnswer>
{
	public static final AskNameMessage INSTANCE = new AskNameMessage();
	
	private AskNameMessage() { }
	
	public String serialize()
	{
		return SIG_ASK_NAME;
	}

	public NameAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return new NameAnswer(sAnswer);
	}
}
