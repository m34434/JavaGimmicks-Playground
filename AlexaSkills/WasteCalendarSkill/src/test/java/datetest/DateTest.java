package datetest;

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public class DateTest
{
   @Test
   public void testDateStuff()
   {
      LocalDate d = LocalDate.parse("2017-2-07");
      System.out.println(d.toString(DateTimeFormat.fullDate().withLocale(Locale.forLanguageTag("de-DE"))));
      System.out.println(d.toString(DateTimeFormat.fullDate().withLocale(Locale.forLanguageTag("en-US"))));
   }

}
