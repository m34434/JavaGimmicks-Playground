package net.sf.javagimmicks.ask.shopshop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.dropbox.core.DbxException;

import net.sf.javagimmicks.ask.base.AbstractSpeechlet;
import net.sf.javagimmicks.ask.base.SpeechletResponseThrowable;
import net.sf.javagimmicks.ask.shopshop.model.ShopShopDao;
import net.sf.javagimmicks.ask.shopshop.model.ShopShopUserData;
import net.sf.javagimmicks.shopshop.ListItem;
import net.sf.javagimmicks.shopshop.ShopShopClient;
import net.sf.javagimmicks.shopshop.ShopShopClientException;
import net.sf.javagimmicks.shopshop.util.ShopShopHelper;

public class ShopShopSpeechlet extends AbstractSpeechlet
{
   private static final String MSG_WELCOME = "welcome";
   private static final String MSG_WELCOME_REPROMPT = "welcome.reprompt";
   private static final String MSG_GET_ALL = "getAll";
   private static final String MSG_GET_LIST_NAME_RESULT = "getListName.result";
   private static final String MSG_SWITCH_UNKNOWN_LIST = "switch.unknownList";
   private static final String MSG_SWITCH_UNKNOWN_LIST_SLOT = "switch.unknownList.slot";
   private static final String MSG_SWITCH_DONE = "switch.done";
   private static final String MSG_GOODBYE = "goodbye";
   private static final String MSG_DROPBOX_NOTOKEN = "dropbox.notoken";

   private static final String ATTR_USER_DATA = "___userData___";

   private AmazonDynamoDB db;

   private AmountTypes amountTypes;

   @Override
   public void onSessionEnded(final SessionEndedRequest request, final Session session)
         throws SpeechletException
   {
      log.debug("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
            session.getSessionId());

      // any cleanup logic goes here
   }
   
   protected SpeechletResponse onLaunchInternal(LaunchRequest request) throws SpeechletResponseThrowable
   {
      final ShopShopUserData userData = getUserData();
      if (userData.getDropboxAccessToken() == null)
      {
         return newSpeechletTellResponse(MSG_DROPBOX_NOTOKEN);
      }
   
      return newSpeechletAskResponseWithReprompt(MSG_WELCOME, MSG_WELCOME_REPROMPT);
   }

   protected SpeechletResponse onIntentInternal(IntentRequest request) throws SpeechletResponseThrowable, SpeechletException
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
      
      final IntentType intentType = IntentType.valueOf(intentName);
   
      final ShopShopUserData userData = getUserData();
   
      //////////
      // Get the current list name
      /////////      
      if(intentType == IntentType.GetCurrentListName)
      {
         final String listName = getSelectedListName(userData);
         return newSpeechletAskResponseWithReprompt(MSG_GET_LIST_NAME_RESULT, MSG_WELCOME_REPROMPT, listName);
      }

      // From here on, we will need also Dropbox access
      if (userData.getDropboxAccessToken() == null)
      {
         return newSpeechletTellResponse(MSG_DROPBOX_NOTOKEN);
      }
      
      //////////
      // Get all available list names
      /////////      
      if(intentType == IntentType.GetAllListNames)
      {
         final List<String> avaiblableLists = getAvailableListNames(userData);
         
         return newSpeechletAskResponseWithReprompt(MSG_GET_ALL, MSG_WELCOME_REPROMPT, String.join(", ", avaiblableLists));
      }
   
      //////////
      // Switch the current list
      /////////      
      if (intentType == IntentType.SwitchList)
      {
         final String listName = intent.getSlot("ListName").getValue();
         if(listName == null)
         {
            return newSpeechletAskResponseWithReprompt(MSG_SWITCH_UNKNOWN_LIST_SLOT, MSG_WELCOME_REPROMPT);
         }
         
         final List<String> avaiblableLists = getAvailableListNames(userData);
         
         final String realListName = findList(avaiblableLists, listName);
         if(realListName == null)
         {
            return newSpeechletAskResponse(MSG_SWITCH_UNKNOWN_LIST, listName, String.join(", ", avaiblableLists));
         }
         
         userData.setListName(realListName);
         ShopShopDao.save(getDb(), userData);
   
         return newSpeechletAskResponseWithReprompt(MSG_SWITCH_DONE, MSG_WELCOME_REPROMPT, listName);
      }
      
