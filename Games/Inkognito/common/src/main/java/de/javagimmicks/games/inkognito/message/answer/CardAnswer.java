package de.javagimmicks.games.inkognito.message.answer;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.model.Card;

public class CardAnswer implements Answer
{
	public static CardAnswer fromName(String sName) throws UnexpectedAnswerException
	{
		try
		{
			Card oResult = Card.fromId(sName);
			return new CardAnswer(oResult);
		}
		catch (IllegalArgumentException e)
		{
			throw new UnexpectedAnswerException("Unknown card name '" + sName + "'!", e);
		}
	}
	
	private final Card m_oCard;

	public CardAnswer(final Card oCard)
	{
		m_oCard = oCard;
	}

	public Card getCard()
	{
		return m_oCard;
	}

	public String serialize()
	{
		return m_oCard.toString();
	}
}
