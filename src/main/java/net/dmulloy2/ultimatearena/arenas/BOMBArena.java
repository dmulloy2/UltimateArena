package net.dmulloy2.ultimatearena.arenas;

import lombok.Getter;
import net.dmulloy2.ultimatearena.flags.BombFlag;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

/**
 * @author dmulloy2
 */

@Getter
public class BOMBArena extends Arena
{
	private int redTeamPower;

	private BombFlag bomb1;
	private BombFlag bomb2;

	public BOMBArena(ArenaZone az)
	{
		super(az);
		this.type = FieldType.BOMB;

		this.redTeamPower = 1;

		this.bomb1 = new BombFlag(this, az.getFlags().get(0), plugin);
		this.bomb1.setBombNumber(1);

		this.bomb2 = new BombFlag(this, az.getFlags().get(1), plugin);
		this.bomb2.setBombNumber(2);
	}

	@Override
	public void onStart()
	{
		this.redTeamPower = active.size() * 3;
		if (redTeamPower < 10)
		{
			this.redTeamPower = 10;
		}
		if (redTeamPower > 150)
		{
			this.redTeamPower = 150;
		}
	}

	@Override
	public void onOutOfTime()
	{
		setWinningTeam(2);
		rewardTeam(winningTeam);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		if (pl.getTeam() == 1)
		{
			redTeamPower--;
			for (ArenaPlayer ap : active)
			{
				if (ap.getTeam() == 1)
				{
					ap.sendMessage("&cYour power is now: &6" + redTeamPower);
				}
				else
				{
					ap.sendMessage("&cThe other team's power is now: &6" + redTeamPower);
				}
			}
		}
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			if (! simpleTeamCheck())
			{
				tellPlayers("&3One team is empty! Game ended!");

				stop();
				return;
			}
		}

		bomb1.checkNear(getActivePlayers());
		bomb2.checkNear(getActivePlayers());

		if (bomb1.isExploded() && bomb2.isExploded())
		{
			setWinningTeam(1);

			stop();

			rewardTeam(1);
			return;
		}

		if (redTeamPower <= 0)
		{
			setWinningTeam(2);

			stop();

			rewardTeam(2);
			return;
		}
	}
}