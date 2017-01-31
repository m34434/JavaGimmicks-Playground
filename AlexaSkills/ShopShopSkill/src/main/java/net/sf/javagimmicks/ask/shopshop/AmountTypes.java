package net.sf.javagimmicks.ask.shopshop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import net.sf.javagimmicks.collections.bidimap.BidiMap;
import net.sf.javagimmicks.collections.bidimap.DualBidiMap;

public class AmountTypes
{
   private final Locale locale;
   private final BidiMap<String, String> m = new DualBidiMap<String, String>(new HashMap<>(), new HashMap<>());
   
   public AmountTypes(Locale locale) throws IOException
   {
      this.locale = locale;
      
      final Properties p = new Properties();
      p.load(getClass().getClassLoader().getResourceAsStream("amountTypes_" + locale.toLanguageTag() + ".properties"));
      
      p.forEach((k, v) -> m.put(k.toString(), v.toString()));
   }

   public Locale getLocale()
   {
      return locale;
   }
   
   public String getAbbreviation(String spokenType)
   {
      return m.get(spokenType);
   }
   
   public String getSpokenType(String abbreviation)
   {
      return m.getKey(abbreviation);
   }
}
