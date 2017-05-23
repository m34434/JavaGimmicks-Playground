package net.sf.javagimmicks.ask.kinesislogger.model;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class LogEvent
{
   private String customerId;
   private String sessionId;
   private String requestId;
   private Date timeStamp;
   private String locale;
   private String intentName;
   private Map<String, String> slots = new TreeMap<>();

   public String getCustomerId()
   {
      return customerId;
   }

   public void setCustomerId(String customerId)
   {
      this.customerId = customerId;
   }

   public String getSessionId()
   {
      return sessionId;
   }

   public void setSessionId(String sessionId)
   {
      this.sessionId = sessionId;
   }

   public String getRequestId()
   {
      return requestId;
   }

   public void setRequestId(String requestId)
   {
      this.requestId = requestId;
   }

   public Date getTimeStamp()
   {
      return timeStamp;
   }

   public void setTimeStamp(Date timeStamp)
   {
      this.timeStamp = timeStamp;
   }

   public String getLocale()
   {
      return locale;
   }

   public void setLocale(String locale)
   {
      this.locale = locale;
   }

   public String getIntentName()
   {
      return intentName;
   }

   public void setIntentName(String intentName)
   {
      this.intentName = intentName;
   }

   public Map<String, String> getSlots()
   {
      return slots;
   }

   public void setSlots(Map<String, String> slots)
   {
      this.slots = slots;
   }

   
}
