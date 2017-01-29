package net.sf.javagimmicks.shopshop;


import org.junit.Before;
import org.junit.Test;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;

import net.sf.javagimmicks.shopshop.util.ShopShopHelper;

public class ShopShopClientTest
{
   // Get your app key and secret from the Dropbox developers website.
//   private static final String APP_KEY = "3907t43g1r9b9nv";
//   private static final String APP_SECRET = "6c62wub4p6ogoa1";
   
   private static final String ACCESS_TOKEN = "qXJwL9Tmtg0AAAAAAAACS8UmKU93lEUs5_GxjgRkKK0RFeJ0CNzra1a7g3jD660L";

   private ShopShopClient shopShopClient;
   
   @Before
   public void setup() throws ShopShopClientException
   {
      shopShopClient = new ShopShopClient(ACCESS_TOKEN, "Einkaufsliste");
   }
   
   @Test
   public void test() throws ShopShopClientException, DbxApiException, DbxException
   {
      System.out.println("Linked account: " + shopShopClient.dropbox.users().getCurrentAccount().getName().getDisplayName());

      for(ListItem i : shopShopClient.getItems())
      {
         System.out.println(i);
      }
      
//      shopShopClient.addItem(new ListItem("Zufall" + System.currentTimeMillis()));
//
//      shopShopClient.save();
   }

   @Test
   public void testGetListNames() throws ShopShopClientException, DbxApiException, DbxException
   {
      System.out.println("Linked account: " + shopShopClient.dropbox.users().getCurrentAccount().getName().getDisplayName());

      System.out.println(ShopShopHelper.getShoppingListNames(shopShopClient.dropbox));
   }
}