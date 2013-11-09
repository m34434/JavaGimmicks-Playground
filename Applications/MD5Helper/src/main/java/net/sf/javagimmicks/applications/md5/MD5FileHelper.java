package net.sf.javagimmicks.applications.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5FileHelper
{
   private static final String SUFFIX_MD5 = ".md5";

   public static File getMD5File(final File file)
   {
      return new File(file.getParentFile(), file.getName() + SUFFIX_MD5);
   }

   public static boolean hasMD5File(final File file)
   {
      final File md5File = getMD5File(file);
      return md5File.exists() && md5File.isFile();
   }

   public static boolean isMD5File(final File file)
   {
      return file.isFile() && isMD5File(file.getName());
   }

   public static boolean isMD5File(final String filename)
   {
      return filename != null && filename.endsWith(SUFFIX_MD5);
   }

   public static void createMD5File(final File file) throws IOException
   {
      final FileOutputStream md5FileOut = new FileOutputStream(getMD5File(file));
      final FileInputStream fileIn = new FileInputStream(file);

      md5FileOut.write(DigestUtils.md5(fileIn));

      fileIn.close();
      md5FileOut.close();
   }
}
