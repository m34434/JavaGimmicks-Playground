package net.sf.javagimmicks.ask.base;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractSpeechlet implements SpeechletV2
{
   protected static final String MSG_FATAL_ERROR = "fatalError";

   protected static final String INTENT_CANCEL = "AMAZON.CancelIntent";
   protected static final String INTENT_STOP = "AMAZON.StopIntent";

   private static final String ATTR_LAUNCH_MODE = "___launchMode___";

   private static final String BUNDLE_NAME = "messages";

   protected final Logger log = LoggerFactory.getLogger(getClass());

   private SpeechletRequestEnvelope<? extends SpeechletRequest> envelope;
   private ResourceBundle bundle;

   abstract protected SpeechletResponse onLaunchInternal(LaunchRequest request)
         throws SpeechletResponseThrowable;

   abstract protected SpeechletResponse onIntentInternal(IntentRequest request)
         throws SpeechletResponseThrowable;

   @Override
   public final void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope)
   {
      if (log.isDebugEnabled())
      {
         log.debug("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
               requestEnvelope.getSession().getSessionId());
      }

      init(requestEnvelope);
      setLaunchMode(false);

      onSessionStartedInternal(requestEnvelope.getRequest());
   }

   @Override
   public final SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope)
   {
      if (log.isDebugEnabled())
      {
         log.debug("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
               requestEnvelope.getSession().getSessionId());
      }

      setLaunchMode(true);

      SpeechletResponse r;
      try
      {
         r = onLaunchInternal(requestEnvelope.getRequest());
      }
      catch (SpeechletResponseThrowable e)
      {
         r = e.getResponse();
      }

      if (r != null && r.getShouldEndSession())
      {
         onSessionEndedInternal(toSessionEndedRequest(requestEnvelope.getRequest()));
      }

      return r;
   }

   @Override
   public final SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope)
   {
      if (log.isDebugEnabled())
      {
         log.debug("onIntent requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
               requestEnvelope.getSession().getSessionId());
      }

      init(requestEnvelope);

      SpeechletResponse r;
      try
      {
         r = onIntentInternal(requestEnvelope.getRequest());
      }
      catch (SpeechletResponseThrowable e)
      {
         r = e.getResponse();
      }

      if (r != null && r.getShouldEndSession())
      {
         onSessionEndedInternal(toSessionEndedRequest(requestEnvelope.getRequest()));
      }

      return r;
   }

   @Override
   public final void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope)
   {
      if (log.isDebugEnabled())
      {
         log.debug("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
               requestEnvelope.getSession().getSessionId());
      }

      onSessionEndedInternal(requestEnvelope.getRequest());
   }

   protected void onSessionStartedInternal(final SessionStartedRequest request)
   {}

   protected void onSessionEndedInternal(SessionEndedRequest request)
   {}

   protected SpeechletResponse newSpeechletTellResponse(String messageKey, Object... args)
         throws SpeechletResponseThrowable
   {
      final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(String.format(getMessage(messageKey), args));

      final SpeechletResponse result = new SpeechletResponse();
      result.setOutputSpeech(speech);

      return SpeechletResponse.newTellResponse(speech);
   }

   protected SpeechletResponse newSpeechletAskResponseWithReprompt(String messageKey, String repromptMessageKey,
         Object... args) throws SpeechletResponseThrowable
   {
      final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(String.format(getMessage(messageKey), args));

      if (!isLaunchMode())
      {
         return SpeechletResponse.newTellResponse(speech);
      }

      final PlainTextOutputSpeech speechReprompt = new PlainTextOutputSpeech();
      speechReprompt.setText(String.format(getMessage(repromptMessageKey), args));

      final Reprompt reprompt = new Reprompt();
      reprompt.setOutputSpeech(speechReprompt);

      return SpeechletResponse.newAskResponse(speech, reprompt);
   }

   protected SpeechletResponse newSpeechletAskResponse(String messageKey, Object... args)
         throws SpeechletResponseThrowable
   {
      return newSpeechletAskResponseWithReprompt(messageKey, messageKey, args);
   }

   protected Context getContext()
   {
      return envelope.getContext();
   }

   protected Session getSession()
   {
      return envelope.getSession();
   }

   protected Locale getRequestLocale()
   {
      return envelope.getRequest().getLocale();
   }

   protected Locale getBundleLocale()
   {
      return bundle.getLocale();
   }

   protected boolean isLaunchMode()
   {
      return (boolean) getSession().getAttribute(ATTR_LAUNCH_MODE);
   }

   protected void setLaunchMode(boolean launchMode)
   {
      getSession().setAttribute(ATTR_LAUNCH_MODE, launchMode);
   }

   protected String getMessage(String key) throws SpeechletResponseThrowable
   {
      try
      {
         return bundle.getString(key);
      }
      catch (MissingResourceException e)
      {
         log.error("Could not find requested message key {} in resource bundle!", key);
         throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
      }
   }

   protected String getSlot(Intent intent, String slotName, String messageKeyEmpty, String repromptMessageKey)
         throws SpeechletResponseThrowable
   {
      final String value = intent.getSlot(slotName).getValue();
      if (value == null || value.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponse(messageKeyEmpty, repromptMessageKey));
      }

      return value;
   }

   protected String getSlot(Intent intent, String slotName, String messageKeyEmpty) throws SpeechletResponseThrowable
   {
      return getSlot(intent, slotName, messageKeyEmpty, messageKeyEmpty);
   }

   protected int getSlotInt(Intent intent, String slotName, String messageKeyEmpty, String messageKeyFormat,
         String repromptMessageKey) throws SpeechletResponseThrowable
   {
      final String intString = getSlot(intent, slotName, messageKeyEmpty, messageKeyEmpty);

      try
      {
         return Integer.parseInt(intString);
      }
      catch (Exception ex)
      {
         log.warn("Could not parse integer string!", ex);
         throw new SpeechletResponseThrowable(
               newSpeechletAskResponseWithReprompt(messageKeyFormat, repromptMessageKey));
      }
   }

   protected LocalDate getSlotLocalDate(Intent intent, String slotName, String messageKeyEmpty, String messageKeyFormat,
         String repromptMessageKey) throws SpeechletResponseThrowable
   {
      final String dateString = getSlot(intent, slotName, messageKeyEmpty, repromptMessageKey);

      try
      {
         return LocalDate.parse(dateString);
      }
      catch (Exception ex)
      {
         log.warn("Could not parse local date string!", ex);
         throw new SpeechletResponseThrowable(
               newSpeechletAskResponseWithReprompt(messageKeyFormat, repromptMessageKey));
      }
   }

   protected <T> void setSessionAttributeAsJson(String attributeName, Object o) throws SpeechletResponseThrowable
   {
      if (o == null)
      {
         return;
      }

      ObjectMapper m = new ObjectMapper();
      try
      {
         final String jsonString = m.writeValueAsString(o);
         log.debug("Serializing object into session attribute '{}' as JSON string '{}'", attributeName, jsonString);

         getSession().setAttribute(attributeName, jsonString);
      }
      catch (IOException e)
      {
         log.error(String.format("Could not set session attribute '%s' as JSON into object", o), e);

         throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
      }

   }

   protected <T> Optional<T> parseSessionAttribute(String attributeName, Class<T> attributeClass)
         throws SpeechletResponseThrowable
   {
      if (attributeClass == null)
      {
         return Optional.empty();
      }

      final Object attribute = getSession().getAttribute(attributeName);
      if (attribute == null)
      {
         return Optional.empty();
      }

      if (attributeClass.isInstance(attribute))
      {
         return Optional.of(attributeClass.cast(attribute));
      }

      if (attribute instanceof String)
      {
         log.debug(
               "Trying to convert read session attribute '{}' into an '{}' instance via JSON parsing! Attribute value: '{}'",
               attributeName, attributeClass, attribute);

         ObjectMapper m = new ObjectMapper();
         try
         {
            return Optional.of(m.readValue((String) attribute, attributeClass));
         }
         catch (IOException e)
         {
            log.error(String.format("Could not map session attribute '%s' as JSON into object!", attribute), e);

            throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
         }
      }

      if (attribute instanceof Map)
      {
         log.debug(
               "Trying to convert read session attribute '%s' into an '%s' instance via BeanUtils Map parsing! Attribute value: '%s'",
               attributeName, attributeClass, attribute);

         @SuppressWarnings("unchecked")
         final Map<String, Object> attributeAsMap = (Map<String, Object>) attribute;

         T result;
         try
         {
            result = attributeClass.newInstance();
            new BeanMap(result).putAll(attributeAsMap);
         }
         catch (Exception e)
         {
            log.error(String.format("Could not map session attribute '%s' into object", attributeAsMap), e);

            throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
         }

         return Optional.of(result);
      }

      throw new IllegalArgumentException(
            String.format("Could not create a %s instance from Session attribute '%s'! Given attribute type was '%s'!",
                  attributeClass.getName(), attribute, attribute.getClass()));
   }

   private void init(final SpeechletRequestEnvelope<? extends SpeechletRequest> envelope)
   {
      this.envelope = envelope;

      final Locale requestLocale = envelope.getRequest().getLocale();
      if (bundle == null || !bundle.getLocale().equals(requestLocale))
      {
         this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, requestLocale);
      }
   }

   private SessionEndedRequest toSessionEndedRequest(final SpeechletRequest request)
   {
      return SessionEndedRequest.builder().withLocale(request.getLocale()).withRequestId(request.getRequestId())
            .withTimestamp(request.getTimestamp()).build();
   }
}
