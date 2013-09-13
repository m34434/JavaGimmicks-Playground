/**
 * 
 */
package de.javagimmicks.apps.chat.client;


public interface ChatClientListener
{
   public void messageReceived(ChatClient client, MessageEvent event);
   public void connectionClosed(ChatClient client);
   
   public void userJoined(ChatClient client, UserEvent event);
   public void userLeft(ChatClient client, UserEvent event);
}