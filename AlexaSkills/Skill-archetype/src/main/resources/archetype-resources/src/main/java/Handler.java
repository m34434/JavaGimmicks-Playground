package ${package};

import java.util.Arrays;
import java.util.HashSet;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class Handler extends SpeechletRequestStreamHandler
{
   public Handler()
   {
      super(new Speechlet(), new HashSet<>(Arrays.asList("<skill application id>")));
   }
}