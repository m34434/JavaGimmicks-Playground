package net.sf.javagimmicks.ask.wastecalendar.model;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;

/**
 * Model representing an item of the ScoreKeeperUserData table in DynamoDB for
 * the ScoreKeeper skill.
 */
@DynamoDBTable(tableName = "ASKWasteCalendarData")
public class CalendarData
{
   private String customerId;

   private Data data;

   @DynamoDBHashKey(attributeName = "CustomerId")
   public String getCustomerId()
   {
      return customerId;
   }

   public void setCustomerId(String customerId)
   {
      this.customerId = customerId;
   }
   
   @DynamoDBAttribute(attributeName = "Data")
   @DynamoDBTypeConvertedJson
   public Data getData()
   {
      if(data == null)
      {
         data = new Data();
      }
      
      return data;
   }

   public void setData(Data data)
   {
      this.data = data;
   }
   
   public static final class Data
   {
      private SortedMap<String, List<String>> entries;

      public SortedMap<String, List<String>> getEntries()
      {
         if(entries == null)
         {
            entries = new TreeMap<>();
         }
         
         return entries;
      }

      public void setEntries(Map<String, List<String>> entries)
      {
         getEntries().clear();
         getEntries().putAll(entries);
      }
   }
}
