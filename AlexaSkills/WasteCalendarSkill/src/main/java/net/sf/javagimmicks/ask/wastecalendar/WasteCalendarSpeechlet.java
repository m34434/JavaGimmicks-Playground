package net.sf.javagimmicks.ask.wastecalendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Days;
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
   // Declare your intent names here
   private interface IntentName
   {
      String CHECK_STATUS = "CheckStatus";
      String NEXT_ENTRY_DATE = "GetNextEntryDate";
      String LAST_ENTRY_DATE = "GetLastEntryDate";
      String ADD_ENTRY = "AddEntry";
      String GET_DATE_ENTRIES = "GetDateEntries";
      String REMOVE_DATE_ENTRY = "RemoveDateEntry";
      String REMOVE_DATE = "RemoveDate";
   }
   
   // Declare your slot types
   private interface Slot
   {
      String DATE = "Date";
      String TYPE = "Type";
   }

   // Declare your message keys here
   private interface Msg
   {
      String WELCOME = "welcome";
      String WELCOME_REPROMPT = "welcome.reprompt";
      String GOODBYE = "goodbye";
      String SLOT_DATE_EMPTY = "slot.date.empty";
      String SLOT_DATE_FORMAT = "slot.date.format";
      String SLOT_TYPE_EMPTY = "slot.type.empty";
      String INTENT_CHECK_STATUS_TODAY = "intent.checkStatus.today";
      String INTENT_CHECK_STATUS_TOMORROW = "intent.checkStatus.tomorrow";
      String INTENT_CHECK_STATUS_DAYS = "intent.checkStatus.days";
      String INTENT_CHECK_STATUS_NONE = "intent.checkStatus.none";
      String INTENT_NEXT_ENTRY_DATE_TODAY = "intent.getNextEntryDate.today";
      String INTENT_NEXT_ENTRY_DATE_TOMORROW = "intent.getNextEntryDate.tomorrow";
      String INTENT_NEXT_ENTRY_DATE_DAYS = "intent.getNextEntryDate.days";
      String INTENT_NEXT_ENTRY_DATE_NONE = "intent.getNextEntryDate.none";
      String INTENT_LAST_ENTRY_DATE_RESULT = "intent.getLastEntryDate.result";
      String INTENT_LAST_ENTRY_DATE_NONE = "intent.getLastEntryDate.none";
      String INTENT_ADD_ENTRY_OK = "intent.addEntry.ok";
      String INTENT_ADD_ENTRY_DUPLICATE = "intent.addEntry.duplicate";
      String INTENT_GET_DATE_ENTRIES_NONE = "intent.getDateEntries.none";
      String INTENT_GET_DATE_ENTRIES_RESULT = "intent.getDateEntries.result";
      String INTENT_REMOVE_DATE_ENTRY_NOT_FOUND = "intent.removeDateEntry.notFound";
      String INTENT_REMOVE_DATE_ENTRY_SUCCESS = "intent.removeDateEntry.success";
      String INTENT_REMOVE_DATE_NOT_FOUND = "intent.removeDate.notFound";
      String INTENT_REMOVE_DATE_SUCCESS = "intent.removeDate.success";
   }
   
   private static final String ATTR_DATA = "___data___";

   private AmazonDynamoDB db;

   protected SpeechletResponse onLaunchInternal(LaunchRequest request) throws SpeechletResponseThrowable
   {
      return newSpeechletAskResponseWithReprompt(Msg.WELCOME, Msg.WELCOME_REPROMPT);
   }

   protected SpeechletResponse onIntentInternal(IntentRequest request) throws SpeechletResponseThrowable
   {
      final Intent intent = request.getIntent();
      final String intentName = intent.getName();
      
      ////////////////////////////
      // Canceling and stopping //
      ////////////////////////////
      if(INTENT_STOP.equals(intentName) || INTENT_CANCEL.equals(intentName))
      {
         return newSpeechletTellResponse(Msg.GOODBYE);
      }
      
      final CalendarData calendarData = getCalendarData();
   
      //////////////////
      // Check status //
      //////////////////
      if(IntentName.CHECK_STATUS.equals(intentName))
      {
         final LocalDate currentDay = LocalDate.fromDateFields(request.getTimestamp());
         
         boolean changed = false;
         
         final Map<String, List<String>> calendarEntriesForDate = calendarData.getData().getEntries();
         for(Iterator<Entry<String, List<String>>> i = calendarEntriesForDate.entrySet().iterator(); i.hasNext();)
         {
            final Entry<String, List<String>> e = i.next();
            final LocalDate refDay = LocalDate.parse(e.getKey());
            
            final int offset = Days.daysBetween(currentDay, refDay).getDays();

            // Remove outdated entries
            if(offset < 0)
            {
               i.remove();
               changed = true;
               continue;
            }
            
            // Update persistence
            if(changed)
            {
               setSessionAttributeAsJson(ATTR_DATA, calendarData);
               WasteCalendarDao.save(getDb(), calendarData);
            }
            
            final List<String> entries = e.getValue();
            if(entries == null || entries.isEmpty())
            {
               return newSpeechletAskResponseWithReprompt(Msg.INTENT_CHECK_STATUS_NONE, Msg.WELCOME_REPROMPT);
            }
            
            if(offset == 0)
            {
               return newSpeechletAskResponseWithReprompt(Msg.INTENT_CHECK_STATUS_TODAY, Msg.WELCOME_REPROMPT, String.join(", ", e.getValue()));
            }
            else if(offset == 1)
            {
               return newSpeechletAskResponseWithReprompt(Msg.INTENT_CHECK_STATUS_TOMORROW, Msg.WELCOME_REPROMPT, String.join(", ", e.getValue()));
            }
            else if(offset <= 3)
            {
               return newSpeechletAskResponseWithReprompt(Msg.INTENT_CHECK_STATUS_DAYS, Msg.WELCOME_REPROMPT, offset, String.join(", ", e.getValue()));
            }
         }
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_CHECK_STATUS_NONE, Msg.WELCOME_REPROMPT);
      }
      
      /////////////////////////
      // Get next entry date //
      /////////////////////////
      if(IntentName.NEXT_ENTRY_DATE.equals(intentName))
      {
         final String type = getSlot(intent, Slot.TYPE, Msg.SLOT_TYPE_EMPTY, Msg.WELCOME_REPROMPT).toLowerCase();
         
         final LocalDate currentDay = LocalDate.fromDateFields(request.getTimestamp());

         boolean changed = false;

         for(Iterator<Entry<String, List<String>>> i = calendarData.getData().getEntries().entrySet().iterator(); i.hasNext();)
         {
            final Entry<String, List<String>> e = i.next();
            final LocalDate refDay = LocalDate.parse(e.getKey());
            
            final int offset = Days.daysBetween(currentDay, refDay).getDays();
            
            // Remove outdated entries
            if(offset < 0)
            {
               i.remove();
               changed = true;
               continue;
            }
            
            // Update persistence
            if(changed)
            {
               setSessionAttributeAsJson(ATTR_DATA, calendarData);
               WasteCalendarDao.save(getDb(), calendarData);
            }
            
            final List<String> typeList = e.getValue();
            if(typeList != null && typeList.contains(type))
            {
               final String spokenDate = toSpokenDate(refDay);

               if(offset == 0)
               {
                  return newSpeechletAskResponseWithReprompt(Msg.INTENT_NEXT_ENTRY_DATE_TODAY, Msg.WELCOME_REPROMPT, spokenDate, type);
               }
               else if(offset == 1)
               {
                  return newSpeechletAskResponseWithReprompt(Msg.INTENT_NEXT_ENTRY_DATE_TOMORROW, Msg.WELCOME_REPROMPT, spokenDate, type);
               }
               else
               {
                  return newSpeechletAskResponseWithReprompt(Msg.INTENT_NEXT_ENTRY_DATE_DAYS, Msg.WELCOME_REPROMPT, spokenDate, type, offset);
               }
            }
         }
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_NEXT_ENTRY_DATE_NONE, Msg.WELCOME_REPROMPT, type);
      }
      
      /////////////////////////
      // Get last entry date //
      /////////////////////////
      if(IntentName.LAST_ENTRY_DATE.equals(intentName))
      {
         final LocalDate currentDay = LocalDate.fromDateFields(request.getTimestamp());

         Entry<String, List<String>> e = null;
         boolean changed = false;

         for(Iterator<Entry<String, List<String>>> i = calendarData.getData().getEntries().entrySet().iterator(); i.hasNext();)
         {
            e = i.next();
            
            final LocalDate refDay = LocalDate.parse(e.getKey());
            
            final int offset = Days.daysBetween(currentDay, refDay).getDays();
            
            // Remove outdated entries
            if(offset < 0)
            {
               i.remove();
               changed = true;
               continue;
            }
            
            // Update persistence
            if(changed)
            {
               setSessionAttributeAsJson(ATTR_DATA, calendarData);
               WasteCalendarDao.save(getDb(), calendarData);
               changed = false;
            }
            
            if(!i.hasNext())
            {
               return newSpeechletAskResponseWithReprompt(Msg.INTENT_LAST_ENTRY_DATE_RESULT, Msg.WELCOME_REPROMPT, toSpokenDate(refDay), String.join(", ", e.getValue()));
            }
         }
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_LAST_ENTRY_DATE_NONE, Msg.WELCOME_REPROMPT);
      }
      
      /////////////////////
      // Add a new entry //
      /////////////////////
      if(IntentName.ADD_ENTRY.equals(intentName))
      {
         final LocalDate date = getDate(intent);
         final String type = getSlot(intent, Slot.TYPE, Msg.SLOT_TYPE_EMPTY, Msg.WELCOME_REPROMPT).toLowerCase();
         
         final Map<String, List<String>> calendarEntriesForDate = calendarData.getData().getEntries();
         List<String> typeList = calendarEntriesForDate.get(date.toString());
         if(typeList == null)
         {
            typeList = new ArrayList<>();
            calendarEntriesForDate.put(date.toString(), typeList);
         }
         
         if(typeList.contains(type))
         {
            return newSpeechletAskResponseWithReprompt(Msg.INTENT_ADD_ENTRY_DUPLICATE, Msg.WELCOME_REPROMPT, toSpokenDate(date), type);
         }
         
         typeList.add(type);
         
         setSessionAttributeAsJson(ATTR_DATA, calendarData);
         WasteCalendarDao.save(getDb(), calendarData);
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_ADD_ENTRY_OK, Msg.WELCOME_REPROMPT, toSpokenDate(date), type);
      }

      /////////////////////////////////////////
      // Remove an entry from a certain date //
      /////////////////////////////////////////
      if(IntentName.REMOVE_DATE_ENTRY.equals(intentName))
      {
         final LocalDate date = getDate(intent);
         final String type = getSlot(intent, Slot.TYPE, Msg.SLOT_TYPE_EMPTY, Msg.WELCOME_REPROMPT).toLowerCase();
         
         final Map<String, List<String>> calendarEntriesForDate = calendarData.getData().getEntries();
         List<String> typeList = calendarEntriesForDate.get(date.toString());
         if(typeList == null || !typeList.remove(type))
         {
            return newSpeechletAskResponseWithReprompt(Msg.INTENT_REMOVE_DATE_ENTRY_NOT_FOUND, Msg.WELCOME_REPROMPT, toSpokenDate(date), type);
         }
         
         if(typeList.isEmpty())
         {
            calendarEntriesForDate.remove(date.toString());
         }
         
         setSessionAttributeAsJson(ATTR_DATA, calendarData);
         WasteCalendarDao.save(getDb(), calendarData);
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_REMOVE_DATE_ENTRY_SUCCESS, Msg.WELCOME_REPROMPT, toSpokenDate(date), type);
      }

      ////////////////////////////////////////////
      // Remove all entries from a certain date //
      ////////////////////////////////////////////
      if(IntentName.REMOVE_DATE.equals(intentName))
      {
         final LocalDate date = getDate(intent);
         
         final Map<String, List<String>> calendarEntriesForDate = calendarData.getData().getEntries();
         List<String> typeList = calendarEntriesForDate.get(date.toString());
         if(typeList == null || typeList.isEmpty())
         {
            return newSpeechletAskResponseWithReprompt(Msg.INTENT_REMOVE_DATE_NOT_FOUND, Msg.WELCOME_REPROMPT, toSpokenDate(date));
         }
         
         calendarEntriesForDate.remove(date.toString());
         
         setSessionAttributeAsJson(ATTR_DATA, calendarData);
         WasteCalendarDao.save(getDb(), calendarData);
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_REMOVE_DATE_SUCCESS, Msg.WELCOME_REPROMPT, toSpokenDate(date));
      }

      ////////////////////////////////////
      // Get entries for a special date //
      ////////////////////////////////////
      if(IntentName.GET_DATE_ENTRIES.equals(intentName))
      {
         final LocalDate date = getDate(intent);
         
         final Map<String, List<String>> calendarEntriesForDate = calendarData.getData().getEntries();
         List<String> typeList = calendarEntriesForDate.get(date.toString());
         if(typeList == null || typeList.isEmpty())
         {
            return newSpeechletAskResponseWithReprompt(Msg.INTENT_GET_DATE_ENTRIES_NONE, Msg.WELCOME_REPROMPT, toSpokenDate(date));
         }
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_GET_DATE_ENTRIES_RESULT, Msg.WELCOME_REPROMPT, toSpokenDate(date), String.join(", ", typeList));
      }

      return newSpeechletAskResponseWithReprompt("unknownIntent", Msg.WELCOME_REPROMPT);
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
      return getSlotLocalDate(intent, Slot.DATE, Msg.SLOT_DATE_EMPTY, Msg.SLOT_DATE_FORMAT, Msg.WELCOME_REPROMPT);
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
      return date.toString(DateTimeFormat.mediumDate().withLocale(getRequestLocale()));
   }
}
