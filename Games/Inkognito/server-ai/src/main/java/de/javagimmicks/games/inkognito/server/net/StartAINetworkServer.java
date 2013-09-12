package de.javagimmicks.games.inkognito.server.net;

import de.javagimmicks.games.inkognito.server.processor.ai.CrazyAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.NormalAIMessageProcessor;
import de.javagimmicks.games.inkognito.server.processor.ai.SmartAIMessageProcessor;

public class StartAINetworkServer
{
    private static final int DEFAULT_PORT = 6201;
    
    public static void main(String[] args) throws IllegalArgumentException, SecurityException, NoSuchMethodException
    {
        final int iPort = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        
        new NetworkServer(iPort, CrazyAIMessageProcessor.class, NormalAIMessageProcessor.class, SmartAIMessageProcessor.class).start();
    }
}
