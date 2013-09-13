package de.javagimmicks.apps.chat.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.javagimmicks.apps.chat.ChatConstants;
import de.javagimmicks.apps.chat.model.ChannelInfo;
import de.javagimmicks.apps.chat.model.Exit;
import de.javagimmicks.apps.chat.model.Login;
import de.javagimmicks.apps.chat.model.Message;
import de.javagimmicks.apps.chat.model.UserInfo;
import de.javagimmicks.apps.chat.model.UserMessage;
import de.javagimmicks.apps.chat.model.ChannelInfo.Type;

public class ChatServer implements ChatConstants
{
   private static final int HISTORY_SIZE = 10;
   
   protected final List<ClientConnectionThread> _clientConnections = new ArrayList<ClientConnectionThread>();
   protected final ServerSocket _serverSocket;
   protected final Queue<UserMessage> _messageHistory = new LinkedList<UserMessage>();
   
   protected Thread _serverThread;
   
   public static void main(String[] args) throws IOException
   {
      ServerSocket listener = new ServerSocket(PORT);
      
      ChatServer server = new ChatServer(listener);
      server.start();      
   }
   
   public ChatServer(ServerSocket socket)
   {
      _serverSocket = socket;
   }
   
   public ChatServer(int port) throws IOException
   {
      this(new ServerSocket(port));
   }
   
   public synchronized void start()
   {
      if(_serverThread != null)
      {
         throw new IllegalStateException("Chat server already started!");
      }
      
      _serverThread = new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               runInternal();
            }
            catch (IOException e)
            {
            }
            
            exit();
         }
      };
      
      _serverThread.start();
   }
   
   public synchronized void stop()
   {
      _serverThread.interrupt();

      exit();
      
      try
      {
         _serverSocket.close();
      }
      catch (IOException e)
      {
      }
   }
   
   public void checkLogin(Login login) throws ProtocolException
   {
      
   }
   
   public void notifyPrivateMessage(UserInfo receiver, ClientConnectionThread sender, Message message)
   {
      final UserMessage userMessage = new UserMessage(sender.getLogin().getUserInfo(), message, true);
      
      synchronized (_clientConnections)
      {
         for(Iterator<ClientConnectionThread> iter = _clientConnections.iterator(); iter.hasNext();)
         {
            ClientConnectionThread connection = iter.next();
            
            Login login = connection.getLogin();
            
            if(login == null || !login.getUserInfo().equals(receiver))
            {
               continue;
            }
            
            try
            {
               connection.getOutput().writeObject(userMessage);
            }
            catch (IOException e)
            {
               connection.interrupt();
               iter.remove();
            }
         }
      }
      
      try
      {
         sender.getOutput().writeObject(userMessage);
      }
      catch (IOException e)
      {
         sender.interrupt();
      }
   }
   
   public void notifyMessage(UserMessage message)
   {
      synchronized (_clientConnections)
      {
         for(Iterator<ClientConnectionThread> iter = _clientConnections.iterator(); iter.hasNext();)
         {
            ClientConnectionThread connection = iter.next();
            
            Login login = connection.getLogin();
            
            if(login == null)
            {
               continue;
            }
            
            try
            {
               connection.getOutput().writeObject(message);
            }
            catch (IOException e)
            {
               connection.interrupt();
               iter.remove();
            }
         }
         
         synchronized (_messageHistory)
         {
            _messageHistory.offer(message);
            if(_messageHistory.size() > HISTORY_SIZE)
            {
               _messageHistory.poll();
            }
         }
      }
   }
   
   public void notifyLogin(ClientConnectionThread connection)
   {
      synchronized(_clientConnections)
      {
         ObjectOutputStream output = connection.getOutput();
         
         // Send user history
         for(ClientConnectionThread clientConnection : _clientConnections)
         {
            try
            {
               output.writeObject(new ChannelInfo(clientConnection.getLogin().getUserInfo(), Type.JOINED));
            }
            catch (IOException e)
            {
               return;
            }
         }

         // Send message history
         synchronized(_messageHistory)
         {
            for(UserMessage message : _messageHistory)
            {
               try
               {
                  output.writeObject(message);
               }
               catch (IOException e)
               {
                  return;
               }
            }
         }

         _clientConnections.add(connection);
         
         Login login = connection.getLogin();
         notifyChannelInfo(new ChannelInfo(login.getUserInfo(), Type.JOINED));
         notifyMessage(new UserMessage(SYSTEM_USER, login.getUserInfo().getUsername() + " has joined the room!"));
      }
   }

   public void notifyExit(ClientConnectionThread connection)
   {
      synchronized(_clientConnections)
      {
         _clientConnections.remove(connection);

         Login login = connection.getLogin();
         if(login != null)
         {
            notifyChannelInfo(new ChannelInfo(login.getUserInfo(), Type.LEFT));
            notifyMessage(new UserMessage(SYSTEM_USER, login.getUserInfo().getUsername() + " has left the room!"));
         }
      }
   }
   
   private void notifyChannelInfo(ChannelInfo channelInfo)
   {
      synchronized (_clientConnections)
      {
         for(Iterator<ClientConnectionThread> iter = _clientConnections.iterator(); iter.hasNext();)
         {
            ClientConnectionThread connection = iter.next();
            
            Login login = connection.getLogin();
            
            if(login == null)
            {
               continue;
            }
            
            try
            {
               ObjectOutputStream output = connection.getOutput();

               output.writeObject(channelInfo);
            }
            catch (IOException e)
            {
               connection.interrupt();
               iter.remove();
            }
         }
      }
   }
   
   private void runInternal() throws IOException
   {
      while(!Thread.interrupted())
      {
         Socket clientSocket = _serverSocket.accept();
         
         new ClientConnectionThread(this, clientSocket).start();
      }
   }
   
   private void exit()
   {
      synchronized(_clientConnections)
      {
         for(ClientConnectionThread connection : _clientConnections)
         {
            try
            {
               connection.getOutput().writeObject(Exit.INSTANCE);
            }
            catch (IOException e)
            {
            }
            connection.interrupt();
         }
         
         _clientConnections.clear();
      }
   }
}
