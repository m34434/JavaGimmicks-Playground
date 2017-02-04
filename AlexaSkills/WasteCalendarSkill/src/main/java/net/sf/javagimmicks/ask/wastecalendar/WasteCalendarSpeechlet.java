package net.sf.javagimmicks.ask.wastecalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import net.sf.javagimmicks.ask.base.AbstractSpeechlet;
import net.sf.javagimmicks.ask.base.SpeechletResponseThrowable;
import net.sf.javagimmicks.ask.wastecalendar.model.CalendarData;
import net.sf.javagimmicks.ask.wastecalendar.model.WasteCalendarDao;

public class WasteCalendarSpeechlet extends AbstractSpeechlet
{
   private static final String MSG_WELCOME = "welcome";
   private static final String MSG_WELCOME_REPROMPT = "welcome.reprompt";
   private static final String MSG_GOODBYE = "goodbye";
   private static final String MSG_SLOT_DATE_EMPTY = "slot.date.empty";
   private static final String MSG_SLOT_DATE_FORMAT = "slot.date.format";
   private static final String MSG_SLOT_TYPE_EMPTY = "slot.type.empty";
   private static final String MSG_INTENT_ADD_ENTRY_OK = "intent.addEntry.ok";

   private static final String SLOT_DATE = "Date";
   private static final String SLOT_TYPE = "Type";
   private static final String ATTR_DATA = "___data___";

   private AmazonDynamoDB db;

   protected SpeechletResponse onLaunchInternal(LaunchRequest request) throws SpeechletResponseThrowable
   {
      return newSpeechletAskResponseWithReprompt(MSG_WELCOME, MSG_WELCOME_REPROMPT);
   }

   protected SpeechletResponse onIntentInternal(IntentRequest request) throws SpeechletResponseThrowable
   {
      final Intent intent = request.getIntent();
      final String intentName = intent.getName();
      
      //////////
      // Canceling and stopping
      /////////      
      if("AMAZON.StopIntent".equals(intentName) || "AMAZON.CancelIntent".equals(intentName))
      {
         return newSpeechletTellResponse(MSG_GOODBYE);
      }
      
      final CalendarData calendarData = getCalendarData();
   
      //////////
      // Get the current list name
      /////////      
      if("AddEntry".equals(intentName))
      {
         final LocalDate date = getDate(intent);
         final String type = getSlot(intent, SLOT_TYPE, MSG_SLOT_TYPE_EMPTY, MSG_WELCOME_REPROMPT);
         
         final Map<String, List<String>> calendarEntriesForDate = calendarData.getData().getEntries();
         List<String> typeList = calendarEntriesForDate.get(date.toString());
         if(typeList == null)
         {
            typeList = new ArrayList<>();
            calendarEntriesForDate.put(date.toString(), typeList);
         }
         
         typeList.add(type);
         
         setSessionAttributeAsJson(ATTR_DATA, calendarData);
         WasteCalendarDao.save(getDb(), calendarData);
         
         return newSpeechletAskResponseWithReprompt(MSG_INTENT_ADD_ENTRY_OK, MSG_WELCOME_REPROMPT, toSpokenDate(date), type);
      }

      return newSpeechletAskResponseWithReprompt("unknownIntent", MSG_WELCOME_REPROMPT);
   }
   
   private CalendarData getCalendarData() throws SpeechletResponseThrowable
   {
      CalendarData calendarData = parseSessionAttribute(ATTR_DATA, CalendarData.class); 

      if(calendarData == null)
      {
          log.info("Loading user data from DynamoDB");
          final String userId = getSession().getUser().getUserId();
          calendarData = WasteCalendarDao.load(getDb(), userId);

          if (calendarData == null)
          {
             calendarData = new CalendarData();
             calendarData.setCustomerId(userId);
          }
      }
      
      setSessionAttributeAsJson(ATTR_DATA, calendarData);

      return calendarData;
   }
   
   private LocalDate getDate(Intent intent) throws SpeechletResponseThrowable
   {
      final String dateString = getSlot(intent, SLOT_DATE, MSG_SLOT_DATE_EMPTY, MSG_WELCOME_REPROMPT);
      
      try
      {
         return LocalDate.parse(dateString);
      }
      catch(Exception ex)
      {
         log.warn("Could not parse date string!", ex);
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt(MSG_SLOT_DATE_FORMAT, MSG_WELCOME_REPROMPT));
      }
   }

   private AmazonDynamoDB getDb()
   {
      if (db == null)
      {
         db = AmazonDynamoDBClientBuilder.defaultClient();
      }

      return db;
   }
   
   private String toSpokenDate(LocalDate date)
   {
      return date.toString(DateTimeFormat.fullDate().withLocale(getRequestLocale()));
   }

}
