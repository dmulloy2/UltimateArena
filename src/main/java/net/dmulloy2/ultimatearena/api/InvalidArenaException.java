package net.dmulloy2.ultimatearena.api;

/**
 * Represents an Exception thrown when loading an ArenaType
 *
 * @author dmulloy2
 */

public class InvalidArenaException extends Exception
{
	private static final long serialVersionUID = 5046253080764309104L;

	/**
	 * Constructs an empty InvalidArenaException.
	 */
	public InvalidArenaException()
	{
		//
	}

	/**
	 * Constructs an InvalidArenaException with a given message.
	 */
	public InvalidArenaException(String message)
	{
		super(message);
	}

	/**
	 * Constructs an InvalidArenaException with a given cause.
	 */
	public InvalidArenaException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Constructs an InvalidArenaException with a given message and cause.
	 */
	public InvalidArenaException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public static final InvalidArenaException fromThrowable(Throwable ex)
	{
		if (ex instanceof InvalidArenaException)
			return (InvalidArenaException) ex;

		return new InvalidArenaException(ex);
	}
}