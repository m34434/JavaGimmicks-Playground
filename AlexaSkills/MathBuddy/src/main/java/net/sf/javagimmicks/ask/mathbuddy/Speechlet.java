package net.sf.javagimmicks.ask.mathbuddy;

import static java.util.Optional.ofNullable;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import net.sf.javagimmicks.ask.base.AbstractSpeechlet;
import net.sf.javagimmicks.ask.base.SpeechletResponseThrowable;
import net.sf.javagimmicks.ask.mathbuddy.model.SkillData;
import net.sf.javagimmicks.ask.mathbuddy.model.SkillDataDao;

public class Speechlet extends AbstractSpeechlet
{
   // Declare your intent names here
   private interface IntentName
   {
      String SET_TITLE = "SetTitle";
      
      String ADD_NUMBERS = "AddNumbers";
      String SUBTRACT_NUMBERS = "SubtractNumbers";
      String MULTIPLY_NUMBERS = "MultiplyNumbers";
      String DIVIDE_NUMBERS = "DivideNumbers";
   }
   
   // Declare your slot types
   private interface Slot
   {
      String TITLE = "Title";
      String NUMBER_A = "NumberA";
      String NUMBER_B = "NumberB";
   }

   // Declare your message keys here
   private interface Msg
   {
      String WELCOME = "welcome";
      String WELCOME_REPROMPT = "welcome.reprompt";
      String GOODBYE = "goodbye";
      String INTENT_UNKNOWN = "intent.unknown";
      
      String DEFAULT_TITLE = "default.title";
      
      String SLOT_TITLE_EMPTY = "slot.title.empty";
      String SLOT_NUMBER_A_EMPTY = "slot.numberA.empty";
      String SLOT_NUMBER_A_FORMAT = "slot.numberA.format";
      String SLOT_NUMBER_B_EMPTY = "slot.numberB.empty";
      String SLOT_NUMBER_B_FORMAT = "slot.numberB.format";

      String INTENT_SET_TITLE_RESULT = "intent.setTitle.result";
      String INTENT_ADD_NUMBERS_RESULT = "intent.addNumbers.result";
      String INTENT_SUBTRACT_NUMBERS_RESULT = "intent.subtractNumbers.result";
      String INTENT_MULTIPLY_NUMBERS_RESULT = "intent.multiplyNumbers.result";
      String INTENT_DIVIDE_NUMBERS_RESULT = "intent.divideNumbers.result";
      String INTENT_DIVIDE_NUMBERS_ZERO = "intent.divideNumbers.zero";
   }
   
   private AmazonDynamoDB db;

   protected SpeechletResponse onLaunchInternal(LaunchRequest request) throws SpeechletResponseThrowable
   {
      final SkillData skillData = loadData();
      final String title = getTitleFromSkillData(skillData);

      return newSpeechletAskResponseWithReprompt(Msg.WELCOME, Msg.WELCOME_REPROMPT, title);
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

      final SkillData skillData = loadData();

      ////////////////////////
      // Intent 'Set title' //
      ////////////////////////
      if(IntentName.SET_TITLE.equals(intentName))
      {
         final String title = getSlot(intent, Slot.TITLE, Msg.SLOT_TITLE_EMPTY, Msg.WELCOME_REPROMPT);

         skillData.getDataBean().setTitle(title);
         saveData(skillData);
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_SET_TITLE_RESULT, Msg.WELCOME_REPROMPT, title);
      }
      
      final int numberA = getSlotInt(intent, Slot.NUMBER_A, Msg.SLOT_NUMBER_A_EMPTY, Msg.SLOT_NUMBER_A_FORMAT, Msg.WELCOME_REPROMPT);
      final int numberB = getSlotInt(intent, Slot.NUMBER_B, Msg.SLOT_NUMBER_B_EMPTY, Msg.SLOT_NUMBER_B_FORMAT, Msg.WELCOME_REPROMPT);

      final String title = getTitleFromSkillData(skillData);
      
      /////////////////////////
      // Intent 'AddNumbers' //
      /////////////////////////
      if(IntentName.ADD_NUMBERS.equals(intentName))
      {
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_ADD_NUMBERS_RESULT, Msg.WELCOME_REPROMPT, title, numberA, numberB, numberA + numberB);
      }
      
      //////////////////////////////
      // Intent 'SubtractNumbers' //
      //////////////////////////////
      if(IntentName.SUBTRACT_NUMBERS.equals(intentName))
      {
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_SUBTRACT_NUMBERS_RESULT, Msg.WELCOME_REPROMPT, title, numberA, numberB, numberA - numberB);
      }
      
      //////////////////////////////
      // Intent 'MultiplyNumbers' //
      //////////////////////////////
      if(IntentName.MULTIPLY_NUMBERS.equals(intentName))
      {
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_MULTIPLY_NUMBERS_RESULT, Msg.WELCOME_REPROMPT, title, numberA, numberB, numberA * numberB);
      }
      
      ////////////////////////////
      // Intent 'DivideNumbers' //
      ////////////////////////////
      if(IntentName.DIVIDE_NUMBERS.equals(intentName))
      {
         if(numberB == 0)
         {
            return newSpeechletAskResponseWithReprompt(Msg.INTENT_DIVIDE_NUMBERS_ZERO, Msg.WELCOME_REPROMPT, title);
         }
         
         final int div = numberA / numberB;
         final int rest = numberA % numberB;
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_DIVIDE_NUMBERS_RESULT, Msg.WELCOME_REPROMPT, title, numberA, numberB, div, rest);
      }
      
      return newSpeechletAskResponseWithReprompt(Msg.INTENT_UNKNOWN, Msg.WELCOME_REPROMPT);
   }

   private AmazonDynamoDB getDb()
   {
      if (db == null)
      {
         db = AmazonDynamoDBClientBuilder.defaultClient();
      }

      return db;
   }

   private SkillData loadData()
   {
      final String userId = getSession().getUser().getUserId();
      SkillData skillData = SkillDataDao.load(getDb(), userId);
      
      if(skillData == null)
      {
         skillData = new SkillData();
         skillData.setCustomerId(userId);
         
         saveData(skillData);
      }
      
      return skillData;
   }

   private void saveData(SkillData data)
   {
      SkillDataDao.save(getDb(), data);
   }

   private String getTitleFromSkillData(final SkillData skillData) throws SpeechletResponseThrowable
   {
      // Get the title that the user assigned or the default title, of none was assigned yet
      return ofNullable(skillData.getDataBean().getTitle()).orElse(getMessage(Msg.DEFAULT_TITLE));
   }
}