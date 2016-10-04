package de.javagimmicks.games.inkognito.message;

import static de.javagimmicks.games.inkognito.message.MessageParser.parseMessage;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.javagimmicks.games.inkognito.message.message.Message;
import de.javagimmicks.games.inkognito.message.message.ReportMoveMessage;
import de.javagimmicks.games.inkognito.model.Location;
import de.javagimmicks.games.inkognito.model.Person;
import net.sf.javagimmicks.collections.CollectionUtils;

public class MessageParserTest
{
    private static final Person USER = Person.Player1;

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
        Assert.assertEquals(USER, ((ReportMoveMessage)message).getPerson());
        Assert.assertSame(Location.Rialto, ((ReportMoveMessage)message).getLocation());
        
        // TODO: add other message types
    }
    
    private static Message parseMessageParts(Object... elements)
    {
        return parseMessage(CollectionUtils.concatElements(Arrays.asList(elements), " ").toString());
    }
}
