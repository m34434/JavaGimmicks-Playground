package net.sf.javagimmicks.shopshop.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.UploadUploader;
import com.dropbox.core.v2.files.WriteMode;

import net.sf.javagimmicks.shopshop.ListItem;

public class ShopShopHelper
{
   private static final String FILE_NAME_FORMAT = "/ShopShop/%s.shopshop";

   private static final String PROP_SHOPPING_LIST = "shoppingList";
   private static final String PROP_COUNT = "count";
   private static final String PROP_NAME = "name";
   private static final String PROP_DONE = "done";
   
   private static final String SUFFIX = ".shopshop";

   public static List<String> getShoppingListNames(final DbxClientV2 dropbox) throws ListFolderErrorException, DbxException
   {
      final List<String> result = new ArrayList<>();
      
      ListFolderResult folders = dropbox.files().listFolder("/ShopShop/");
      while (true)
      {
         for (Metadata metadata : folders.getEntries())
         {
            final String fileName = metadata.getName();
            
            if(fileName.endsWith(SUFFIX))
            {
               result.add(fileName.substring(0, fileName.length() - SUFFIX.length()));
            }
         }

         if (!folders.getHasMore())
         {
            break;
         }

         folders = dropbox.files().listFolderContinue(folders.getCursor());
      }
      
      return result;
   }

   public static List<String> getShoppingListNames(String dropboxAccessToken) throws ListFolderErrorException, DbxException
   {
      return getShoppingListNames(new DbxClientV2(new DbxRequestConfig("ShopShopClient"), dropboxAccessToken));
   }

   public static NSDictionary getShopShopList(final DbxClientV2 dropbox, String shoppingListName)
         throws IOException, PropertyListFormatException, DownloadErrorException, DbxException
   {
      final NSDictionary shoppingList;
      try (InputStream is = dropbox.files().download(String.format(FILE_NAME_FORMAT, shoppingListName))
            .getInputStream())
      {
         shoppingList = (NSDictionary) BinaryPropertyListParser.parse(is);
      }
      return shoppingList;
   }

   public static void putShoppingList(final DbxClientV2 dropbox, String shoppingListName, NSDictionary shoppingList)
         throws UploadErrorException, DbxException, IOException
   {
      try (final UploadUploader upload = dropbox.files()
            .uploadBuilder(String.format(FILE_NAME_FORMAT, shoppingListName)).withMode(WriteMode.OVERWRITE).start())
      {
         try (OutputStream os = upload.getOutputStream())
         {
            BinaryPropertyListWriter.write(os, shoppingList);
         }
         upload.finish();
      }
   }

   public static List<ListItem> getItems(NSDictionary shoppingList)
   {
      final NSArray itemList = (NSArray) shoppingList.get(PROP_SHOPPING_LIST);

      final List<ListItem> result = new ArrayList<>(itemList.count());

      for (NSObject itemRaw : itemList.getArray())
      {
         final NSDictionary item = (NSDictionary) itemRaw;

         final String count = item.get(PROP_COUNT).toString();
         final String name = item.get(PROP_NAME).toString();
         final boolean done = Boolean.parseBoolean(item.get(PROP_DONE).toString());

         result.add(new ListItem(count, name, done));
      }

      return result;
   }

   public static void addItem(NSDictionary shoppingList, ListItem item)
   {
      final NSArray itemList = (NSArray) shoppingList.get(PROP_SHOPPING_LIST);

      final NSDictionary newItem = new NSDictionary();
      newItem.put(PROP_COUNT, item.getCount());
      newItem.put(PROP_NAME, item.getName());
      newItem.put(PROP_DONE, item.isDone());

      shoppingList.put(PROP_SHOPPING_LIST, PListHelper.add(itemList, newItem));
   }

   public static void addItem(DbxClientV2 dropbox, String shoppingListName, ListItem item)
         throws DownloadErrorException, IOException, PropertyListFormatException, DbxException
   {
      final NSDictionary shoppingList = getShopShopList(dropbox, shoppingListName);
      addItem(shoppingList, item);
      putShoppingList(dropbox, shoppingListName, shoppingList);
   }
}
