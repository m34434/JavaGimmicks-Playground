package de.javagimmicks.apps.chat.client;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.javagimmicks.apps.chat.ChatConstants;
import de.javagimmicks.apps.chat.ChatUtils;
import de.javagimmicks.apps.chat.model.ChannelInfo;
import de.javagimmicks.apps.chat.model.Exit;
import de.javagimmicks.apps.chat.model.Login;
import de.javagimmicks.apps.chat.model.Message;
import de.javagimmicks.apps.chat.model.UserInfo;
import de.javagimmicks.apps.chat.model.UserMessage;
import de.javagimmicks.apps.chat.model.ChannelInfo.Type;

public class ChatClient implements ChatConstants
{
   private final String _userName;
   private final String _password;
   private final Color _color;
   private final String _host;
   private final int _port;
   
   private Socket _clientSocket;
   private ObjectInputStream _input;
   private ObjectOutputStream _output;
   private final Lock _readLock;
   private final Lock _writeLock;
   
   private List<UserInfo> _users = new ArrayList<UserInfo>();
   
   private List<ChatClientListener> _listeners = new ArrayList<ChatClientListener>();

   public ChatClient(String userName, String password, Color color, String host, int port)
   {
      _userName = userName;
      _password = password;
      _color = color;
      _host = host;
      _port = port;
      
      final ReadWriteLock rwLock = new ReentrantReadWriteLock();
      _readLock = rwLock.readLock();
      _writeLock = rwLock.writeLock();
   }

   public ChatClient(String userName, String password, Color color, String host)
   {
      this(userName, password, color, host, PORT);
   }
   
   public boolean isConnected()
   {
      _readLock.lock();
      try
      {
         return _clientSocket != null && !_clientSocket.isClosed();
      }
      finally
      {
         _readLock.unlock();
      }
   }
   
   public String getUserName()
   {
      return _userName;
   }

   public String getPassword()
   {
      return _password;
   }

   public Color getColor()
   {
      return _color;
   }

   public String getHost()
   {
      return _host;
   }
   
   public int getPort()
   {
      return _port;
   }

   public void connect() throws ClientException
   {
      _writeLock.lock();
      try
      {
         checkClosed();

         _clientSocket = new Socket(_host, _port);
         _input = new ObjectInputStream(_clientSocket.getInputStream());
         _output = new ObjectOutputStream(_clientSocket.getOutputStream());
         
         _output.writeObject(new Login(new UserInfo(_userName, _color), _password));
         
         new ReceiveThread().start();
      }
      catch (Exception ex)
      {
         closeAndCleanupConnection();
         throw new ClientException(ex);
      }
      finally
      {
         _writeLock.unlock();
      }
   }

   public void disconnect()
   {
      _readLock.lock();
      try
      {
         _output.writeObject(Exit.INSTANCE);
         _readLock.unlock();
      }
      catch (Exception e)
      {
         _readLock.unlock();
         closeAndCleanupConnection();
      }
   }
   
   public void send(String message) throws ClientException
   {
      if(message == null)
      {
         return;
      }

      _readLock.lock();
      try
      {
         checkOpen();
         
         try
         {
            _output.writeObject(new Message(message));
         }
         catch (IOException e)
         {
            throw new ClientException(e);
         }
      }
      finally
      {
         _readLock.unlock();
      }
   }
   
   public void whisper(String message, String userName) throws ClientException
   {
      if(message == null || userName == null)
      {
         return;
      }

      _readLock.lock();
      try
      {
         checkOpen();

         final UserInfo userInfo = findUser(userName);
         
         if(userInfo != null)
         {
            try
            {
               _output.writeObject(new UserMessage(userInfo, message));
            }
            catch (IOException e)
            {
               throw new ClientException(e);
            }
         }
      }
      finally
      {
         _readLock.unlock();
      }
   }

   public void addListener(ChatClientListener listener)
   {
      _listeners.add(listener);
   }
   
