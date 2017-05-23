package net.sf.javagimmicks.ask.kinesislogger;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.sf.javagimmicks.ask.kinesislogger.model.LogEvent;

public class SkillKinesisLogger
{
   protected final Logger log = LoggerFactory.getLogger(getClass());

   private final AmazonKinesisAsync kinesis;
   private final String streamName;
   
   private final ObjectWriter jsonWriter;

   public SkillKinesisLogger(AmazonKinesisAsync kinesis, String streamName)
   {
      Objects.requireNonNull(kinesis, "Kinesis client may not be null!");
      Objects.requireNonNull(streamName, "Stream name may not be null!");
      
      this.kinesis = kinesis;
      this.streamName = streamName;
      
      final ObjectMapper json = new ObjectMapper();
      configureJackson(json);
      this.jsonWriter = json.writer();
   }
   
   public SkillKinesisLogger(AmazonKinesisAsyncClientBuilder kinesisBuilder, String streamName)
   {
      this(Objects.requireNonNull(kinesisBuilder, "Kinesis client builder may not be null!").build(), streamName);
   }
   
   public SkillKinesisLogger(String streamName)
   {
      this(AmazonKinesisAsyncClientBuilder.defaultClient(), streamName);
   }
   
   public final void onIntent(IntentRequest intent, Session session)
   {
      final PutRecordRequest kinesisRequest = new PutRecordRequest();
      kinesisRequest.setStreamName(streamName);
      fillKinesisRequest(intent, session, kinesisRequest);
      
      final LogEvent logEvent = new LogEvent(); 
      fillLogEvent(intent, session, logEvent);
      
      try
      {
         kinesisRequest.setData(ByteBuffer.wrap(jsonWriter.writeValueAsBytes(logEvent)));
      }
      catch (JsonProcessingException e)
      {
         log.error("Could not serialize intent information into JSON!", e);
         return;
      }
      
      kinesis.putRecordAsync(kinesisRequest);
   }
   
   protected void configureJackson(ObjectMapper json)
   {
   }

   protected void fillKinesisRequest(IntentRequest intent, Session session, PutRecordRequest kinesisRequest)
   {
      kinesisRequest.setPartitionKey(session.getUser().getUserId());
   }
   
   protected void fillLogEvent(IntentRequest intent, Session session, LogEvent logEvent)
   {
      logEvent.setCustomerId(session.getUser().getUserId());
      logEvent.setSessionId(session.getSessionId());
      logEvent.setRequestId(intent.getRequestId());
      logEvent.setTimeStamp(intent.getTimestamp());
      logEvent.setLocale(intent.getLocale().toLanguageTag());
      logEvent.setIntentName(intent.getIntent().getName());
      
      intent.getIntent().getSlots().values().forEach(s -> logEvent.getSlots().put(s.getName(), s.getValue()));
   }
}
