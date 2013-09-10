package de.javagimmicks.games.inkognito.model;

import static de.javagimmicks.games.inkognito.model.CardType.Name;
import static de.javagimmicks.games.inkognito.model.CardType.Telephone;

public class Player implements Person
{
	private final String m_sName;
	
	private Card m_oNameCard;
	private Card m_oTelephoneCard;

	public Player(final String sName)
	{
		m_sName = sName;
	}
	
	public Player(final String sName, final Card oNameCard, final Card oTelephoneCard)
	{
		this(sName);
		
		setNameCard(oNameCard);
		setTelephoneCard(oTelephoneCard);
	}
	
	public boolean isIdKnown()
	{
		return isNameKnown() && isTelephoneKnown();
	}
	
	public boolean isNameKnown()
	{
		return m_oNameCard != null;
	}
	
	public boolean isTelephoneKnown()
	{
		return m_oTelephoneCard != null;
	}
	
	public void setNameCard(Card oNameCard)
	{
		if(oNameCard != null && oNameCard.getCardType() != Name)
		{
			throw new IllegalArgumentException("Name card must be of card type 'Name'!");
		}
		
		m_oNameCard = oNameCard;
	}
	
	public void setTelephoneCard(Card oTelephoneCard)
	{
		if(oTelephoneCard != null && oTelephoneCard.getCardType() != Telephone)
		{
			throw new IllegalArgumentException("Telephone card must be of card type 'Telephone'!");
		}
		
		m_oTelephoneCard = oTelephoneCard;
	}

	public Card getNameCard()
	{
		return m_oNameCard;
	}

	public Card getTelephoneCard()
	{
		return m_oTelephoneCard;
	}

	public String getName()
	{
		return m_sName;
	}
	
	public String toString()
	{
		return m_sName;
	}
	
	public CardPair getId()
	{
		return new CardPair(m_oNameCard, m_oTelephoneCard);
	}
}