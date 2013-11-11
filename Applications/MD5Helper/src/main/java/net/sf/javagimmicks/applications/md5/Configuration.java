package net.sf.javagimmicks.applications.md5;

import static org.apache.commons.cli.OptionBuilder.create;
import static org.apache.commons.cli.OptionBuilder.hasArg;
import static org.apache.commons.cli.OptionBuilder.isRequired;
import static org.apache.commons.cli.OptionBuilder.withDescription;
import static org.apache.commons.cli.OptionBuilder.withLongOpt;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Configuration
{
   private final boolean _create;
   private final boolean _update;
   private final boolean _validate;
   private final boolean _recursive;
   private final File _root;

   private Configuration(final boolean create, final boolean update, final boolean validate, final boolean recursive,
         final File root)
   {
      this._create = create;
      this._update = update;
      this._validate = validate;
      this._recursive = recursive;
      this._root = root;
   }

   public boolean isCreate()
   {
      return this._create;
   }

   public boolean isUpdate()
   {
      return this._update;
   }

   public boolean isValidate()
   {
      return this._validate;
   }

   public boolean isRecursive()
   {
      return this._recursive;
   }

   public File getRoot()
   {
      return this._root;
   }

   public static Configuration parse(final String[] args)
   {
      final Options cmdLineOptions = buildOptions();

      final CommandLine cmd;
      try
      {
         cmd = new BasicParser().parse(cmdLineOptions, args);

         if (cmd.getArgs().length != 1)
         {
            throw new ParseException("No or too much folders specified!");
         }
      }
      catch (final ParseException e)
      {
         Configuration.printHelp(System.out, cmdLineOptions);
         return null;
      }

      final boolean create = cmd.hasOption("c");
      final boolean update = cmd.hasOption("u");
      final boolean validate = cmd.hasOption("v");
      final boolean recursive = cmd.hasOption("r");

      return new Configuration(create, update, validate, recursive, new File(cmd.getArgs()[0]));
   }

   public static Options buildOptions()
   {
      withLongOpt("create");
      withDescription("creates new MD5 files for found files that don't yet have an MD5 file");
      hasArg(false);
      isRequired(true);
      final Option createOption = create("c");

      withLongOpt("update");
      withDescription("creates new or updates existing MD5 files for found files");
      hasArg(false);
      isRequired(true);
      final Option updateOption = create("u");

      withLongOpt("validate");
      withDescription("validates any found file which has an according MD5 file");
      hasArg(false);
      isRequired(true);
      final Option validateOption = create("v");

      final OptionGroup mainOptionGroup = new OptionGroup();
      mainOptionGroup.setRequired(true);
      mainOptionGroup.addOption(createOption);
      mainOptionGroup.addOption(updateOption);
      mainOptionGroup.addOption(validateOption);

      withLongOpt("recursive");
      withDescription("recurses into sub-folders");
      hasArg(false);
      isRequired(false);
      final Option recursiveOption = create("r");

      final Options options = new Options();
      options.addOptionGroup(mainOptionGroup);
      options.addOption(recursiveOption);

      return options;
   }

   public static void printHelp(final OutputStream out, final Options options)
   {
      final PrintWriter pw = new PrintWriter(System.out);

      final HelpFormatter helpFormatter = new HelpFormatter();
      helpFormatter.setSyntaxPrefix("Usage: ");

      helpFormatter.printHelp(pw, 80, "java -jar <jar-name> OPTIONS FOLDER", "Option description:", options, 3, 3,
            "JavaGimmicks MD5 Helper");

      pw.flush();
   }
}
