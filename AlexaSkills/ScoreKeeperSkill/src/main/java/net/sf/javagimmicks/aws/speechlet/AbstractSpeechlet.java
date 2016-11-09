package net.sf.javagimmicks.aws.speechlet;

import java.util.Locale;
import java.util.ResourceBundle;

import com.amazon.speech.speechlet.Speechlet;

public abstract class AbstractSpeechlet implements Speechlet
{
    protected final Locale locale;
    protected final ResourceBundle bundle;

    protected AbstractSpeechlet(Locale locale)
    {
        this.locale = locale;
        
        this.bundle = ResourceBundle.getBundle(getClass().getPackage().getName() + ".messages", locale);
    }

    protected final String getString(String key)
    {
        return bundle.getString(key);
    }
    
    protected final String getString(String key, Object... args)
    {
        return String.format(getString(key), args);
    }
    
}
