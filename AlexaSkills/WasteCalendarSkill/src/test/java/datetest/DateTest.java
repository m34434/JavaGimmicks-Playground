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
      LocalDate d1 = LocalDate.parse("2017-2-07");
      LocalDate d2 = LocalDate.parse("2017-2-09");

      System.out.println(d1.toString(DateTimeFormat.mediumDate().withLocale(Locale.forLanguageTag("de-DE"))));
   }

}
