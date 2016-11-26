package net.sf.javagimmicks.shopshop;

import java.io.IOException;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import net.sf.javagimmicks.shopshop.util.ShopShopHelper;

public class ShopShopClient
{
   private final DbxClientV2 dropbox;
   private final String shoppingListName;
   private final NSDictionary shoppingList;
   
   public ShopShopClient(DbxClientV2 dropbox, String shoppingListName) throws ShopShopClientException
   {
      this.dropbox = dropbox;
      this.shoppingListName = shoppingListName;
      try
      {
         this.shoppingList = ShopShopHelper.getShopShopList(dropbox, shoppingListName);
      }
      catch (IOException | PropertyListFormatException | DbxException e)
      {
         throw new ShopShopClientException("Could not load shopping list from Dropbox!", e);
      }
   }
   
   public ShopShopClient(String dropboxAccessToken, String shoppingListName) throws ShopShopClientException
   {
      this(new DbxClientV2(new DbxRequestConfig("ShopShopClient"), dropboxAccessToken), shoppingListName);
   }
   
   public DbxClientV2 getDropboxClient()
   {
      return dropbox;
   }
   
   public void addItem(String count, String item)
   {
      ShopShopHelper.addItem(shoppingList, count, item);
   }
   
   public void addItem(String item)
   {
      ShopShopHelper.addItem(shoppingList, item);
   }
   
   public void getItems()
   {
      final NSArray itemList = (NSArray) shoppingList.get("shoppingList");

      for(NSObject itemRaw : itemList.getArray())
      {
         final NSDictionary item = (NSDictionary)itemRaw;
         
         System.out.println(item.get("name") + " / " + item.get("done"));
      }

   }
   
   public void save() throws ShopShopClientException
   {
      try
      {
         ShopShopHelper.putShoppingList(dropbox, shoppingListName, shoppingList);
      }
      catch (DbxException | IOException e)
      {
         throw new ShopShopClientException("Could not save shopping list to Dropbox!", e);
      }
   }
}
