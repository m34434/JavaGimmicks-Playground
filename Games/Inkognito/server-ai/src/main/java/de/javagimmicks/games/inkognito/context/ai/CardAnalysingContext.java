package de.javagimmicks.games.inkognito.context.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Person;
import net.sf.javagimmicks.math.Permuter;

public class CardAnalysingContext
{
   private final PlayerContext playerContext;

   private ArrayList<Person> m_oPlayers = new ArrayList<Person>();
   private LinkedList<List<CardPair>> m_oPossibleSolutions = new LinkedList<List<CardPair>>();
   private Map<Person, Set<CardPair>> m_oPlayerPossibleSolutions = new HashMap<Person, Set<CardPair>>();
   
   public CardAnalysingContext(PlayerContext playerContext)
   {
      this.playerContext = playerContext;
   }
   
   public boolean isSolutionFound()
   {
      return m_oPossibleSolutions.size() == 1;
   }
   
   public boolean isPlayerIdKnown(Person oPlayer)
   {
      // Get the index of the player in the solution combinations
      int iPlayerIndex = m_oPlayers.indexOf(oPlayer);
      if(iPlayerIndex == -1)
      {
         return false;
      }
      
      CardPair oFoundPair = null;
      
      // Iterator over all remaining solutions and put each card pair for the player in the map
      for(List<CardPair> oPossibleSolution : m_oPossibleSolutions)
      {
         CardPair oPair = oPossibleSolution.get(iPlayerIndex);
         
         if(oFoundPair == null)
         {
            oFoundPair = oPair;
         }
         else if(oFoundPair != oPair)
         {
            return false;
         }
      }
      
      return true;
   }
   
   public boolean isPlayerNameKnown(Person oPlayer)
   {
      // Get the index of the player in the solution combinations
      int iPlayerIndex = m_oPlayers.indexOf(oPlayer);
      if(iPlayerIndex == -1)
      {
         return false;
      }

      Card oFoundCard = null;
      
      // Iterator over all remaining solutions and put each card pair for the player in the map
      for(List<CardPair> oPossibleSolution : m_oPossibleSolutions)
      {
         Card oNameCard = oPossibleSolution.get(iPlayerIndex).getCard1();
         
         if(oFoundCard == null)
         {
            oFoundCard = oNameCard;
         }
         else if(oFoundCard != oNameCard)
         {
            return false;
         }
      }
      
      return true;
   }
   
   public boolean isPlayerTelephoneKnown(Person oPlayer)
   {
      // Get the index of the player in the solution combinations
      int iPlayerIndex = m_oPlayers.indexOf(oPlayer);
      if(iPlayerIndex == -1)
      {
         return false;
      }

      Card oFoundCard = null;
      
      // Iterator over all remaining solutions and put each card pair for the player in the map
      for(List<CardPair> oPossibleSolution : m_oPossibleSolutions)
      {
         Card oNameCard = oPossibleSolution.get(iPlayerIndex).getCard2();
         
         if(oFoundCard == null)
         {
            oFoundCard = oNameCard;
         }
         else if(oFoundCard != oNameCard)
         {
            return false;
         }
      }
      
      return true;
   }
   
   public Set<Card> getPossiblePlayerTelephones(Person oPlayer)
   {
      Set<Card> oResult = new HashSet<Card>();
      
      for(CardPair oPair : getPossiblePlayerId(oPlayer))
      {
         oResult.add(oPair.getCard2());
      }
      
      return oResult;
   }
   
   public Set<Card> getPossiblePlayerNames(Person oPlayer)
   {
      Set<Card> oResult = new HashSet<Card>();
      
      for(CardPair oPair : getPossiblePlayerId(oPlayer))
      {
         oResult.add(oPair.getCard1());
      }
      
      return oResult;
   }
   
   public Set<CardPair> getPossiblePlayerId(Person oPlayer)
   {
      Set<CardPair> oResult = m_oPlayerPossibleSolutions.get(oPlayer);
      return oResult == null ? new HashSet<CardPair>() : oResult;
   }
   
