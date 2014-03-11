/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.api;

/**
 * @author dmulloy2
 */

public class InvalidArenaException extends Exception
{
	private static final long serialVersionUID = 5046253080764309104L;

	public InvalidArenaException()
	{
		//
	}

	public InvalidArenaException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidArenaException(Throwable cause)
	{
		super(cause);
	}

	public InvalidArenaException(String message)
	{
		super(message);
	}
}