      //////////
      // Read the current list
      /////////      
      if (intentType == IntentType.ReadList)
      {
         final String listName = getSelectedListName(userData);
         
         final List<ListItem> listItems = getItems(userData, listName);
         final List<String> listItemStrings = new ArrayList<>(listItems.size());
         
         for(ListItem i : listItems)
         {
            listItemStrings.add(getListItemSpokenText(i));
         }
         
         return newSpeechletAskResponseWithReprompt("readList", MSG_WELCOME_REPROMPT, listName, String.join(", ", listItemStrings));
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
   
      //////////
      // Remove item from list
      /////////      
      if(intentType == IntentType.RemoveItem)
      {
         final String listName = getSelectedListName(userData);

         final String itemName = getItemName(intent);

         return removeItem(userData, listName, itemName);
      }

      return newSpeechletAskResponseWithReprompt("unknownIntent", MSG_WELCOME_REPROMPT);
   }

   private ShopShopUserData getUserData()
   {
      final Object userDataRaw = getSession().getAttribute(ATTR_USER_DATA);
      
      // Subsequent calls within this Lambda instance
      if(userDataRaw instanceof ShopShopUserData)
      {
         return (ShopShopUserData) userDataRaw;
      }
      
      // Subsequent call to onIntent() after a previous onLaunch() request
      if(userDataRaw instanceof Map)
      {
         @SuppressWarnings("unchecked")
         final Map<String, Object> userDataMap = (Map<String, Object>)userDataRaw;
      
         // TODO: try to make this better using commons-beanutils or stuff like this (maybe there's sth. from Amazon API)
         final ShopShopUserData userData = new ShopShopUserData();
         userData.setCustomerId((String) userDataMap.get("customerId"));
         userData.setDropboxAccessToken((String) userDataMap.get("dropboxAccessToken"));
         userData.setListName((String) userDataMap.get("listName"));
         
         getSession().setAttribute(ATTR_USER_DATA, userData);
         
         return userData;
      }
      
      log.info("Loading user data from DynamoDB");
      final String userId = getSession().getUser().getUserId();
      ShopShopUserData userData = ShopShopDao.load(getDb(), userId);

      if (userData == null)
      {
         userData = new ShopShopUserData();
         userData.setCustomerId(userId);

         ShopShopDao.save(getDb(), userData);
      }
      
      getSession().setAttribute(ATTR_USER_DATA, userData);

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
      final Locale locale = getLocale();
      
      if(amountTypes == null || !locale.equals(amountTypes.getLocale()))
      {
         try
         {
            amountTypes = new AmountTypes(locale);
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
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("dropbox.connect.error", MSG_WELCOME_REPROMPT));
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
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.noamount", MSG_WELCOME_REPROMPT));
      }
      
      return amount;
   }

   private String getAmountType(Intent intent) throws SpeechletResponseThrowable
   {
      String amountType = intent.getSlot("AmountType").getValue();
      if(amountType == null || amountType.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.noamounttype", MSG_WELCOME_REPROMPT));
      }
      
      return amountType;
   }

   private String getItemName(Intent intent) throws SpeechletResponseThrowable
   {
      final String itemName = intent.getSlot("ItemName").getValue();
      if(itemName == null || itemName.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("addItem.noitem", MSG_WELCOME_REPROMPT));
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
         return newSpeechletAskResponseWithReprompt("dropbox.connect.error", MSG_WELCOME_REPROMPT);
      }
      
      return newSpeechletAskResponseWithReprompt("addItem.ok", MSG_WELCOME_REPROMPT, getListItemSpokenText(listItem));
   }
   
   private SpeechletResponse removeItem(final ShopShopUserData userData, final String listName, final String itemName) throws SpeechletResponseThrowable
   {
      if(itemName == null)
      {
         return newSpeechletAskResponseWithReprompt("removeItem.noitem", MSG_WELCOME_REPROMPT);
      }
      
      try
      {
         final ShopShopClient shopShopClient = new ShopShopClient(userData.getDropboxAccessToken(), listName);
         for(ListIterator<ListItem> i = shopShopClient.getItems().listIterator(); i.hasNext();)
         {
            final ListItem listItem = i.next();
            
            if(itemName.equalsIgnoreCase(listItem.getName()))
            {
               shopShopClient.removeItem(i.previousIndex());
               shopShopClient.save();
               
               return newSpeechletAskResponseWithReprompt("removeItem.ok", MSG_WELCOME_REPROMPT, itemName);
            }
         }
      }
      catch (ShopShopClientException e)
      {
         return newSpeechletAskResponseWithReprompt("dropbox.connect.error", MSG_WELCOME_REPROMPT);
      }
      
      return newSpeechletAskResponseWithReprompt("removeItem.notfound", MSG_WELCOME_REPROMPT, itemName);
   }
   
   private List<ListItem> getItems(final ShopShopUserData userData, final String listName) throws SpeechletResponseThrowable
   {
      try
      {
         final ShopShopClient shopShopClient = new ShopShopClient(userData.getDropboxAccessToken(), listName);
         return shopShopClient.getItems();
      }
      catch (ShopShopClientException e)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponseWithReprompt("dropbox.connect.error", MSG_WELCOME_REPROMPT));
      }
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
