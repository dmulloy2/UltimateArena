package net.dmulloy2.ultimatearena.types;

/**
 * @author dmulloy2
 */

public enum Permission
{
	CLASS, 
	CLASSLIST,
	CREATE, 
	DELETE,
	DISABLE,
	DISLIKE,
	ENABLE,
	FORCESTOP,
	INFO, 
	JOIN,
	JOIN_FORCE,
	KICK,
	LIKE, 
	LIST,
	PAUSE,
	RELOAD, 
	SETDONE, 
	SETPOINT,
	START,
	STATS,
	STOP,
	VERSION,

	BUILD, 
	BYPASS;

	public final String node;

	Permission()
	{
		this.node = toString().toLowerCase().replaceAll("_", ".");
	}

	public String getNode()
	{
		return this.node;
	}
}