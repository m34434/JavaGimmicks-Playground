package de.javagimmicks.apps.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.javagimmicks.apps.chat.ChatUtils;
import de.javagimmicks.apps.chat.model.Exit;
import de.javagimmicks.apps.chat.model.Login;
import de.javagimmicks.apps.chat.model.Message;
import de.javagimmicks.apps.chat.model.UserMessage;

public class ClientConnectionThread extends Thread
{
   protected final ChatServer _server;
   protected final Socket _socket;
   
   protected final ObjectInputStream _input;
   protected final ObjectOutputStream _output;

   protected Login _login;
   
   public ClientConnectionThread(ChatServer server, Socket socket) throws IOException
   {
      _server = server;
      _socket = socket;

      _output = new ObjectOutputStream(_socket.getOutputStream());
      _input = new ObjectInputStream(_socket.getInputStream());
   }
   
   public Login getLogin()
   {
      return _login;
   }
   
   public ObjectOutputStream getOutput()
   {
      return _output;
   }
   
   public void run()
   {
      try
      {
         runInternal();
      }
      catch (IOException e)
      {
         doExit(null);
      }
      catch (ClassNotFoundException e)
      {
         doExit("Unknown object received!");
      }
      catch (ProtocolException e)
      {
         doExit(e.getMessage());
      }
      
      _server.notifyExit(this);
      
      ChatUtils.closeQuietly(_input);
      ChatUtils.closeQuietly(_output);
      ChatUtils.closeQuietly(_socket);
   }
   
   private void doExit(String message)
   {
      try
      {
         if(message != null)
         {
            sendSystemMessage(message);
         }
         
         sendExit();
      }
      catch(IOException ex)
      {
         
      }
   }

   private void runInternal() throws IOException, ClassNotFoundException, ProtocolException
   {
      while(!Thread.interrupted())
      {
         Object input = _input.readObject();
    
         if(_login == null)
         {
            doLogin(input);
         }
         else
         {
            if(input instanceof Message)
            {
               _server.notifyMessage(new UserMessage(_login.getUserInfo(), (Message)input));
            }
            else if(input instanceof UserMessage)
            {
               final UserMessage userMessage = (UserMessage)input;
               _server.notifyPrivateMessage(userMessage.getUserInfo(), this, userMessage.getMessage());
            }
            else if(input instanceof Exit)
            {
               break;
            }
            else if(input instanceof Login)
            {
               throw new ProtocolException("Login already sent!");
            }
            else
            {
               throw new ProtocolException("Unknown message sent!");
            }
         }
      }
   }

   private void doLogin(Object input) throws ProtocolException
   {
      if(!(input instanceof Login))
      {
         throw new ProtocolException("No login sent!");
      }
      else
      {
         _login = (Login)input;
         _server.checkLogin(_login);
         _server.notifyLogin(this);
      }
   }
   
   private void sendSystemMessage(String messageText) throws IOException
   {
      _output.writeObject(new UserMessage(ChatServer.SYSTEM_USER, new Message(messageText)));
   }
   
   private void sendExit() throws IOException
   {
      _output.writeObject(Exit.INSTANCE);
   }
}
