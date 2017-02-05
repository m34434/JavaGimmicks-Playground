package datetest;

import java.util.Locale;

import org.joda.time.Days;
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
      
      System.out.println(Days.daysBetween(d1, d2).getDays());
      System.out.println(Days.daysBetween(d1, d1).getDays());
      System.out.println(Days.daysBetween(d2, d1).getDays());
   }

}