   public void notifyCardPairSeen(Person oPlayer, CardPair oSeenPair)
   {
      // Get the index of the player in the solution combinations
      int iPlayerIndex = m_oPlayers.indexOf(oPlayer);
      if(iPlayerIndex == -1)
      {
         return;
      }
      
      // Clear the map of possible player solutions, since it will be updated now
      m_oPlayerPossibleSolutions.clear();
      
      // Iterate over all remaining solutions
      for(Iterator<List<CardPair>> iterSolutions = m_oPossibleSolutions.iterator(); iterSolutions.hasNext();)
      {
         // Get the current solution and the respective card pair for the player
         List<CardPair> oPossibleSolution = iterSolutions.next();
         CardPair oPlayerCardPair = oPossibleSolution.get(iPlayerIndex);
         
         // If no seen information matches, this solution can be removed
         if(!oPlayerCardPair.containsCard(oSeenPair.getCard1()) && !oPlayerCardPair.containsCard(oSeenPair.getCard2()))
         {
            iterSolutions.remove();
         }
         // If on information matches update the map of remaining possible player ids
         else
         {
            updatePlayerPossibleSolutions(oPossibleSolution);
         }
      }
      
      updateKnownInformation();
   }
   
   public void notifyCardSeen(Person oPlayer, Card oSeenCard)
   {
      // Get the index of the player in the solution combinations
      int iPlayerIndex = m_oPlayers.indexOf(oPlayer);
      if(iPlayerIndex == -1)
      {
         return;
      }
      
      // Clear the map of possible player solutions, since it will be updated now
      m_oPlayerPossibleSolutions.clear();
      
      // Iterate over all remaining solutions
      for(Iterator<List<CardPair>> iterSolutions = m_oPossibleSolutions.iterator(); iterSolutions.hasNext();)
      {
         // Get the current solution and the respective card pair for the player
         List<CardPair> oPossibleSolution = iterSolutions.next();
         CardPair oPlayerCardPair = oPossibleSolution.get(iPlayerIndex);
         
         // If no seen information matches, this solution can be removed
         if(!oPlayerCardPair.containsCard(oSeenCard))
         {
            iterSolutions.remove();
         }
         // If on information matches update the map of remaining possible player ids
         else
         {
            updatePlayerPossibleSolutions(oPossibleSolution);
         }
      }
      
      updateKnownInformation();
   }
   
   public void init(List<Person> oPlayers, List<Card> oNameCards, List<Card> oTelephoneCards)
   {
      if(oPlayers.size() != oNameCards.size() || oPlayers.size() != oTelephoneCards.size())
      {
         throw new IllegalArgumentException("Number of players, name cards and telephone cards must be equal!");
      }
      
      m_oPlayers.ensureCapacity(oPlayers.size());
      m_oPlayers.addAll(oPlayers);
      
      for(List<Card> oNameCardPermutation : new Permuter<Card>(oNameCards))
      {
         for(List<Card> oTelephoneCardPermutation : new Permuter<Card>(oTelephoneCards))
         {
            // Prepare a list of card pairs for the current rotation
            List<CardPair> oPossibleSolution = new ArrayList<CardPair>(oNameCardPermutation.size());

            // Build all card pairs for the current rotation
            Iterator<Card> iterNameCards = oNameCardPermutation.iterator();
            Iterator<Card> iterTelephoneCards = oTelephoneCardPermutation.iterator();
            while(iterNameCards.hasNext())
            {
               oPossibleSolution.add(new CardPair(iterNameCards.next(), iterTelephoneCards.next()));
            }

            oPossibleSolution = Collections.unmodifiableList(oPossibleSolution);
            
            // Update possible solution data
            updatePlayerPossibleSolutions(oPossibleSolution);
            m_oPossibleSolutions.add(oPossibleSolution);
         }
      }
   }
   
   public List<List<CardPair>> getRemainingSolutions()
   {
      return Collections.unmodifiableList(m_oPossibleSolutions);
   }
   
   public void reset()
   {
      m_oPlayers.clear();
      m_oPossibleSolutions.clear();
      m_oPlayerPossibleSolutions.clear();
   }
   
