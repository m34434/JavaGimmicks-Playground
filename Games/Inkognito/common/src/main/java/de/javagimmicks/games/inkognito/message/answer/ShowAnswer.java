package de.javagimmicks.games.inkognito.message.answer;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;

public class ShowAnswer implements Answer
{
	public static ShowAnswer parse(String sAnswer) throws UnexpectedAnswerException
	{
		StringTokenizer oTokenizer = new StringTokenizer(sAnswer, " ");
	
		int iTokenCount = oTokenizer.countTokens();
		
		if(iTokenCount == 2)
		{
			return getNormalAnswer(oTokenizer);
		}
		else
		{
			String sFirstToken = oTokenizer.nextToken();
			
			if(!sFirstToken.equals("phone"))
			{
				throw new UnexpectedAnswerException("Unexpected start of answer '" + sFirstToken + "'!");
			}
			
			return getPhoneAnswer(oTokenizer);
		}
	}
	
	private static ShowAnswer getNormalAnswer(StringTokenizer oTokenizer) throws UnexpectedAnswerException
	{
		Card oCard1 = nextCard(oTokenizer);
		Card oCard2 = nextCard(oTokenizer);
		
		return new ShowAnswer(new CardPair(oCard1, oCard2));
	}
	
	private static ShowAnswer getPhoneAnswer(StringTokenizer oTokenizer) throws UnexpectedAnswerException
	{
		List<CardPair> oSolution = new LinkedList<CardPair>();
		
		while(oTokenizer.hasMoreElements())
		{
			Card oCardA = nextCard(oTokenizer);
			Card oCardB = nextCard(oTokenizer);
			oSolution.add(new CardPair(oCardA, oCardB));
		}

		return new ShowAnswer(oSolution);
	}
	
	private static Card nextCard(StringTokenizer oTokenizer) throws UnexpectedAnswerException
	{
		if(!oTokenizer.hasMoreElements())
		{
			throw new UnexpectedAnswerException("Wrong number of tokens found!");
		}
		
		String sCardName = oTokenizer.nextToken();
		
		try
		{
			return Card.fromId(sCardName);
		}
		catch(IllegalArgumentException ex)
		{
			throw new UnexpectedAnswerException("Unkown card '" + sCardName + "'!", ex);
		}
	}
	
	private final CardPair m_oCardPair;
	private final List<CardPair> m_oSolutionPairs;

	public ShowAnswer(final CardPair oCardPair)
	{
		m_oCardPair = oCardPair;
		m_oSolutionPairs = null;
	}

	public ShowAnswer(final List<CardPair> oSolutionPairs)
	{
		m_oCardPair = null;
		m_oSolutionPairs = oSolutionPairs;
	}

	public boolean isPhoneCall()
	{
		return m_oCardPair == null;
	}

	public CardPair getCardPair()
	{
		return m_oCardPair;
	}

	public List<CardPair> getSolution()
	{
		return m_oSolutionPairs;
	}

	public String serialize()
	{
		if(isPhoneCall())
		{
			StringBuffer oResult = new StringBuffer();
			oResult.append("phone");
			
			for(CardPair oPair : m_oSolutionPairs)
			{
				oResult.append(' ').append(oPair.toString());
			}
			
			return oResult.toString();
		}
		else
		{
			return m_oCardPair.toString();
		}
	}
}
