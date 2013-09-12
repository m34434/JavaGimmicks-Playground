package de.javagimmicks.games.inkognito.client.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NetworkClient
{
	public static void joinNetworkGame(String host, int port, NetworkPlayer player) throws IOException
	{
		new NetworkClient(host, port).joinNetworkGame(player);
	}
	
    public NetworkClient()
    {
        this(null, 6201);
    }
    
    public NetworkClient(String host, int port)
    {
    	this.host = host;
    	this.port = port;
    }
    
    public void joinNetworkGame(NetworkPlayer player) throws IOException
    {
        // Logdatei oeffnen, wenn moeglich; ansonsten bleibt es bei log == null.
        try
        {
        	new File("log").mkdir();
            log = new PrintStream(new FileOutputStream("log/inkognito-"
                + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())
                + "." + host
                + "." + port
                + ".log"));
        }
        catch(IOException ex)
        {
            System.err.println("Cannot write log file - logging disabled");
            log = null;
        }
        
        // Startmeldung in die Logdatei.
        log("log start");
        
        // Kommunikation mit dem Moderator
        try
        {
            log("connecting to host " + host + " on port " + port);

            // Serverport oeffnen
            Socket connection = new Socket(host, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            
            log("connected!");

            // Requestschleife
            while(true)
            {
                // Anfrage lesen
                String request = input.readLine();
                
                // Bei 'exit' abbrechen; der Moderator schliesst die Verbindung
                if("exit".equals(request))
                {
                    log("moderator terminates");
                    
                    input.close();
                    output.close();
                    connection.close();
                    
                    // Ende der Requestschleife
                    break;
                }
                
                // Wenn Text gekommen ist: verarbeiten
                if(request != null && request.length() > 0)
                {
                    log("-> " + request);
                    
                    // Nachricht beantworten
                    String response = player.process(request);
                    
                    // Nicht-leere Antworten protokollen
                    if(response != null && response.length() > 0)
                    {
                        log("<- " + response);

                        output.println(response);
                        output.flush();
                    }
                }
            }
        }
        catch(IOException ex)
        {
            // da ging was schief mit der Netzwerkkommuniaktion
            log("IOException: " + ex.getMessage());
            throw ex;
        }
        finally
        {
            // Protokoll schliessen
            log("log end");
            if(log != null)
            {
                log.close();
            }
        }
    }
    
    /** Fuegt den String 'msg' samt Zeitmarke an die Protokolldatei an.
      * @param msg Text der an die Protokolldatei angefuegt wird.
      */
    private void log(String msg)
    {
        if(log == null)
            return;
            
        log.print(dateFormat.format(new Date()));
        log.print(" ");
        log.println(msg);
        log.flush();
    }

    private final String host;
    private final int port;
    
    /** Logdatei. */
    private static PrintStream log = null;
    
    /** Ausgabeformat fuer Zeitmarken in der Logdatei. */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
