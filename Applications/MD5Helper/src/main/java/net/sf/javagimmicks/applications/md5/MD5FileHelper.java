package net.sf.javagimmicks.applications.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

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
      final Writer md5FileOut = new OutputStreamWriter(new FileOutputStream(getMD5File(file)), "US-ASCII");

      try
      {
         md5FileOut.write(getMD5String(file));
      }
      finally
      {
         md5FileOut.close();
      }
   }

   public static boolean isChecksumValid(final File file) throws IOException
   {
      final File md5File = getMD5File(file);

      return !md5File.isFile() || getMD5String(md5File).equals(readMD5File(md5File));
   }

   public static String readMD5File(final File md5File) throws IOException
   {
      return IOUtils.toString(new InputStreamReader(new FileInputStream(md5File), "US-ASCII"));
   }

   private static String getMD5String(final File file) throws IOException
   {
      final FileInputStream fileIn = new FileInputStream(file);

      try
      {
         return DigestUtils.md5Hex(fileIn);
      }
      finally
      {
         fileIn.close();
      }
   }
}
