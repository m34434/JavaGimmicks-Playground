package de.javagimmicks.apps.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatUtils
{
   public static void closeQuietly(InputStream inputStream)
   {
      try
      {
         inputStream.close();
      }
      catch (IOException ignore)
      {
      }
   }
   public static void closeQuietly(OutputStream outputStream)
   {
      try
      {
         outputStream.close();
      }
      catch (IOException ignore)
      {
      }
   }
   public static void closeQuietly(Socket socket)
   {
      try
      {
         socket.close();
      }
      catch (IOException ignore)
      {
      }
   }
}
