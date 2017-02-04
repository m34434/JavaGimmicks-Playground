package net.sf.javagimmicks.ask.wastecalendar;

import java.util.Arrays;
import java.util.HashSet;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class Handler extends SpeechletRequestStreamHandler
{
   public Handler()
   {
      super(new WasteCalendarSpeechlet(), new HashSet<>(Arrays.asList("amzn1.ask.skill.39700240-5817-4471-b3eb-67a6f2122c92")));
   }
}