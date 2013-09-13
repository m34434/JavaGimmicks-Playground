package de.javagimmicks.apps.isync;

import java.io.File;

import javax.swing.JTextField;

import net.sf.javagimmicks.io.folderdiff.FileInfo;
import net.sf.javagimmicks.io.folderdiff.FolderDiffListener;
import net.sf.javagimmicks.swing.BoundedEventQueue;

class StatusFolderDiffListener implements FolderDiffListener
{
   private final JTextField _txtStatus;
   private final BoundedEventQueue _eventQueue = new BoundedEventQueue(100);

   public StatusFolderDiffListener(JTextField status)
   {
      _txtStatus = status;
      _eventQueue.startWorking();
   }
   
   public void reset()
   {
      _eventQueue.stopWorking();
      _eventQueue.startWorking();
   }

   public void fileInfosCompared(FileInfo fileInfo1, FileInfo fileInfo2)
   {
      setText(new StringBuilder()
      .append("Comparing files '")
      .append(fileInfo1.getOriginalFile().getAbsolutePath())
      .append("' and '")
      .append(fileInfo2.getOriginalFile().getAbsolutePath())
      .append("'"));
      
   }

   public void folderScanned(File folder)
   {
      setText(new StringBuilder()
      .append("Scanning folder '")
      .append(folder.getAbsolutePath())
      .append("'"));
   }
   
   private void setText(final StringBuilder text)
   {
      _eventQueue.invoke(new Runnable()
      {
         public void run()
         {
            _txtStatus.setText(text.toString());
            _txtStatus.setCaretPosition(0);
         }
      });
   }
}
