package net.sf.javagimmicks.applications.md5;

import org.apache.commons.cli.Options;
import org.junit.Test;

public class ConfigurationTest
{
   @Test
   public void testConfiguration()
   {
      final Options config = Configuration.buildOptions();

      Configuration.printHelp(System.out, config);
   }
}
