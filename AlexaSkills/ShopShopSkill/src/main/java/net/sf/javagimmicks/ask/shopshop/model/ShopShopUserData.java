package net.sf.javagimmicks.ask.shopshop.model;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;

/**
 * Model representing an item of the ScoreKeeperUserData table in DynamoDB for
 * the ScoreKeeper skill.
 */
@DynamoDBTable(tableName = "ASKShopShopUserData")
public class ShopShopUserData
{
   private String customerId;

   private String dropboxAccessToken;
   
   private State state;

   private String listName;

   @DynamoDBHashKey(attributeName = "CustomerId")
   public String getCustomerId()
   {
      return customerId;
   }

   public void setCustomerId(String customerId)
   {
      this.customerId = customerId;
   }
   
   @DynamoDBAttribute(attributeName = "DropboxAccessToken")
   public String getDropboxAccessToken()
   {
      return dropboxAccessToken;
   }

   public void setDropboxAccessToken(String dropboxAccessToken)
   {
      this.dropboxAccessToken = dropboxAccessToken;
   }

   @DynamoDBAttribute(attributeName = "State")
   @DynamoDBTypeConvertedEnum
   public State getState()
   {
      return state;
   }

   public void setState(State state)
   {
      this.state = state;
   }
   

   @DynamoDBAttribute(attributeName = "ListName")
   public String getListName()
   {
      return listName;
   }

   public void setListName(String listName)
   {
      this.listName = listName;
   }
   
   public static ShopShopUserData fromSessionAttribute(Object value)
   {
      if(value == null)
      {
         return null;
      }

      // Subsequent calls within this Lambda instance
      if(value instanceof ShopShopUserData)
      {
         return (ShopShopUserData) value;
      }
      
      // Subsequent call to onIntent() after a previous onLaunch() request
      if(value instanceof Map)
      {
         @SuppressWarnings("unchecked")
         final Map<String, Object> userDataMap = (Map<String, Object>)value;
      
         // TODO: try to make this better using commons-beanutils or stuff like this (maybe there's sth. from Amazon API)
        final ShopShopUserData userData = new ShopShopUserData();
         userData.setCustomerId((String) userDataMap.get("customerId"));
         userData.setDropboxAccessToken((String) userDataMap.get("dropboxAccessToken"));
         userData.setListName((String) userDataMap.get("listName"));
         
         return userData;
      }
      
      throw new IllegalArgumentException("Could not parse from session attribute!");
   }
}
