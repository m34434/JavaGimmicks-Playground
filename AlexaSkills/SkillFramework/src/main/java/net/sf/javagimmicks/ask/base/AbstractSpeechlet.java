package net.sf.javagimmicks.ask.base;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

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
	public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
		log.debug("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		init(request, session);
		this.session.setAttribute(ATTR_LAUNCH_MODE, false);
	}

	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
		log.debug("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		this.session.setAttribute(ATTR_LAUNCH_MODE, true);

		try {
			return onLaunchInternal(request);
		} catch (SpeechletResponseThrowable e) {
			return e.getResponse();
		}
	}

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		log.debug("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		init(request, session);

		try {
			return onIntentInternal(request);
		} catch (SpeechletResponseThrowable e) {
			return e.getResponse();
		}
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

	protected Session getSession()
	{
		return session;
	}
	
	protected Locale getLocale()
	{
		return bundle.getLocale();
	}
	
	protected String getMessage(String key) throws SpeechletResponseThrowable {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			throw new SpeechletResponseThrowable(newSpeechletTellResponse(MSG_FATAL_ERROR));
		}
	}

	private void init(final SpeechletRequest request, final Session session) {
		this.session = session;

		final Locale requestLocale = request.getLocale();
		if (bundle == null || !bundle.getLocale().equals(requestLocale)) {
			this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, requestLocale);
		}
	}

}
