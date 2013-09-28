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
		this.startTimer = 120;
		this.maxGameTime = 60 * 15;
		this.maxDeaths = 990;

		bomb1 = new BombFlag(this, az.getFlags().get(0), plugin);
		bomb2 = new BombFlag(this, az.getFlags().get(1), plugin);
		bomb1.setBombNumber(1);
		bomb2.setBombNumber(2);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		this.redTeamPower = getActivePlayers() * 3;
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
		rewardTeam(winningTeam, false);
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		if (pl.getTeam() == 1)
		{
			redTeamPower--;
			for (int i = 0; i < arenaPlayers.size(); i++)
			{
				ArenaPlayer apl = arenaPlayers.get(i);
				if (checkValid(apl))
				{
					if (apl.getTeam() == 1)
					{
						apl.sendMessage("&cYour power is now: &6" + redTeamPower);
					}
					else
					{
						apl.sendMessage("&cThe other team's power is now: &6" + redTeamPower);
					}
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
			simpleTeamCheck(true);
		}

		bomb1.checkNear(arenaPlayers);
		bomb2.checkNear(arenaPlayers);

		if (bomb1.isExploded() && bomb2.isExploded())
		{
			setWinningTeam(1);

			stop();

			rewardTeam(1, false);
			return;
		}

		if (redTeamPower <= 0)
		{
			setWinningTeam(2);

			stop();

			rewardTeam(2, false);
			return;
		}
	}
}