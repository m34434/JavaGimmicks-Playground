package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
import de.javagimmicks.games.inkognito.model.Player;

public class AskShowMessage implements AnsweredMessage<ShowAnswer>
{
	private final String m_sPlayerName;

	public static AskShowMessage fromPlayer(final Player oAskingPlayer)
	{
		return new AskShowMessage(oAskingPlayer.getName());
	}
	
	public AskShowMessage(final String sPlayerName)
	{
		m_sPlayerName = sPlayerName;
	}

	public String getPlayerName()
	{
		return m_sPlayerName;
	}
	
	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_ASK_SHOW)
			.append(' ')
			.append(m_sPlayerName)
			.toString();
	}

	public ShowAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return ShowAnswer.parse(sAnswer);
	}
}