   private void updatePlayerPossibleSolutions(List<CardPair> oPossibleSolution)
   {
      // Iterate over all single card pairs of the solution
      for(ListIterator<CardPair> iterPlayerId = oPossibleSolution.listIterator(); iterPlayerId.hasNext();)
      {
         // Get the card pair and the respective player
         CardPair oCurrentPlayerId = iterPlayerId.next();
         Person oCurrentPlayer = m_oPlayers.get(iterPlayerId.previousIndex());
         
         // Get or create the map of remaining possible solutions for that player
         Set<CardPair> oPlayerPossibleSolution = m_oPlayerPossibleSolutions.get(oCurrentPlayer);
         if(oPlayerPossibleSolution == null)
         {
            oPlayerPossibleSolution = new HashSet<CardPair>();
            m_oPlayerPossibleSolutions.put(oCurrentPlayer, oPlayerPossibleSolution);
         }
         
         // Add the card pair
         oPlayerPossibleSolution.add(oCurrentPlayerId);
      }
   }
   
   private void updateKnownInformation()
   {
      for(Person oPlayer : m_oPlayers)
      {
         Set<Card> oPossiblePlayerNames = getPossiblePlayerNames(oPlayer);
         if(oPossiblePlayerNames.size() == 1)
         {
            playerContext.setNameCard(oPlayer, oPossiblePlayerNames.iterator().next());
         }
         
         Set<Card> oPossiblePlayerTelephones = getPossiblePlayerTelephones(oPlayer);
         if(oPossiblePlayerTelephones.size() == 1)
         {
            playerContext.setTelephoneCard(oPlayer, oPossiblePlayerTelephones.iterator().next());
         }
      }
   }
   
   public static void main(String[] args)
   {
      Person oPlayer1 = Person.Player1;
      Person oPlayer2 = Person.Player2;
      Person oPlayer3 = Person.Player3;
      
      List<Person> oPlayers = Arrays.asList(new Person[]{oPlayer1, oPlayer2, oPlayer3});
      List<Card> oNameCards = Arrays.asList(new Card[]{Card.AgentX, Card.ColonelBubble, Card.LordFiddleBottom});
      List<Card> oTelephoneCards = Arrays.asList(new Card[]{Card.T0, Card.T11, Card.T52});
      
      CardAnalysingContext oContext = new CardAnalysingContext(new PlayerContext());
      oContext.init(oPlayers, oNameCards, oTelephoneCards);
      
      printRemainingSolutions(oContext);
      showCard(oContext, oPlayer1, Card.T52);
      showCardPair(oContext, oPlayer3, Card.T11, Card.ColonelBubble);
      showCard(oContext, oPlayer3, Card.T11);
      showCardPair(oContext, oPlayer3, Card.LordFiddleBottom, Card.T0);
      showCardPair(oContext, oPlayer2, Card.AgentX, Card.ColonelBubble);
      showCardPair(oContext, oPlayer3, Card.T11, Card.LordFiddleBottom);
      showCardPair(oContext, oPlayer1, Card.ColonelBubble, Card.MadameZsaZsa);
   }

   private static void printRemainingSolutions(CardAnalysingContext oContext)
   {
      for(List<CardPair> oSolution : oContext.getRemainingSolutions())
      {
         System.out.println(oSolution);
      }
      System.out.println();
   }
   
   private static void showCard(CardAnalysingContext oContext, Person oPlayer, Card oCard)
   {
      System.out.println(oPlayer + " : " + oCard);
      oContext.notifyCardSeen(oPlayer, oCard);
      
      printRemainingSolutions(oContext);
   }

   private static void showCardPair(CardAnalysingContext oContext, Person oPlayer, Card oCard1, Card oCard2)
   {
      CardPair oCardPair = new CardPair(oCard1, oCard2);
      
      System.out.println(oPlayer + " : " + oCardPair);
      oContext.notifyCardPairSeen(oPlayer, oCardPair);
      
      printRemainingSolutions(oContext);
   }
}