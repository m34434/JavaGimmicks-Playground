package net.sf.javagimmicks.ask.shopshop.model;

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
}
