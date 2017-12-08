package net.sf.javagimmicks.hwd.job;

import static java.time.Instant.now;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import net.pushover.client.PushoverException;
import net.sf.javagimmicks.hwd.Pushover;
import net.sf.javagimmicks.hwd.Pushover.Message;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PingJob extends QuartzJobBean
{
   @Override
   protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException
   {
      boolean firstRun = lastPingResult == null;

      try
      {
         // Ping and store the result
         boolean pingResult = ping(address);
         ctx.setResult(pingResult);
         ctx.getJobDetail().getJobDataMap().put("lastResult", pingResult);

         // Prepare the Push Message
         final Message m = p.newMessage() //
               .setTitle(ctx.getJobDetail().getDescription());

         // Ping successful
         if (pingResult)
         {
            // First job run - App just started: send initial OK message
            if (firstRun)
            {
               m.setMessage( //
                     "Started to monitor address '%s'! Address is currently reachable!", address) //
                     .push();
            }
            
            // Last ping was not successful: send OK message
            else if (!lastPingResult)
            {
               m.setMessage( //
                     "Address '%s' is reachable AGAIN!", address) //
                     .push();
            }
         }

         // Ping NOT successful
         else
         {
            // First job run - App just started: send initial NOK message
            if (firstRun)
            {
               m.setMessage( //
                     "Started to monitor address '%s'! Address is currently NOT reachable!", address) //
                     .push();

               updateLastFailMessage(ctx);
            }
            
            // Last ping was successful: send NOK message
            else if (lastPingResult)
            {
               m.setMessage( //
                     "Address '%s' is NO LONGER reachable!", address) //
                     .push();

               updateLastFailMessage(ctx);
            }
            
            // Reminder interval elapsed: send reminder NOK message
            else if (now().isAfter(lastFailMessage.plus(reminderInterval)))
            {
               m.setMessage( //
                     "Reminder: address '%s' is STILL NOT reachable!", address) //
                     .push();

               updateLastFailMessage(ctx);
            }
         }
      }
      catch (PushoverException e)
      {
         throw new JobExecutionException(e);
      }
   }

   //////////// Helpers section ////////////////////
   private static void updateLastFailMessage(JobExecutionContext ctx)
   {
      ctx.getJobDetail().getJobDataMap().put("lastFailMessage", now());
   }

   private static boolean ping(String address)
   {
      try
      {
         final InetAddress a = InetAddress.getByName(address);
         return a.isReachable(1000);
      }
      catch (IOException e)
      {
         return false;
      }
   }

   //////////// Properties section ////////////////////
   private Pushover p;
   private String address;
   private TemporalAmount reminderInterval;

   private Boolean lastPingResult;
   private Instant lastFailMessage;

   public void setPushover(Pushover p)
   {
      this.p = p;
   }

   public void setAddress(String address)
   {
      this.address = address;
   }

   public void setReminderInterval(TemporalAmount reminderInterval)
   {
      this.reminderInterval = reminderInterval;
   }

   public void setLastResult(Boolean lastPingResult)
   {
      this.lastPingResult = lastPingResult;
   }

   public void setLastFailMessage(Instant lastFailMessage)
   {
      this.lastFailMessage = lastFailMessage;
   }
}
