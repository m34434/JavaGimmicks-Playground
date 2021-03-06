package net.sf.javagimmicks.ask.mathbuddy.model;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class SkillDataDao
{
   private SkillDataDao() {}

   public static Optional<SkillData> load(final AmazonDynamoDB db, final String customerId)
   {
      final SkillData skillData = new SkillData();
      skillData.setCustomerId(customerId);
      
      return Optional.ofNullable(createDynamoDBMapper(db).load(skillData));
   }

   public static void save(final AmazonDynamoDB db, final SkillData skillData)
   {
      createDynamoDBMapper(db).save(skillData);
   }

   private static DynamoDBMapper createDynamoDBMapper(AmazonDynamoDB db)
   {
      return new DynamoDBMapper(db);
   }
}
