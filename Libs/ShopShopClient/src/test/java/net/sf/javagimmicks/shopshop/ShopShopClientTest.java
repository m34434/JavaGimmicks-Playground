package net.sf.javagimmicks.shopshop;


import org.junit.Before;
import org.junit.Test;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;

public class ShopShopClientTest
{
   // Get your app key and secret from the Dropbox developers website.
   private static final String APP_KEY = "3907t43g1r9b9nv";
   private static final String APP_SECRET = "6c62wub4p6ogoa1";
   
   private static final String ACCESS_TOKEN = "qXJwL9Tmtg0AAAAAAAACPwAB9k-IaPCoZiT_-XTDIfII-Pt4Hd0skWQD5vXeIEyW";

   private ShopShopClient shopShopClient;
   
   @Before
   public void setup() throws ShopShopClientException
   {
      shopShopClient = new ShopShopClient(ACCESS_TOKEN, "Einkaufsliste");
   }
   
   @Test
   public void test() throws ShopShopClientException, DbxApiException, DbxException
   {
      System.out.println("Linked account: " + shopShopClient.getDropboxClient().users().getCurrentAccount().getName());

      shopShopClient.addItem("Zufall" + System.currentTimeMillis());

      shopShopClient.save();
   }
}