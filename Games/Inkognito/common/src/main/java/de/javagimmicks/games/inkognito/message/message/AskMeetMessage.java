package de.javagimmicks.games.inkognito.message.message;

import de.javagimmicks.games.inkognito.message.UnexpectedAnswerException;
import de.javagimmicks.games.inkognito.message.answer.NameAnswer;
import de.javagimmicks.games.inkognito.model.Person;

public class AskMeetMessage implements AnsweredMessage<NameAnswer>
{
	public static final AskMeetMessage INSTANCE = new AskMeetMessage();
	
	private AskMeetMessage() { }
	
	public String serialize()
	{
		return SIG_ASK_MEET;
	}

	public NameAnswer parseAnswer(String sAnswer) throws UnexpectedAnswerException
	{
	   try
	   {
	      return new NameAnswer(Person.valueOf(sAnswer));
	   }
	   catch(IllegalArgumentException | NullPointerException e)
	   {
	      throw new UnexpectedAnswerException(e);
	   }
	}
	
}
