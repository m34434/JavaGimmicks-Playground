package net.sf.javagimmicks.shopshop;

public class ListItem
{
   private final String count;
   private final String name;
   private final boolean done;

   public ListItem(String count, String name, boolean done)
   {
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("Item name must not be null or empty!");
      }

      if (count == null)
      {
         count = "";
      }

      this.count = count;
      this.name = name;
      this.done = done;
   }

   public ListItem(String name, boolean done)
   {
      this("", name, done);
   }

   public ListItem(String name)
   {
      this(name, false);
   }

   public ListItem(String count, String name)
   {
      this(count, name, false);
   }

   public String getCount()
   {
      return count;
   }

   public String getName()
   {
      return name;
   }

   public boolean isDone()
   {
      return done;
   }

   @Override
   public String toString()
   {
      return "ListItem [count=" + count + ", name=" + name + ", done=" + done + "]";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((count == null) ? 0 : count.hashCode());
      result = prime * result + (done ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ListItem other = (ListItem) obj;
      if (count == null)
      {
         if (other.count != null) return false;
      }
      else if (!count.equals(other.count)) return false;
      if (done != other.done) return false;
      if (name == null)
      {
         if (other.name != null) return false;
      }
      else if (!name.equals(other.name)) return false;
      return true;
   }

}
