package net.dmulloy2.ultimatearena.api;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author dmulloy2
 */

public class ArenaLogger extends Logger
{
	private final String prefix;
	public ArenaLogger(ArenaType type)
	{
		super(type.getClass().getCanonicalName(), null);
		prefix = "[UltimateArena] [" + type.getStylizedName() + "] ";
		setLevel(Level.ALL);
	}

	@Override
	public void log(LogRecord logRecord)
	{
		logRecord.setMessage(prefix + logRecord.getMessage());
		super.log(logRecord);
	}
}