package net.sf.javagimmicks.applications.md5;

import java.io.File;
import java.io.IOException;

import net.sf.javagimmicks.io.FileTraverser;
import net.sf.javagimmicks.io.FileTraverser.FileVisitor;
import net.sf.javagimmicks.io.FileTraverser.TypeFilter;

public class Main
{
   public static void main(final String[] args)
   {
      final Configuration config = Configuration.parse(args);
      if (config == null)
      {
         return;
      }

      final FileTraverser traverser = new FileTraverser(config.getRoot());
      traverser.setRecursive(config.isRecursive());
      traverser.setTypeFilter(TypeFilter.FILE);

      if (config.isCreate())
      {
         traverser.run(new CreateVisitor());
      }
      else if (config.isValidate())
      {
         traverser.run(new ValidateVisitor());
      }
   }

   private static class CreateVisitor implements FileVisitor
   {
      public void visit(final File file)
      {
         if (MD5FileHelper.isMD5File(file))
         {
            return;
         }

         try
         {
            MD5FileHelper.createMD5File(file);
            System.out.println(String.format("MD5 file generated for file '%1$s'!", file));
         }
         catch (final IOException e)
         {
            System.err.println(String.format("Could not create MD5 file for file '%1$s'! System message: '%2$s'", file,
                  e.getMessage()));
         }
      }
   }

   private static class ValidateVisitor implements FileVisitor
   {
      public void visit(final File file)
      {
         if (MD5FileHelper.isMD5File(file))
         {
            return;
         }

         try
         {
            if (!MD5FileHelper.isChecksumValid(file))
            {
               System.err.println(String.format("Checksum invalid of file '%1$s'!", file));
            }
         }
         catch (final IOException e)
         {
            System.err.println(String.format("Could not validate MD5 file for file '%1$s'! System message: '%2$s'",
                  file,
                  e.getMessage()));
         }
      }
   }
}
