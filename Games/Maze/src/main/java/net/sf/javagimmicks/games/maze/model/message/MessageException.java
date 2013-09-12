package net.sf.javagimmicks.games.maze.model.message;

public class MessageException extends Exception
{
	private static final long serialVersionUID = 683151477496707251L;

	public MessageException()
	{
	}

	public MessageException(String message)
	{
		super(message);
	}

	public MessageException(Throwable cause)
	{
		super(cause);
	}

	public MessageException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
