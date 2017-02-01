package net.sf.javagimmicks.ask.shopshop;

import java.util.Arrays;
import java.util.HashSet;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class Handler extends SpeechletRequestStreamHandler
{
   public Handler()
   {
      super(new ShopShopSpeechlet(), new HashSet<>(Arrays.asList("amzn1.ask.skill.d41c0b3f-7486-4d95-81d2-96f207302ff8")));
   }
}