package net.sf.javagimmicks.ask.kinesislogger;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseAsync;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseAsyncClientBuilder;
import com.amazonaws.services.kinesisfirehose.model.PutRecordRequest;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.sf.javagimmicks.ask.kinesislogger.model.LogEvent;

public class SkillKinesisFirehoseLogger
{
   protected final Logger log = LoggerFactory.getLogger(getClass());

   private final AmazonKinesisFirehoseAsync firehose;
   private final String deliveryStreamName;
   
   private final ObjectWriter jsonWriter;

   public SkillKinesisFirehoseLogger(AmazonKinesisFirehoseAsync firehose, String deliveryStreamName)
   {
      Objects.requireNonNull(firehose, "Kinesis client may not be null!");
      Objects.requireNonNull(deliveryStreamName, "Stream name may not be null!");
      
      this.firehose = firehose;
      this.deliveryStreamName = deliveryStreamName;
      
      final ObjectMapper json = new ObjectMapper();
      configureJackson(json);
      this.jsonWriter = json.writer();
   }
   
   public SkillKinesisFirehoseLogger(AmazonKinesisFirehoseAsyncClientBuilder firehoseBuilder, String deliveryStreamName)
   {
      this(Objects.requireNonNull(firehoseBuilder, "Kinesis client builder may not be null!").build(), deliveryStreamName);
   }
   
   public SkillKinesisFirehoseLogger(String deliveryStreamName)
   {
      this(AmazonKinesisFirehoseAsyncClientBuilder.defaultClient(), deliveryStreamName);
   }
   
   public final void onIntent(IntentRequest intent, Session session)
   {
      final PutRecordRequest firehoseRequest = new PutRecordRequest();
      firehoseRequest.setDeliveryStreamName(deliveryStreamName);
      
      final LogEvent logEvent = new LogEvent(); 
      fillLogEvent(intent, session, logEvent);
      
      try
      {
         final Record r = new Record();
         r.setData(ByteBuffer.wrap(jsonWriter.writeValueAsBytes(logEvent)));
         
         firehoseRequest.setRecord(r);
      }
      catch (JsonProcessingException e)
      {
         log.error("Could not serialize intent information into JSON!", e);
         return;
      }
      
      firehose.putRecordAsync(firehoseRequest);
   }
   
   protected void configureJackson(ObjectMapper json)
   {
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
