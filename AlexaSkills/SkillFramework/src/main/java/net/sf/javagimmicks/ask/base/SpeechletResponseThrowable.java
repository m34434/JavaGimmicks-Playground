package net.sf.javagimmicks.ask.base;

import com.amazon.speech.speechlet.SpeechletResponse;

public class SpeechletResponseThrowable extends Throwable
{
   private static final long serialVersionUID = 1L;
   
   private final SpeechletResponse response;

   public SpeechletResponseThrowable(SpeechletResponse response)
   {
      this.response = response;
   }

   public SpeechletResponse getResponse()
   {
      return this.response;
   }
}
