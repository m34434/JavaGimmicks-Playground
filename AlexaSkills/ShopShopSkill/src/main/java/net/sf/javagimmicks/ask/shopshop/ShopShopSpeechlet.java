package net.sf.javagimmicks.ask.shopshop;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.dropbox.core.DbxException;

import net.sf.javagimmicks.ask.shopshop.model.ShopShopDao;
import net.sf.javagimmicks.ask.shopshop.model.ShopShopUserData;
import net.sf.javagimmicks.shopshop.ListItem;
import net.sf.javagimmicks.shopshop.ShopShopClient;
import net.sf.javagimmicks.shopshop.ShopShopClientException;
import net.sf.javagimmicks.shopshop.util.ShopShopHelper;

public class ShopShopSpeechlet implements Speechlet
{
   private static final Logger log = LoggerFactory.getLogger(ShopShopSpeechlet.class);
   
   private static final String MSG_FATAL_ERROR = "fatalError";

   private static final String BUNDLE_NAME = "messages";

   private ResourceBundle bundle;
   private AmountTypes amountTypes;

   private AmazonDynamoDB db;

   private Session session;

   @Override
   public void onSessionStarted(final SessionStartedRequest request, final Session session)
         throws SpeechletException
   {
      onRequest(request, session);
   
      log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
            session.getSessionId());
   
   }

   @Override
   public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
         throws SpeechletException
   {
      onRequest(request, session);
   
      log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
            session.getSessionId());
   
      try
      {
         return onLaunchInternal(request);
      }
      catch (SpeechletResponseThrowable e)
      {
         return e.getResponse();
      }
   }
   
   @Override
   public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException
   {
      onRequest(request, session);

      log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
      
      try
      {
         return onIntentInternal(request);
      }
      catch (SpeechletResponseThrowable e)
      {
         return e.getResponse();
      }
   }

   @Override
   public void onSessionEnded(final SessionEndedRequest request, final Session session)
         throws SpeechletException
   {
      onRequest(request, session);

      log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
            session.getSessionId());

      // any cleanup logic goes here
   }
   
   protected SpeechletResponse onLaunchInternal(LaunchRequest request) throws SpeechletResponseThrowable
   {
      final ShopShopUserData userData = getUserData();
      if (userData.getDropboxAccessToken() == null)
      {
         return newSpeechletTellResponse("dropbox.notoken");
      }
   
      return newSpeechletAskResponseWithReprompt("welcome", "welcome.reprompt");
   }

   protected SpeechletResponse onIntentInternal(IntentRequest request) throws SpeechletResponseThrowable, SpeechletException
   {
      onRequest(request, session);
   
      log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
            session.getSessionId());
   
      final Intent intent = request.getIntent();
      final String intentName = intent.getName();
      
      //////////
      // Canceling and stopping
      /////////      
      if("AMAZON.StopIntent".equals(intentName) || "AMAZON.CancelIntent".equals(intentName))
      {
         return newSpeechletTellResponse("goodbye");
      }
      
      final IntentType intentType = IntentType.valueOf(intentName);
   
      final ShopShopUserData userData = getUserData();
   
      //////////
      // Get the current list name
      /////////      
      if(intentType == IntentType.GetCurrentListName)
      {
         final String listName = getSelectedListName(userData);
         return newSpeechletAskResponseWithReprompt("getListName.result", "welcome.reprompt", listName);
      }

      // From here on, we will need also Dropbox access
      if (userData.getDropboxAccessToken() == null)
      {
         return newSpeechletTellResponse("dropbox.notoken");
      }
      
      //////////
      // Get all available list names
      /////////      
      if(intentType == IntentType.GetAllListNames)
      {
         final List<String> avaiblableLists = getAvailableListNames(userData);
         
         return newSpeechletAskResponseWithReprompt("getAll", "welcome.reprompt", String.join(", ", avaiblableLists));
      }
   
      //////////
      // Switch the current list
      /////////      
      if (intentType == IntentType.SwitchList)
      {
         final String listName = intent.getSlot("ListName").getValue();
         if(listName == null)
         {
            return newSpeechletAskResponseWithReprompt("switch.unknownList.slot", "welcome.reprompt");
         }
         
         final List<String> avaiblableLists = getAvailableListNames(userData);
         
         final String realListName = findList(avaiblableLists, listName);
         if(realListName == null)
         {
            return newSpeechletAskResponse("switch.unknownList", listName, String.join(", ", avaiblableLists));
         }
         
         userData.setListName(realListName);
         ShopShopDao.save(getDb(), userData);
   
         return newSpeechletAskResponseWithReprompt("switch.done", "welcome.reprompt", listName);
      }
      
      //////////
      // Add item to list
      /////////      
      if(intentType == IntentType.AddItem)
      {
         final String listName = getSelectedListName(userData);

         final String itemName = getItemName(intent);
         final ListItem listItem = new ListItem(itemName);

         return addItem(userData, listName, listItem);
      }
   
      //////////
      // Add item with amount to list
      /////////      
      if(intentType == IntentType.AddAmountItem)
      {
         final String listName = getSelectedListName(userData);

         final String amount = getAmount(intent);
         final String itemName = getItemName(intent);
         
         final ListItem listItem = new ListItem(amount, itemName);

         return addItem(userData, listName, listItem);
      }
   
      //////////
      // Add item with amount and type to list
      /////////      
      if(intentType == IntentType.AddAmountItemTyped)
      {
         final String listName = getSelectedListName(userData);

         final String amountTypeSpoken = getAmountType(intent);
         final String amount = getAmount(intent);
         final String itemName = getItemName(intent);

         String amountFullText = amount;
         if(amountTypeSpoken != null)
         {
            final String amountAbbreviation = getAmountTypes().getAbbreviation(amountTypeSpoken.toLowerCase());
            
            if(amountAbbreviation != null)
            {
               amountFullText = amount + " " + amountAbbreviation;
            }
         }
         
         final ListItem listItem = new ListItem(amountFullText, itemName);

         return addItem(userData, listName, listItem);
      }
   
      return newSpeechletAskResponseWithReprompt("unknownIntent", "welcome.reprompt");
   }

   protected String getMessage(String key) throws SpeechletResponseThrowable
   {
       try
       {
           return bundle.getString(key);
       }
       catch(MissingResourceException e)
       {
           throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
       }
   }
   
   protected SpeechletResponse newSpeechletTellResponse(String messageKey, Object... args) throws SpeechletResponseThrowable
   {
      final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(String.format(getMessage(messageKey), args));
   
      final SpeechletResponse result = new SpeechletResponse();
      result.setOutputSpeech(speech);
   
      return SpeechletResponse.newTellResponse(speech);
   }

   protected SpeechletResponse newSpeechletAskResponseWithReprompt(String messageKey, String repromptMessageKey,
         Object... args) throws SpeechletResponseThrowable
   {
      final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(String.format(getMessage(messageKey), args));
   
      final PlainTextOutputSpeech speechReprompt = new PlainTextOutputSpeech();
      speechReprompt.setText(String.format(getMessage(repromptMessageKey), args));
   
      final Reprompt reprompt = new Reprompt();
      reprompt.setOutputSpeech(speechReprompt);
   
      final SpeechletResponse result = new SpeechletResponse();
      result.setOutputSpeech(speech);
   
      return SpeechletResponse.newAskResponse(speech, reprompt);
   }

   protected SpeechletResponse newSpeechletAskResponse(String messageKey, Object... args) throws SpeechletResponseThrowable
   {
      return newSpeechletAskResponseWithReprompt(messageKey, messageKey, args);
   }

   private void onRequest(SpeechletRequest request, Session session)
   {
      this.session = session;

      final String language = request.getLocale().getLanguage();
      bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.forLanguageTag(language));
   }

   private ShopShopUserData getUserData()
   {
      ShopShopUserData userData = ShopShopDao.load(getDb(), session.getUser().getUserId());

      if (userData == null)
      {
         userData = new ShopShopUserData();
         userData.setCustomerId(session.getUser().getUserId());

         ShopShopDao.save(getDb(), userData);
      }

      return userData;
   }

   private AmazonDynamoDB getDb()
   {
      if (db == null)
      {
//         db = new AmazonDynamoDBClient();
//         db.setRegion(RegionUtils.getRegion("eu-west-1"));
         db = AmazonDynamoDBClientBuilder.defaultClient();
      }

      return db;
   }
   
   private AmountTypes getAmountTypes() throws SpeechletResponseThrowable
   {
      final String language = bundle.getLocale().getLanguage();
      
      if(amountTypes == null || !language.equals(amountTypes.getLanguage()))
      {
         try
         {
            amountTypes = new AmountTypes(language);
         }
         catch (IOException e)
         {
            throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
         }
      }
      
      return amountTypes;
   }

   private List<String> getAvailableListNames(ShopShopUserData userData) throws SpeechletResponseThrowable
   {
      try
      {
         final List<String> listNames = ShopShopHelper.getShoppingListNames(userData.getDropboxAccessToken());
         if(listNames == null || listNames.isEmpty())
         {
            throw new SpeechletResponseThrowable(newSpeechletTellResponse("listNames.noList"));
         }
         
         return listNames;
      }
      catch (DbxException e)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("dropbox.connect.error", "welcome.reprompt"));
      }
   }

   private String getSelectedListName(ShopShopUserData userData) throws SpeechletResponseThrowable
   {
      final String listName = userData.getListName();
      if(listName == null || listName.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponse("getListName.noList"));
      }
      
      return listName;
   }

   private String getAmount(Intent intent) throws SpeechletResponseThrowable
   {
      final String amount = intent.getSlot("Amount").getValue();
      if(amount == null || amount.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.noamount", "welcome.reprompt"));
      }
      
      return amount;
   }

   private String getAmountType(Intent intent) throws SpeechletResponseThrowable
   {
      String amountType = intent.getSlot("AmountType").getValue();
      if(amountType == null || amountType.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.noamounttype", "welcome.reprompt"));
      }
      
      return amountType;
   }

   private String getItemName(Intent intent) throws SpeechletResponseThrowable
   {
      final String itemName = intent.getSlot("ItemName").getValue();
      if(itemName == null || itemName.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.noitem", "welcome.reprompt"));
      }
      else if (itemName.equals("schokolade"))
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.bad", "welcome.reprompt"));
      }
      
      return itemName;
   }
   
   private String getListItemSpokenText(ListItem listItem) throws SpeechletResponseThrowable
   {
      final String itemName = listItem.getName();
      final String amountFullText = listItem.getCount();
      
      if(amountFullText == null || amountFullText.length() == 0)
      {
         return itemName;
      }
      
      final String[] amountParts = amountFullText.split(" ");
      if(amountParts.length == 1)
      {
         return amountParts[0] + " " + itemName;
      }
      else
      {
         return amountParts[0] + " " + getAmountTypes().getSpokenType(amountParts[1]) + " " + itemName;
      }
   }

   private SpeechletResponse addItem(final ShopShopUserData userData, final String listName, final ListItem listItem) throws SpeechletResponseThrowable
   {
      try
      {
         final ShopShopClient shopShopClient = new ShopShopClient(userData.getDropboxAccessToken(), listName);
         shopShopClient.addItem(listItem);
         shopShopClient.save();
      }
      catch (ShopShopClientException e)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("dropbox.connect.error", "welcome.reprompt"));
      }
      
      return newSpeechletAskResponseWithReprompt("addItem.ok", "welcome.reprompt", getListItemSpokenText(listItem));
   }

   private static String findList(Collection<String> listNames, String listName)
   {
      for(String currentListName : listNames)
      {
         if(currentListName.equalsIgnoreCase(listName))
         {
            return currentListName;
         }
      }
      
      return null;
   }
}
