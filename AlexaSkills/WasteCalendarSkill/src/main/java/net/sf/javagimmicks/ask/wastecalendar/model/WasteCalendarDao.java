package net.sf.javagimmicks.ask.wastecalendar.model;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class WasteCalendarDao
{
   public static Optional<CalendarData> load(final AmazonDynamoDB db, String customerId)
   {
      final CalendarData refItem = new CalendarData();
      refItem.setCustomerId(customerId);
      
      return Optional.ofNullable(createDynamoDBMapper(db).load(refItem));
   }

   public static void save(final AmazonDynamoDB db, final CalendarData item)
   {
      createDynamoDBMapper(db).save(item);
   }

   private static DynamoDBMapper createDynamoDBMapper(AmazonDynamoDB db)
   {
      return new DynamoDBMapper(db);
   }
}
