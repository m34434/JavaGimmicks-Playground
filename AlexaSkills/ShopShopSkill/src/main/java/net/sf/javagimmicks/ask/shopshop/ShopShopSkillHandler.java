package net.sf.javagimmicks.ask.shopshop;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public final class ShopShopSkillHandler extends SpeechletRequestStreamHandler
{
   private static final Set<String> supportedApplicationIds;

   static
   {
      /*
       * This Id can be found on https://developer.amazon.com/edw/home.html#/
       * "Edit" the relevant Alexa Skill and put the relevant Application Ids in
       * this Set.
       */
      supportedApplicationIds = new HashSet<String>();
      supportedApplicationIds.add("amzn1.ask.skill.d41c0b3f-7486-4d95-81d2-96f207302ff8");
   }

   public ShopShopSkillHandler()
   {
      super(new ShopShopSpeechlet(), supportedApplicationIds);
   }
}
