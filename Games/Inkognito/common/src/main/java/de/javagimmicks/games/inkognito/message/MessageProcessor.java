package de.javagimmicks.games.inkognito.message;

import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.message.AnsweredMessage;
import de.javagimmicks.games.inkognito.message.message.Message;

public interface MessageProcessor
{
	public void processMessage(Message oMessage);
	public <A extends Answer> A processAnsweredMessage(AnsweredMessage<A> oMessage) throws UnexpectedAnswerException;
}
