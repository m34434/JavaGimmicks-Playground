package de.javagimmicks.games.inkognito.context;

import static de.javagimmicks.games.inkognito.model.CardType.Name;
import static de.javagimmicks.games.inkognito.model.CardType.Telephone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Person;

public class PlayerContext
{
   private static final List<Person> players;
   
   private final LinkedList<Person> m_oPlayerRotationList = new LinkedList<>();

   private final Map<Person, Card> m_oIdCards = new HashMap<>();
   private final Map<Person, Card> m_oTelephoneCards = new HashMap<>();

   public void reset()
   {
      m_oPlayerRotationList.clear();
      m_oIdCards.clear();
      m_oTelephoneCards.clear();

      for (int i = 0; i < 4; ++i)
      {
         m_oPlayerRotationList.add(Person.values()[i]);
      }
   }

   public void rotatePlayers()
   {
      m_oPlayerRotationList.addLast(m_oPlayerRotationList.removeFirst());
   }

   public List<Person> getPlayersRotated()
   {
      return Collections.unmodifiableList(m_oPlayerRotationList);
   }

   public Card getNameCard(Person player)
   {
      return m_oIdCards.get(player);
   }

   public void setNameCard(Person player, Card card)
   {
      if (card != null && card.getCardType() != Name)
      {
         throw new IllegalArgumentException("Name card must be of card type 'Name'!");
      }

      m_oIdCards.put(player, card);
   }

   public Card getTelephoneCard(Person player)
   {
      return m_oTelephoneCards.get(player);
   }

   public void setTelephoneCard(Person player, Card card)
   {
      if (card != null && card.getCardType() != Telephone)
      {
         throw new IllegalArgumentException("Telephone card must be of card type 'Telephone'!");
      }

      m_oTelephoneCards.put(player, card);
   }

   public CardPair getId(Person player)
   {
      return new CardPair(m_oIdCards.get(player), m_oTelephoneCards.get(player));
   }
   
   public boolean isIdKnown(Person player)
   {
      return isNameKnown(player) && isTelephoneKnown(player);
   }

   public boolean isNameKnown(Person player)
   {
      return getNameCard(player) != null;
   }

   public boolean isTelephoneKnown(Person player)
   {
      return getTelephoneCard(player) != null;
   }
   
   public static List<Person> getPlayers()
   {
      return players;
   }
   
   static
   {
      final List<Person> playerList = new ArrayList<>();
      
      for(Person p : Person.values())
      {
         if(Person.Envoy != p)
         {
            playerList.add(p);
         }
      }
      
      players = Collections.unmodifiableList(playerList);
   }

}