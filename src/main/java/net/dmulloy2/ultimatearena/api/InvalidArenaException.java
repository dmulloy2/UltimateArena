/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
