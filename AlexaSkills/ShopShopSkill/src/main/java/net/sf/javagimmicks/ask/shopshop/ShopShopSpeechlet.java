package net.sf.javagimmicks.ask.shopshop;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
import net.sf.javagimmicks.shopshop.util.ShopShopHelper;

public class ShopShopSpeechlet implements Speechlet
{
   private static final Logger log = LoggerFactory.getLogger(ShopShopSpeechlet.class);

   private AmazonDynamoDB db;

   private ShopShopUserData userData;

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

      final ShopShopUserData userData = getUserData();
      if (userData.getDropboxAccessToken() == null)
      {
         return newSpeechletTellResponse("dropbox.notoken");
      }

      return newSpeechletAskResponseWithReprompt("welcome", "welcome.reprompt");
   }

   @Override
   public SpeechletResponse onIntent(IntentRequest request, Session session)
         throws SpeechletException
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

      //////////
      // Get the current list name
      /////////      
      if(intentType == IntentType.GetCurrentListName)
      {
         final String listName = userData.getListName();
         
         if(listName != null && listName.length() > 0)
         {
            return newSpeechletAskResponseWithReprompt("getListName.result", "welcome.reprompt", listName);
         }
         else
         {
            return newSpeechletAskResponse("getListName.noList");
         }
      }

      final ShopShopUserData userData = getUserData();
      if (userData.getDropboxAccessToken() == null)
      {
         return newSpeechletTellResponse("dropbox.notoken");
      }
      
      if (intentType == IntentType.SwitchList)
      {
         final String listName = intent.getSlot("ListName").getValue();
         if(listName == null)
         {
            return newSpeechletAskResponseWithReprompt("switch.unknownList.slot", "welcome.reprompt");
         }
         
         final List<String> avaiblableLists;
         try
         {
            avaiblableLists = ShopShopHelper.getShoppingListNames(userData.getDropboxAccessToken());
         }
         catch (DbxException e)
         {
            return newSpeechletAskResponseWithReprompt("dropbox.connect.error", "welcome.reprompt");
         }
         
         final String realListName = findList(avaiblableLists, listName);
         if(realListName == null)
         {
            return newSpeechletAskResponse("switch.unknownList", listName, String.join(", ", avaiblableLists));
         }
         
         userData.setListName(realListName);
         ShopShopDao.save(getDb(), userData);

         return newSpeechletAskResponseWithReprompt("switch.done", "welcome.reprompt", listName);
      }

      return newSpeechletAskResponseWithReprompt("unknownIntent", "welcome.reprompt");
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

   private void onRequest(SpeechletRequest request, Session session)
   {
      this.session = session;
      Locale.setDefault(Locale.forLanguageTag(request.getLocale().getLanguage()));
   }

   private ShopShopUserData getUserData()
   {
      if (userData == null)
      {
         userData = ShopShopDao.load(getDb(), session.getUser().getUserId());

         if (userData == null)
         {
            userData = new ShopShopUserData();
            userData.setCustomerId(session.getUser().getUserId());

            ShopShopDao.save(getDb(), userData);
         }
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

   protected static SpeechletResponse newSpeechletTellResponse(String messageKey, Object... args)
   {
      final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(String.format(Messages.getString(messageKey), args));

      final SpeechletResponse result = new SpeechletResponse();
      result.setOutputSpeech(speech);

      return SpeechletResponse.newTellResponse(speech);
   }

   protected static SpeechletResponse newSpeechletAskResponseWithReprompt(String messageKey, String repromptMessageKey,
         Object... args)
   {
      final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(String.format(Messages.getString(messageKey), args));

      final PlainTextOutputSpeech speechReprompt = new PlainTextOutputSpeech();
      speechReprompt.setText(String.format(Messages.getString(repromptMessageKey), args));

      final Reprompt reprompt = new Reprompt();
      reprompt.setOutputSpeech(speechReprompt);

      final SpeechletResponse result = new SpeechletResponse();
      result.setOutputSpeech(speech);

      return SpeechletResponse.newAskResponse(speech, reprompt);
   }

   protected static SpeechletResponse newSpeechletAskResponse(String messageKey, Object... args)
   {
      return newSpeechletAskResponseWithReprompt(messageKey, messageKey, args);
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
