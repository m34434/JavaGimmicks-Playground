package net.sf.javagimmicks.applications.md5;

import static org.apache.commons.cli.OptionBuilder.create;
import static org.apache.commons.cli.OptionBuilder.hasArg;
import static org.apache.commons.cli.OptionBuilder.isRequired;
import static org.apache.commons.cli.OptionBuilder.withDescription;
import static org.apache.commons.cli.OptionBuilder.withLongOpt;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class Configuration
{
   public static Options buildOptions()
   {
      withLongOpt("create");
      withDescription("creates new MD5 files for found files");
      hasArg(false);
      isRequired(true);
      final Option createOption = create("c");

      withLongOpt("validate");
      withDescription("validates any found file which has an according MD5 file");
      hasArg(false);
      isRequired(true);
      final Option validateOption = create("v");

      final OptionGroup createValidateOption = new OptionGroup();
      createValidateOption.setRequired(true);
      createValidateOption.addOption(createOption);
      createValidateOption.addOption(validateOption);

      withLongOpt("recursive");
      withDescription("recurses into sub-folders");
      hasArg(false);
      isRequired(false);
      final Option recursiveOption = create("r");

      final Options options = new Options();
      options.addOptionGroup(createValidateOption);
      options.addOption(recursiveOption);

      return options;
   }
}
