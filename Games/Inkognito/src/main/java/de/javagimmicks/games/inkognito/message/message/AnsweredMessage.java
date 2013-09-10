package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.Answer;

public interface AnsweredMessage<A extends Answer> extends Message
{
	public abstract A parseAnswer(String sAnswer) throws UnexpectedAnswerException;

}
