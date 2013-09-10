package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.model.Player;

public class AskShowEnvoyMessage implements AnsweredMessage<CardAnswer>
{
	private final String m_sPlayerName;

	public static AskShowEnvoyMessage fromPlayer(final Player oAskingPlayer)
	{
		return new AskShowEnvoyMessage(oAskingPlayer.getName());
	}
	
	public AskShowEnvoyMessage(final String sPlayerName)
	{
		m_sPlayerName = sPlayerName;
	}

	public String serialize()
	{
		return new StringBuffer()
			.append(SIG_ASK_SHOW)
			.append(" envoy ")
			.append(m_sPlayerName)
			.toString();
	}

	public String getPlayerName()
	{
		return m_sPlayerName;
	}

	public CardAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
		return CardAnswer.fromName(sAnswer);
	}
	
}
