package net.sf.javagimmicks.ask.mathbuddy.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;

@DynamoDBTable(tableName = "ASK_MathBuddy")
public class SkillData
{
   private String customerId;

   private DataBean data;

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
   public DataBean getDataBean()
   {
      if(data == null)
      {
         data = new DataBean();
      }
      
      return data;
   }

   public void setDataBean(DataBean data)
   {
      this.data = data;
   }
   
   public static final class DataBean
   {
      private String title;

      public String getTitle()
      {
         return title;
      }

      public void setTitle(String title)
      {
         this.title = title;
      }
   }
}
