package net.sf.javagimmicks.hwd;

import net.pushover.client.MessagePriority;
import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverException;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;

public class Pushover
{
   private static final String API_TOKEN = "";
   // private static final String USER_ID = "";
   private static final String GROUP_ID = "";

   private final PushoverClient p = new PushoverRestClient();

   public Message newMessage()
   {
      return new Message(API_TOKEN, GROUP_ID);
   }
   
   public class Message
   {
      private final PushoverMessage.Builder m;

      private Message(String apiToken, String userId)
      {
         m = PushoverMessage.builderWithApiToken(apiToken).setUserId(userId);
      }
      
      public void push() throws PushoverException
      {
         final PushoverMessage msg = m.build();
         
         synchronized (p)
         {
            p.pushMessage(msg);
         }
      }

      public Message setMessage(String message)
      {
         m.setMessage(message);
         return this;
      }

      public Message setMessage(String format, Object... args)
      {
         return setMessage(String.format(format, args));
      }

      public Message setDevice(String device)
      {
         m.setDevice(device);
         return this;
      }

      public Message setTitle(String title)
      {
         m.setTitle(title);
         return this;
      }

      public Message setUrl(String url)
      {
         m.setUrl(url);
         return this;
      }

      public Message setTitleForURL(String titleForURL)
      {
         m.setTitleForURL(titleForURL);
         return this;
      }

      public Message setPriority(MessagePriority priority)
      {
         m.setPriority(priority);
         return this;
      }

      public Message setTimestamp(Long timestamp)
      {
         m.setTimestamp(timestamp);
         return this;
      }

      public Message setSound(String sound)
      {
         m.setSound(sound);
         return this;
      }
   }
}
