package net.sf.javagimmicks.shopshop.util;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;

public class PListHelper
{
   public static NSArray add(NSArray base, NSObject item)
   {
      final NSArray result = new NSArray(base.count() + 1);
      
      System.arraycopy(base.getArray(), 0, result.getArray(), 0, base.count());
      result.setValue(base.count(), item);
      
      return result;
   }
}