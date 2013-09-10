package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.LocationAnswer;

public class AskMoveMessage implements AnsweredMessage<LocationAnswer>
{
	public static final AskMoveMessage INSTANCE = new AskMoveMessage();
	
	private AskMoveMessage() { }

	public String serialize()
	{
		return SIG_ASK_MOVE;
	}

	public LocationAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return LocationAnswer.fromName(sAnswer);
	}
	
}
