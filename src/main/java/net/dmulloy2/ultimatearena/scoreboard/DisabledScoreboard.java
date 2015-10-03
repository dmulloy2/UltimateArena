/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.scoreboard;

/**
 * @author dmulloy2
 */

public class DisabledScoreboard implements ArenaScoreboard
{
	private static final DisabledScoreboard instance = new DisabledScoreboard();

	public static DisabledScoreboard getInstance()
	{
		return instance;
	}

	@Override
	public void update()
	{

	}

	@Override
	public void dispose()
	{

	}

	@Override
	public boolean isEnabled()
	{
		return false;
	}
}