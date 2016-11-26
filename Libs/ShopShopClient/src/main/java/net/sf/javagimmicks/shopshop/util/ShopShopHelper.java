package net.sf.javagimmicks.shopshop.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.UploadUploader;
import com.dropbox.core.v2.files.WriteMode;

public class ShopShopHelper
{
   private static final String FILE_NAME_FORMAT = "/ShopShop/%s.shopshop";

   public static NSDictionary getShopShopList(final DbxClientV2 dropbox, String shoppingListName)
         throws IOException, PropertyListFormatException, DownloadErrorException, DbxException
   {
      final NSDictionary shoppingList;
      try(InputStream is = dropbox.files().download(String.format(FILE_NAME_FORMAT, shoppingListName)).getInputStream())
      {
         shoppingList = (NSDictionary) BinaryPropertyListParser.parse(is);
      }
      return shoppingList;
   }

   public static void putShoppingList(final DbxClientV2 dropbox, String shoppingListName, NSDictionary shoppingList) throws UploadErrorException, DbxException, IOException
   {
      try(final UploadUploader upload = dropbox.files().uploadBuilder(String.format(FILE_NAME_FORMAT, shoppingListName)).withMode(WriteMode.OVERWRITE).start())
      {
         try(OutputStream os = upload.getOutputStream())
         {
            BinaryPropertyListWriter.write(os, shoppingList);
         }
         upload.finish();
      }
   }

   public static void addItem(NSDictionary shoppingList, String count, String item)
   {
      final NSArray itemList = (NSArray) shoppingList.get("shoppingList");

      final NSDictionary newItem = new NSDictionary();
      newItem.put("count", count);
      newItem.put("name", item);
      newItem.put("done", false);
      
      shoppingList.put("shoppingList", PListHelper.add(itemList, newItem));
   }

   public static void addItem(NSDictionary shoppingList, String item)
   {
      addItem(shoppingList, "", item);
   }
   
   public static void addItem(DbxClientV2 dropbox, String shoppingListName, String count, String item) throws DownloadErrorException, IOException, PropertyListFormatException, DbxException
   {
      final NSDictionary shoppingList = getShopShopList(dropbox, shoppingListName);
      addItem(shoppingList, count, item);
      putShoppingList(dropbox, shoppingListName, shoppingList);
   }

   public static void addItem(DbxClientV2 dropbox, String shoppingListName, String item) throws DownloadErrorException, IOException, PropertyListFormatException, DbxException
   {
      addItem(dropbox, shoppingListName, "", item);
   }
}
