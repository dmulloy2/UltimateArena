package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import net.dmulloy2.types.IPermission;

/**
 * @author dmulloy2
 */

@Getter
public enum Permission implements IPermission
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
	OPTION,
	PAUSE,
	RELOAD,
	SETPOINT,
	SPECTATE,
	START,
	STATS,
	STOP,
	UNDO,
	VERSION,

	BUILD,
	BYPASS;

	private final String node;
	private Permission()
	{
		this.node = toString().toLowerCase().replaceAll("_", ".");
	}
}