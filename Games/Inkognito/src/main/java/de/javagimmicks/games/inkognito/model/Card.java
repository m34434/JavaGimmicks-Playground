package de.javagimmicks.games.inkognito.model;

import static de.javagimmicks.games.inkognito.model.CardType.Name;
import static de.javagimmicks.games.inkognito.model.CardType.Telephone;

import java.util.ArrayList;
import java.util.List;

public enum Card
{
	ColonelBubble(Name, "ColonelBubble"),
	LordFiddleBottom(Name, "LordFiddleBottom"),
	MadameZsaZsa(Name, "MadameZsaZsa"),
	AgentX(Name, "AgentX"),
	
	T0(Telephone, "0"),
	T11(Telephone, "11"),
	T29(Telephone, "29"),
	T52(Telephone, "52");
	
	public static Card fromId(String sId)
	{
		for(Card oCard : values())
		{
			if(oCard.m_sId.equals(sId))
			{
				return oCard;
			}
		}
		
		throw new IllegalArgumentException("No card found with specified id '" + sId + "'");
	}
	
	public static int getNameCount()
	{
		return values().length / 2;
	}
	
	public static Card getPartner(Card oCard)
	{
		switch(oCard)
		{
			case ColonelBubble:
				return LordFiddleBottom;
			case LordFiddleBottom:
				return ColonelBubble;
			case MadameZsaZsa:
				return AgentX;
			case AgentX:
				return MadameZsaZsa;
			default:
				throw new IllegalArgumentException("Specified card is not a name card");
		}
	}
	
	public static List<Card> getCardsByType(CardType oType)
	{
		List<Card> oResult = new ArrayList<Card>(getNameCount());
		
		for(Card oCard : values())
		{
			if(oCard.getCardType() == oType)
			{
				oResult.add(oCard);
			}
		}
		
		return oResult;
	}
	
	private final CardType m_oType;
	private final String m_sId;

	private Card(final CardType type, final String sId)
	{
		m_oType = type;
		m_sId = sId;
	};
	
	public CardType getCardType()
	{
		return m_oType;
	}
	
	public boolean isPartner(Card oOtherCard)
	{
		return getPartner(this) == oOtherCard;
	}
	
	public String toString()
	{
		return m_sId;
	}
	
}
