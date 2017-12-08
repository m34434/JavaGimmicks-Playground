package net.sf.javagimmicks.hwd;

import static de.chandre.quartz.spring.QuartzUtils.jobBuilder;
import static de.chandre.quartz.spring.QuartzUtils.simpleTriggerBuilder;

import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.TemporalAmount;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.sf.javagimmicks.hwd.job.PingJob;

@SpringBootApplication
public class HomeWatchDogApplication
{
   @Value("${token}")
   private String apiToken;
   
   @Value("${user}")
   private String userId;

   /////// Basic stuff /////////
   public static void main(String[] args)
   {
      SpringApplication.run(HomeWatchDogApplication.class, args);
   }

   @Bean
   public Pushover pushover()
   {
      System.out.printf("%s --- %s%n", apiToken, userId);
      return new Pushover(apiToken, userId);
   }

   ///////// Job: Arlo Hub /////////
   @Bean
   public JobDetail jobPingArlo()
   {
      return newPingJob("pingJobArlo", "Ping job for Arlo Hub", "192.168.0.145", Duration.ofHours(1));
   }

   @Bean
   public Trigger triggerPingArlo() throws ParseException
   {
      return simpleTrigger(jobPingArlo(), Duration.ofMinutes(1));
   }
   
   ///////// Job: iPad Air 2 (Testing) /////////
   @Bean
   public JobDetail jobPingIpadAir2()
   {
      return newPingJob("pingJobIPadAir2", "Ping job for iPad Air 2", "192.168.0.49", Duration.ofSeconds(15));
   }

   @Bean
   public Trigger triggerPingIpadAir2() throws ParseException
   {
      return simpleTrigger(jobPingIpadAir2(), Duration.ofSeconds(5));
   }
   
   /////// Private helpers /////////
   private JobDetail newPingJob(String name, String description, String address, TemporalAmount reminderInterval)
   {
      return jobBuilder() //
            .name(name) //
            .description(description) //
            .jobClass(PingJob.class) //
            .addJobData("pushover", pushover()) //
            .addJobData("address", address) //
            .addJobData("reminderInterval", reminderInterval)
            .durability(true) //
            .build();
   }
   
   private Trigger simpleTrigger(JobDetail j, Duration i) throws ParseException
   {
      return simpleTriggerBuilder() //
            .name(j.getKey().getName() + "Trigger") //
            .repeatInterval(i.toMillis()) //
            .jobDetail(j) //
            .build();
   }

}
