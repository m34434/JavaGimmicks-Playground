package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.model.Person;

public class AskShowMessage implements AnsweredMessage<ShowAnswer>
{
	private final Person player;

	public AskShowMessage(final Person player)
	{
		this.player = player;
	}

	public Person getPlayer()
	{
		return player;
	}
	
	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_ASK_SHOW)
			.append(' ')
			.append(player.name())
			.toString();
	}

	public ShowAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return ShowAnswer.parse(sAnswer);
	}
}
