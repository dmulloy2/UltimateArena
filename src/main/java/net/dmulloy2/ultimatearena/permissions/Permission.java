package net.dmulloy2.ultimatearena.permissions;

/**
 * @author dmulloy2
 */

public enum Permission
{
	CMD_CREATE("create"),
	CMD_DELETE("delete"),
	CMD_DISABLE("disable"),
	CMD_ENABLE("enable"),
	CMD_FORCE_STOP("forcestop"),
	CMD_KICK("kick"),
	CMD_PAUSE("pause"),
	CMD_RELOAD("reload"),
	CMD_SET_DONE("setdone"),
	CMD_SET_POINT("setpoint"),
	CMD_START("start"),
	CMD_STOP("stop"),
	BUILD("build"),
	FORCE_JOIN("forcejoin"),
	JOIN("join"),
	COMMAND_BYPASS("commandbypass");
	
	public final String node;
	Permission(final String node) 
	{
		this.node = node;
	}
	
	public String getNode()
	{
		return this.node;
	}
}