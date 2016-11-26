package de.javagimmicks.games.inkognito.message;

public class UnexpectedAnswerException extends IllegalArgumentException
{
	private static final long serialVersionUID = 7583139285057666894L;

	public UnexpectedAnswerException()
	{
		super();
	}

	public UnexpectedAnswerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnexpectedAnswerException(String message)
	{
		super(message);
	}

	public UnexpectedAnswerException(Throwable cause)
	{
		super(cause);
	}

}
