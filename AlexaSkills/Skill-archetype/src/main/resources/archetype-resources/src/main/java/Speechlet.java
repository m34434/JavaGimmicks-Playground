package ${package};

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import ${package}.model.SkillData;
import ${package}.model.SkillDataDao;
import net.sf.javagimmicks.ask.base.AbstractSpeechlet;
import net.sf.javagimmicks.ask.base.SpeechletResponseThrowable;

public class Speechlet extends AbstractSpeechlet
{
   // Declare your intent names here
   private interface IntentName
   {
      String LIST_ITEMS = "ListItems";
      String ADD_ITEM = "AddItem";
      String REMOVE_ITEM = "RemoveItem";
   }
   
   // Declare your slot types
   private interface Slot
   {
      String ITEM = "Item";
   }

   // Declare your message keys here
   private interface Msg
   {
      String WELCOME = "welcome";
      String WELCOME_REPROMPT = "welcome.reprompt";
      String GOODBYE = "goodbye";
      String INTENT_UNKNOWN = "intent.unknown";
      
      String SLOT_ITEM_EMPTY = "slot.item.empty";

      String INTENT_LIST_ITEMS_RESULT = "intent.listItems.result";
      String INTENT_ADD_ITEM_RESULT = "intent.addItem.result";
      String INTENT_REMOVE_ITEM_RESULT = "intent.removeItem.result";
   }
   
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

      final SkillData skillData = loadData();

      ////////////////////////
      // Intent 'ListItems' //
      ////////////////////////
      if(IntentName.LIST_ITEMS.equals(intentName))
      {
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_LIST_ITEMS_RESULT, Msg.WELCOME_REPROMPT, String.join(", ", skillData.getDataBean().getItems()));
      }
      
      //////////////////////
      // Intent 'AddItem' //
      //////////////////////
      if(IntentName.ADD_ITEM.equals(intentName))
      {
         final String item = getSlot(intent, Slot.ITEM, Msg.SLOT_ITEM_EMPTY);
         
         skillData.getDataBean().getItems().add(item);
         saveData(skillData);
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_ADD_ITEM_RESULT, Msg.WELCOME_REPROMPT, item);
      }
      
      /////////////////////////
      // Intent 'RemoteItem' //
      /////////////////////////
      if(IntentName.REMOVE_ITEM.equals(intentName))
      {
         final String item = getSlot(intent, Slot.ITEM, Msg.SLOT_ITEM_EMPTY);
         
         skillData.getDataBean().getItems().remove(item);
         saveData(skillData);
         
         return newSpeechletAskResponseWithReprompt(Msg.INTENT_REMOVE_ITEM_RESULT, Msg.WELCOME_REPROMPT, item);
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
      return SkillDataDao.load(getDb(), getSession().getUser().getUserId());
   }

   private void saveData(SkillData data)
   {
      SkillDataDao.save(getDb(), data);
   }
}