   public void removeListener(ChatClientListener listener)
   {
      _listeners.remove(listener);
   }
   
   private void closeAndCleanupConnection()
   {
      _writeLock.lock();
      try
      {
         if(_output != null)
         {
            ChatUtils.closeQuietly(_output);
            _output = null;
         }
         
         if(_input != null)
         {
            ChatUtils.closeQuietly(_input);
            _input = null;
         }
         
         if(_clientSocket != null)
         {
            ChatUtils.closeQuietly(_clientSocket);
            _clientSocket = null;
         }
      }
      finally
      {
         _writeLock.unlock();
      }
      
      synchronized (_users)
      {
         _users.clear();
      }
   }
   
   private void checkClosed()
   {
      if(isConnected())
      {
         throw new IllegalStateException("Already connected!");
      }
   }
   
   private void checkOpen()
   {
      if(!isConnected())
      {
         throw new IllegalStateException("Not (yet) connected!");
      }
   }

   private UserInfo findUser(String userName)
   {
      synchronized (_users)
      {
         for(UserInfo userInfo : _users)
         {
            if(userInfo.getUsername().equals(userName))
            {
               return userInfo;
            }
         }
      }
      
      return null;
   }

   private class ReceiveThread extends Thread
   {
      @Override
      public void run()
      {
         try
         {
            runInternal();
         }
         catch (Exception e)
         {
         }
         finally
         {
            connectionClosed();
            closeAndCleanupConnection();
         }
      }
   
      private void runInternal() throws IOException, ClassNotFoundException
      {
         while(!Thread.interrupted())
         {
            final Object input;
            _readLock.lock();
            try
            {
               input = _input.readObject();
            }
            finally
            {
               _readLock.unlock();
            }
            
            if(input instanceof Exit)
            {
               break;
            }
            else if(input instanceof UserMessage)
            {
               messageReceived((UserMessage)input);
            }
            else if(input instanceof ChannelInfo)
            {
               final ChannelInfo channelInfo = (ChannelInfo) input;
               final Type infoType = channelInfo.getType();
               final UserInfo userInfo = channelInfo.getUserInfo();

               if(infoType == Type.JOINED)
               {
                  synchronized (_users)
                  {
                     _users.add(userInfo);
                  }
                  userJoined(userInfo);
               }
               else if(infoType == Type.LEFT)
               {
                  synchronized (_users)
                  {
                     _users.remove(userInfo);
                  }
                  userLeft(userInfo);
               }
            }
         }
      }
      
      private void messageReceived(UserMessage message)
      {
         final MessageEvent event = createMessageEvent(message);
         
         for(ChatClientListener listener : _listeners)
         {
            listener.messageReceived(ChatClient.this, event);
         }
      }
      
      private void connectionClosed()
      {
         for(ChatClientListener listener : _listeners)
         {
            listener.connectionClosed(ChatClient.this);
         }
      }
      
      private void userJoined(UserInfo userInfo)
      {
         final UserEvent event = createUserEvent(userInfo);
         
         for(ChatClientListener listener : _listeners)
         {
            listener.userJoined(ChatClient.this, event);
         }
      }

      private void userLeft(UserInfo userInfo)
      {
         final UserEvent event = createUserEvent(userInfo);
         
         for(ChatClientListener listener : _listeners)
         {
            listener.userLeft(ChatClient.this, event);
         }
      }
   }
   
   private static UserEvent createUserEvent(UserInfo userInfo)
   {
      return new UserEvent(userInfo.getUsername(), userInfo.getColor());
   }
   
   private static MessageEvent createMessageEvent(UserMessage message)
   {
      final String messageText = message.getMessage().getMessage(); 
      final String sender = message.getUserInfo().getUsername();
      final Color color = message.getUserInfo().getColor();
      final boolean whispered = message.isWhispered();
      
      return new MessageEvent(sender, color, messageText, whispered);
   }
}
