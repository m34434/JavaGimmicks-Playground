package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.model.Person;

public class AskShowEnvoyMessage implements AnsweredMessage<CardAnswer>
{
	private final Person player;

	public AskShowEnvoyMessage(final Person player)
	{
		this.player = player;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_ASK_SHOW)
			.append(" envoy ")
			.append(player.name())
			.toString();
	}

	public Person getPlayer()
	{
		return player;
	}

	public CardAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return CardAnswer.fromName(sAnswer);
	}
	
}
