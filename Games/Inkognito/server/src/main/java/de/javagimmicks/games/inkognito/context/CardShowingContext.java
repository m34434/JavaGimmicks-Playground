package de.javagimmicks.games.inkognito.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.CardType;
import de.javagimmicks.games.inkognito.model.Person;

public class CardShowingContext
{
   private final PlayerContext playerContext;
   
   private final Map<Person, Map<Person, Set<CardPair>>> m_oShownCards = new HashMap<>();
   private final Map<Person, Map<Person, Set<CardType>>> m_oShownIds = new HashMap<>();
   
   public CardShowingContext(PlayerContext playerContext)
   {
      this.playerContext = playerContext;
   }

   public void reset()
   {
      m_oShownCards.clear();
      m_oShownIds.clear();
   }
   
   public boolean mayPlayerShowPair(Person oShowingPlayer, Person oAskingPlayer, CardPair oCardPair)
   {
      if(oCardPair.getCard1() == oCardPair.getCard2())
      {
         return false;
      }
      
      if(!oCardPair.containsCard(playerContext.getTelephoneCard(oShowingPlayer)) && !oCardPair.containsCard(playerContext.getNameCard(oShowingPlayer)))
      {
         return false;
      }
      
      return !getCreatePlayerShownCards(oShowingPlayer, oAskingPlayer).contains(oCardPair);
   }
   
   public void notifiyPlayerShow(Person oShowingPlayer, Person oAskingPlayer, CardPair oCardPair)
   {
      getCreatePlayerShownCards(oShowingPlayer, oAskingPlayer).add(oCardPair);
   }
   
   public boolean mayPlayerAskId(Person oAskingPlayer, Person oShowingPlayer)
   {
      return getCreatePlayerShownIds(oShowingPlayer, oAskingPlayer).size() <= 1;
   }
   
   public boolean mayPlayerShowId(Person oShowingPlayer, Person oAskingPlayer, Card oCard)
   {
      if(!playerContext.getId(oShowingPlayer).containsCard(oCard))
      {
         return false;
      }
      
      return !getCreatePlayerShownIds(oShowingPlayer, oAskingPlayer).contains(oCard.getCardType());
   }
   
   public void notifiyPlayerShow(Person oShowingPlayer, Person oAskingPlayer, Card oCard)
   {
      getCreatePlayerShownIds(oShowingPlayer, oAskingPlayer).add(oCard.getCardType());
   }
   
   private Set<CardPair> getCreatePlayerShownCards(Person oShowingPlayer, Person oAskingPlayer)
   {
      Map<Person, Set<CardPair>> oPlayerShowCards = getCreatePlayerShownCards(oShowingPlayer);
      Set<CardPair> oResult = oPlayerShowCards.get(oAskingPlayer);
      
      if(oResult == null)
      {
         oResult = new HashSet<CardPair>();
         oPlayerShowCards.put(oAskingPlayer, oResult);
      }
      
      return oResult;
   }
   
   private Map<Person, Set<CardPair>> getCreatePlayerShownCards(Person oPlayer)
   {
      Map<Person, Set<CardPair>> oResult = m_oShownCards.get(oPlayer);
      
      if(oResult == null)
      {
         oResult = new HashMap<Person, Set<CardPair>>();
         m_oShownCards.put(oPlayer, oResult);
      }
      
      return oResult;
   }
   
   private Set<CardType> getCreatePlayerShownIds(Person oShowingPlayer, Person oAskingPlayer)
   {
      Map<Person, Set<CardType>> oPlayerShowCards = getCreatePlayerShownIds(oShowingPlayer);
      Set<CardType> oResult = oPlayerShowCards.get(oAskingPlayer);
      
      if(oResult == null)
      {
         oResult = new HashSet<CardType>();
         oPlayerShowCards.put(oAskingPlayer, oResult);
      }
      
      return oResult;
   }
   
   private Map<Person, Set<CardType>> getCreatePlayerShownIds(Person oPlayer)
   {
      Map<Person, Set<CardType>> oResult = m_oShownIds.get(oPlayer);
      
      if(oResult == null)
      {
         oResult = new HashMap<Person, Set<CardType>>();
         m_oShownIds.put(oPlayer, oResult);
      }
      
      return oResult;
   }
}