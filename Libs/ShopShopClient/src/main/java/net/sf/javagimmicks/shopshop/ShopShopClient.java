package net.sf.javagimmicks.shopshop;

import java.io.IOException;
import java.util.List;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import net.sf.javagimmicks.shopshop.util.ShopShopHelper;

public class ShopShopClient
{
   final DbxClientV2 dropbox;
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
   
   public String getShoppingListName()
   {
      return shoppingListName;
   }

   public void addItem(ListItem item)
   {
      ShopShopHelper.addItem(shoppingList, item);
   }
   
   public List<ListItem> getItems()
   {
      return ShopShopHelper.getItems(shoppingList);
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
