package net.sf.javagimmicks.shopshop;

public class ShopShopClientException extends Exception
{
   private static final long serialVersionUID = 1L;

   public ShopShopClientException()
   {}

   public ShopShopClientException(String message)
   {
      super(message);
   }

   public ShopShopClientException(Throwable cause)
   {
      super(cause);
   }

   public ShopShopClientException(String message, Throwable cause)
   {
      super(message, cause);
   }

   protected ShopShopClientException(String message, Throwable cause, boolean enableSuppression,
         boolean writableStackTrace)
   {
      super(message, cause, enableSuppression, writableStackTrace);
   }

}
