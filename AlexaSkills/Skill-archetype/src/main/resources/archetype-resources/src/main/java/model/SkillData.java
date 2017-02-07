package ${package}.model;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;

@DynamoDBTable(tableName = "ASK_${artifactId}")
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
      private List<String> items;
      private String foo;

      public List<String> getItems()
      {
         if(items == null)
         {
            items = new ArrayList<>();
         }
         
         return items;
      }

      public void setItems(List<String> items)
      {
         this.items = items;
      }

      public String getFoo()
      {
         return foo;
      }

      public void setFoo(String foo)
      {
         this.foo = foo;
      }
   }
}
