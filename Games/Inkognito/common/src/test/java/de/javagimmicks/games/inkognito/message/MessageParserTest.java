package de.javagimmicks.games.inkognito.message;

import static de.javagimmicks.games.inkognito.message.MessageParser.parseMessage;
import static org.junit.Assert.fail;

import java.util.Arrays;


import net.sf.javagimmicks.collections.CollectionUtils;

import org.junit.Assert;
import org.junit.Test;

import de.javagimmicks.games.inkognito.message.message.Message;
import de.javagimmicks.games.inkognito.message.message.ReportMoveMessage;
import de.javagimmicks.games.inkognito.model.Location;

public class MessageParserTest
{
    private static final String USER = "Michael";

    @Test
    public void testMessageParser()
    {
        Message message;
        try
        {
            message = parseMessage("fake!");
            fail(IllegalArgumentException.class.getName() + " expected!");
        }
        catch(IllegalArgumentException ignore) {}
        
        message = parseMessageParts("moveto!", USER, Location.Rialto);
        Assert.assertTrue("Wrong message type!", message instanceof ReportMoveMessage);
        Assert.assertEquals(USER, ((ReportMoveMessage)message).getPersonName());
        Assert.assertSame(Location.Rialto, ((ReportMoveMessage)message).getLocation());
        
        // TODO: add other message types
    }
    
    private static Message parseMessageParts(Object... elements)
    {
        return parseMessage(CollectionUtils.concatElements(Arrays.asList(elements), " ").toString());
    }
}
