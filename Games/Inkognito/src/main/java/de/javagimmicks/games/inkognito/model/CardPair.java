package de.javagimmicks.games.inkognito.model;

public class CardPair
{
	private final Card m_oCard1;
	private final Card m_oCard2;
	
	public CardPair(final Card oCard1, final Card oCard2)
	{
		if(oCard1 == null || oCard2 == null)
		{
			throw new IllegalArgumentException("Cannot pass a null card to a card pair!");
		}
		
		m_oCard1 = oCard1;
		m_oCard2 = oCard2;
	}

	public boolean containsCard(Card oCard)
	{
		return m_oCard1.equals(oCard) || m_oCard2.equals(oCard);
	}
	
	public Card getCard1()
	{
		return m_oCard1;
	}

	public Card getCard2()
	{
		return m_oCard2;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof CardPair))
		{
			return false;
		}
		
		CardPair oOtherPair = (CardPair) obj;
		
		if(oOtherPair.m_oCard1.equals(this.m_oCard1))
		{
			return oOtherPair.m_oCard2.equals(this.m_oCard2);
		}
		else if(oOtherPair.m_oCard2.equals(this.m_oCard1))
		{
			return oOtherPair.m_oCard1.equals(this.m_oCard2);
		}
		else
		{
			return false;
		}
	}

	public int hashCode()
	{
		return m_oCard1.hashCode() + m_oCard2.hashCode();
	}
	
	public String toString()
	{
		return new StringBuffer()
			.append(m_oCard1)
			.append(' ')
			.append(m_oCard2)
			.toString();
	}
}
