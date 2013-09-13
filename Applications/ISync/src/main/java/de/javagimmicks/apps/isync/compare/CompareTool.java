package de.javagimmicks.apps.isync.compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CompareTool
{
   public static final String FILE_NAME = "compare.properties";
   
   private static CompareTool INSTANCE;
   private static long TSTMP;
   
   private final String _executable;
   private final String _workingDir;
   private final String _args;
   
   private CompareTool(String executable, String workingDir, String args)
   {
      _executable = executable;
      _workingDir = workingDir;
      _args = args;
   }
   
   public static synchronized CompareTool getInstance() throws IOException
   {
      final File compareSettingsFile = getSettingsFile();
      
      if(INSTANCE == null || compareSettingsFile.lastModified() > TSTMP)
      {
         createInstance(compareSettingsFile);
      }
      
      return INSTANCE;
   }
   
   public static File getSettingsFile()
   {
      File result = new File(new File(new File(System.getProperty("user.home")), ".isync"), FILE_NAME);
      if(result.exists() && result.isFile())
      {
         return result;
      }
      
      return new File(FILE_NAME);
   }

   public void execute(File sourceFile, File targetFile) throws IOException
   {
      final String sourcePath = sourceFile.getAbsolutePath();
      final String targetPath = targetFile.getAbsolutePath();
      
      
      final String args = _args
         .replaceAll("\\$\\{left}", sourcePath.replaceAll("\\\\", "\\\\\\\\"))
         .replaceAll("\\$\\{right}", targetPath.replaceAll("\\\\", "\\\\\\\\"));
      
      final String cmd = new StringBuilder()
         .append(_executable)
         .append(" ")
         .append(args)
         .toString();
      
      final File workingDir = (_workingDir != null && _workingDir.length() > 0) ? new File(_workingDir) : null;
      
      Runtime.getRuntime().exec(cmd, null, workingDir);
   }
   
   private static void createInstance(File compareSettingsFile) throws IOException
   {
      final Properties settings = new Properties();
      final FileInputStream settingsInputStream = new FileInputStream(compareSettingsFile);
      
      try
      {
         settings.load(settingsInputStream);
      }
      finally
      {
         settingsInputStream.close();
      }
      
      final String executable = settings.getProperty("executable");
      final String workingDir = settings.getProperty("workingDir");
      final String args = settings.getProperty("args");
      
      INSTANCE = new CompareTool(executable, workingDir, args);
   }
}
