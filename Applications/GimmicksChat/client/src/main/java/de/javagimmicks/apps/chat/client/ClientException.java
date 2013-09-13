package de.javagimmicks.apps.chat.client;

public class ClientException extends Exception
{
   private static final long serialVersionUID = 6670485667928172953L;

   public ClientException()
   {
   }

   public ClientException(String message)
   {
      super(message);
   }

   public ClientException(Throwable cause)
   {
      super(cause);
   }

   public ClientException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
