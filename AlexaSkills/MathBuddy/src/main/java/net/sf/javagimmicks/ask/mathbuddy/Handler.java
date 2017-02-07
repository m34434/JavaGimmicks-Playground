package net.sf.javagimmicks.ask.mathbuddy;

import java.util.Arrays;
import java.util.HashSet;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class Handler extends SpeechletRequestStreamHandler
{
   public Handler()
   {
      super(new Speechlet(), new HashSet<>(Arrays.asList("amzn1.ask.skill.b9e8eeca-9c8b-4135-965b-675e8f817a1d")));
   }
}