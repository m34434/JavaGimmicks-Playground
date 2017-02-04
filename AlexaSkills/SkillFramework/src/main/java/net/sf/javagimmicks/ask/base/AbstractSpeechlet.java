package net.sf.javagimmicks.ask.base;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractSpeechlet implements Speechlet {

	protected static final String MSG_FATAL_ERROR = "fatalError";

	private static final String BUNDLE_NAME = "messages";
	private static final String ATTR_LAUNCH_MODE = "___launchMode___";

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private Session session;
	private ResourceBundle bundle;

	abstract protected SpeechletResponse onLaunchInternal(LaunchRequest request)
			throws SpeechletResponseThrowable, SpeechletException;

	abstract protected SpeechletResponse onIntentInternal(IntentRequest request)
			throws SpeechletResponseThrowable, SpeechletException;

   @Override
   public void onSessionStarted(final SessionStartedRequest request, final Session session)
         throws SpeechletException {
      log.debug("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

      init(request, session);
      session.setAttribute(ATTR_LAUNCH_MODE, false);
      
      onSessionStartedInternal(request);
   }

   @Override
   public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
         throws SpeechletException {
      log.debug("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

      session.setAttribute(ATTR_LAUNCH_MODE, true);

      SpeechletResponse r;
      try {
         r = onLaunchInternal(request);
      } catch (SpeechletResponseThrowable e) {
         r = e.getResponse();
      }
      
      if(r != null && r.getShouldEndSession()) {
         onSessionEndedInternal(toSessionEndedRequest(request));
      }
      
      return r;
   }

   @Override
   public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
      log.debug("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

      init(request, session);

      SpeechletResponse r;
      try {
         r = onIntentInternal(request);
      } catch (SpeechletResponseThrowable e) {
         r = e.getResponse();
      }
      
      if(r != null && r.getShouldEndSession()) {
         onSessionEndedInternal(toSessionEndedRequest(request));
      }
      
      return r;
   }

   @Override
   public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
      log.debug("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

      onSessionEndedInternal(request);
   }

   protected void onSessionStartedInternal(final SessionStartedRequest request)
         throws SpeechletException {
   }

   protected void onSessionEndedInternal(SessionEndedRequest request) {
	}

	protected SpeechletResponse newSpeechletTellResponse(String messageKey, Object... args)
			throws SpeechletResponseThrowable {
		final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(String.format(getMessage(messageKey), args));

		final SpeechletResponse result = new SpeechletResponse();
		result.setOutputSpeech(speech);

		return SpeechletResponse.newTellResponse(speech);
	}

	protected SpeechletResponse newSpeechletAskResponseWithReprompt(String messageKey, String repromptMessageKey,
			Object... args) throws SpeechletResponseThrowable {
		final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(String.format(getMessage(messageKey), args));

		if (!(Boolean) session.getAttribute(ATTR_LAUNCH_MODE)) {
			return SpeechletResponse.newTellResponse(speech);
		}

		final PlainTextOutputSpeech speechReprompt = new PlainTextOutputSpeech();
		speechReprompt.setText(String.format(getMessage(repromptMessageKey), args));

		final Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speechReprompt);

		return SpeechletResponse.newAskResponse(speech, reprompt);
	}

	protected SpeechletResponse newSpeechletAskResponse(String messageKey, Object... args)
			throws SpeechletResponseThrowable {
		return newSpeechletAskResponseWithReprompt(messageKey, messageKey, args);
	}

	protected Session getSession() {
		return session;
	}

	protected Locale getLocale() {
		return bundle.getLocale();
	}

	protected String getMessage(String key) throws SpeechletResponseThrowable {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
		}
	}
	
   protected String getSlot(Intent intent, String slotName, String messageKey) throws SpeechletResponseThrowable
   {
      final String value = intent.getSlot(slotName).getValue();
      if(value == null || value.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponse(messageKey));
      }
      
      return value;
   }
   
   protected String getSlot(Intent intent, String slotName, String messageKey, String repromptMessageKey) throws SpeechletResponseThrowable
   {
      final String value = intent.getSlot(slotName).getValue();
      if(value == null || value.length() == 0)
      {
         throw new SpeechletResponseThrowable(newSpeechletAskResponse(messageKey, repromptMessageKey));
      }
      
      return value;
   }
   
   protected <T> void setSessionAttributeAsJson(String attributeName, Object o) throws SpeechletResponseThrowable
   {
      if(o == null)
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
   
	protected <T> T parseSessionAttribute(String attributeName, Class<T> attributeClass) throws SpeechletResponseThrowable
	{
	   if(attributeClass == null)
	   {
	      return null;
	   }
	   
	   final Object attribute = session.getAttribute(attributeName);
	   if(attribute == null)
	   {
	      return null;
	   }
      
      if(attributeClass.isInstance(attribute))
	   {
	      return attributeClass.cast(attribute);
	   }
	   
	   if(attribute instanceof String)
	   {
	      log.debug("Trying to convert read session attribute '{}' into an '{}' instance via JSON parsing! Attribute value: '{}'", attributeName, attributeClass, attribute);

	      ObjectMapper m = new ObjectMapper();
	      try
         {
            return m.readValue((String)attribute, attributeClass);
         }
         catch (IOException e)
         {
            log.error(String.format("Could not map session attribute '%s' as JSON into object!", attribute), e);
            
            throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
         }
	   }
	   
	   if(attribute instanceof Map)
	   {
         log.debug("Trying to convert read session attribute '%s' into an '%s' instance via BeanUtils Map parsing! Attribute value: '%s'", attributeName, attributeClass, attribute);

         @SuppressWarnings("unchecked")
         final Map<String, Object> attributeAsMap = (Map<String, Object>)attribute;
	      
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
	      
	      return result;
	   }
	   
	   throw new IllegalArgumentException(String.format("Could not create a %s instance from Session attribute '%s'! Given attribute type was '%s'!", attributeClass.getName(), attribute, attribute.getClass()));
	}

	private void init(final SpeechletRequest request, final Session session) {
		this.session = session;

		final Locale requestLocale = request.getLocale();
		if (bundle == null || !bundle.getLocale().equals(requestLocale)) {
			this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, requestLocale);
		}
	}

	private SessionEndedRequest toSessionEndedRequest(final SpeechletRequest request)
   {
      return SessionEndedRequest.builder().withLocale(request.getLocale()).withRequestId(request.getRequestId()).withTimestamp(request.getTimestamp()).build();
   }
}
