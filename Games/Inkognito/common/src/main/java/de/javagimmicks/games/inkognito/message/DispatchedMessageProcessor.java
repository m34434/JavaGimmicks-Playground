package de.javagimmicks.games.inkognito.message;

import de.javagimmicks.games.inkognito.message.answer.CardAnswer;
import de.javagimmicks.games.inkognito.message.answer.LocationAnswer;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;
import de.javagimmicks.games.inkognito.message.answer.ShowAnswer;
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

public interface DispatchedMessageProcessor
{
   NameAnswer processAskMeetMessage(AskMeetMessage oMessage);

   LocationAnswer processAskMoveMessage(
         AskMoveMessage oMessage);

   CardAnswer processAskShowEnvoyMessage(
         AskShowEnvoyMessage oMessage);

   ShowAnswer processAskShowMessage(AskShowMessage oMessage);

   void processReportNameMessage(
         ReportNameMessage oMessage);

   void processReportIdMessage(ReportIdMessage oMessage);

   void processReportMoveMessage(ReportMoveMessage oMessage);

   void processReportSeeEnvoyMessage(
         ReportSeeEnvoyMessage oMessage);

   void processReportSeeMessage(ReportSeeMessage oMessage);

   void processReportWinLooseMessage(
         ReportWinLooseMessage oMessage);

   void processReportEndMessage(ReportEndMessage oMessage);

   void processReportExitMessage(ReportExitMessage oMessage);

}
