package de.javagimmicks.games.inkognito.server;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;

import de.javagimmicks.games.inkognito.server.net.NetworkServer;

public class Logging
{
    public static void setup()
    {
        new File("log").mkdirs();
        DOMConfigurator.configure(NetworkServer.class.getClassLoader().getResource("log4j.xml"));
    }
}
