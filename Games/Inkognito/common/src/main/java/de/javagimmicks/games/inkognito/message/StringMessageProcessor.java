package de.javagimmicks.games.inkognito.message;

import java.util.StringTokenizer;

import de.javagimmicks.games.inkognito.message.answer.Answer;
import de.javagimmicks.games.inkognito.message.message.AskMeetMessage;
import de.javagimmicks.games.inkognito.message.message.AskMoveMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.AskShowMessage;
import de.javagimmicks.games.inkognito.message.message.ReportEndMessage;
import de.javagimmicks.games.inkognito.message.message.ReportExitMessage;
import de.javagimmicks.games.inkognito.message.message.ReportIdMessage;
import de.javagimmicks.games.inkognito.message.message.ReportMoveMessage;
import de.javagimmicks.games.inkognito.message.message.ReportNameMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeEnvoyMessage;
import de.javagimmicks.games.inkognito.message.message.ReportSeeMessage;
import de.javagimmicks.games.inkognito.message.message.ReportWinLooseMessage;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;

public class StringMessageProcessor implements MessageConstants
{
   private StringMessageProcessor()
   {}

   @SuppressWarnings("unchecked")
   public static <A extends Answer> A processStringMessage(DispatchedMessageProcessor p, String sMessage)
         throws IllegalArgumentException
   {
      StringTokenizer oTokenizer = new StringTokenizer(sMessage);

      String sMessageType = oTokenizer.nextToken();

      A answer = null;
      
      if (SIG_REP_NAME.equals(sMessageType))
      {
         p.processReportNameMessage(new ReportNameMessage(Person.valueOf(oTokenizer.nextToken())));
      }
      else if (SIG_REP_ID.equals(sMessageType))
      {
         Card oNameCard = Card.fromId(oTokenizer.nextToken());
         Card oTelephoneCard = Card.fromId(oTokenizer.nextToken());

         p.processReportIdMessage(new ReportIdMessage(oNameCard, oTelephoneCard));
      }
      else if (SIG_REP_MOVE.equals(sMessageType))
      {
         Person oPerson = Person.valueOf(oTokenizer.nextToken());
         Location oLocation = Location.valueOf(oTokenizer.nextToken());

         p.processReportMoveMessage(new ReportMoveMessage(oPerson, oLocation));
      }
      else if (SIG_REP_SEE.equals(sMessageType))
      {
         Person person = Person.valueOf(oTokenizer.nextToken());
         Card oCard1 = Card.fromId(oTokenizer.nextToken());

         if (oTokenizer.hasMoreTokens())
         {
            Card oCard2 = Card.fromId(oTokenizer.nextToken());

            p.processReportSeeMessage(new ReportSeeMessage(person, new CardPair(oCard1, oCard2)));
         }
         else
         {
            p.processReportSeeEnvoyMessage(new ReportSeeEnvoyMessage(person, oCard1));
         }
      }
      else if (SIG_REP_END.equals(sMessageType))
      {
         StringBuffer oMessage = new StringBuffer();
         while (oTokenizer.hasMoreTokens())
         {
            oMessage.append(' ').append(oTokenizer.nextToken());
         }

         p.processReportEndMessage(new ReportEndMessage(oMessage.substring(1)));
      }
      else if (SIG_REP_WINNER.equals(sMessageType))
      {
         Person oPlayer = Person.valueOf(oTokenizer.nextToken());
         Card oNameCard = Card.fromId(oTokenizer.nextToken());
         Card oTelephoneCard = Card.fromId(oTokenizer.nextToken());

         p.processReportWinLooseMessage(new ReportWinLooseMessage(oPlayer, new CardPair(oNameCard, oTelephoneCard), true));
      }
      else if (SIG_REP_LOOSER.equals(sMessageType))
      {
         Person oPlayer = Person.valueOf(oTokenizer.nextToken());
         Card oNameCard = Card.fromId(oTokenizer.nextToken());
         Card oTelephoneCard = Card.fromId(oTokenizer.nextToken());

         p.processReportWinLooseMessage(new ReportWinLooseMessage(oPlayer, new CardPair(oNameCard, oTelephoneCard), false));
      }
      else if (SIG_REP_EXIT.equals(sMessageType))
      {
         p.processReportExitMessage(ReportExitMessage.INSTANCE);
      }
      else if (SIG_ASK_MEET.equals(sMessageType))
      {
         answer = (A)p.processAskMeetMessage(AskMeetMessage.INSTANCE);
      }
      else if (SIG_ASK_MOVE.equals(sMessageType))
      {
         answer = (A)p.processAskMoveMessage(AskMoveMessage.INSTANCE);
      }
      else if (SIG_ASK_SHOW.equals(sMessageType))
      {
         Person player = Person.valueOf(oTokenizer.nextToken());

         if (Person.Envoy == player)
         {
            player = Person.valueOf(oTokenizer.nextToken());

            answer = (A)p.processAskShowEnvoyMessage(new AskShowEnvoyMessage(player));
         }
         else
         {
            answer = (A)p.processAskShowMessage(new AskShowMessage(player));
         }
      }
      else
      {
         throw new IllegalArgumentException("Unknown message type!");
      }
      
      return answer;
   }
}
