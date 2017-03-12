package net.sf.javagimmicks.ask.shopshop.model;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class ShopShopDao
{
   public static Optional<ShopShopUserData> load(final AmazonDynamoDB db, String customerId)
   {
      final ShopShopUserData refItem = new ShopShopUserData();
      refItem.setCustomerId(customerId);
      
      return Optional.ofNullable(createDynamoDBMapper(db).load(refItem));
   }

   public static void save(final AmazonDynamoDB db, final ShopShopUserData item)
   {
      createDynamoDBMapper(db).save(item);
   }

   private static DynamoDBMapper createDynamoDBMapper(AmazonDynamoDB db)
   {
      return new DynamoDBMapper(db);
   }
}
