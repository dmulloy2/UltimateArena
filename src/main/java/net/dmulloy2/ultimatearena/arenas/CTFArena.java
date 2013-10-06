package net.dmulloy2.ultimatearena.arenas;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.flags.CTFFlagBase;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author dmulloy2
 */

@Getter
@Setter
public class CTFArena extends Arena
{
	private CTFFlagBase redFlag;
	private CTFFlagBase blueFlag;
	
	private int redCap, blueCap;

	private BukkitTask moveTask;
	private String lastcap;

	public CTFArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.CTF;
		this.startTimer = 120;
		this.maxGameTime = 60 * 15;
		this.maxDeaths = 990;

		this.redFlag = new CTFFlagBase(this, az.getFlags().get(0), 1, plugin);
		this.blueFlag = new CTFFlagBase(this, az.getFlags().get(1), 2, plugin);

		redFlag.initialize();
		blueFlag.initialize();

		this.moveTask = new ExecuteMove().runTaskTimer(plugin, 12, 1);
	}

	@Override
	public void check()
	{
		if (startTimer <= 0)
		{
			if (!simpleTeamCheck(false))
			{
				tellPlayers("&3One team is empty! game ended!");

				stop();
			}
			else
			{
				if (getStartingAmount() <= 1)
				{
					tellPlayers("&3Not enough people to play!");

					stop();
				}
			}
		}

		if (redCap >= 3 || blueCap >= 3)
		{
			setWinningTeam(1);
			
			this.lastcap = "&cRED";

			if (blueCap >= 3)
			{
				this.lastcap = "&9BLUE";

				setWinningTeam(2);
			}

			winGame();
		}
		else
		{
			redFlag.getFlag().tick();
			blueFlag.getFlag().tick();
		}
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}

	public void winGame()
	{
		if (redCap >= 3 && blueCap >= 3)
		{
			setWinningTeam(-1);

			stop();

			rewardTeam(-1);
			return;
		}

		stop();

		rewardTeam(winningTeam);
	}

	@Override
	public void onStop()
	{
		redFlag.getFlag().setStopped(true);
		blueFlag.getFlag().setStopped(true);

		redFlag.getFlag().getReturnto().getBlock().setType(Material.AIR);
		blueFlag.getFlag().getReturnto().getBlock().setType(Material.AIR);
		redFlag.getFlag().despawn();
		blueFlag.getFlag().despawn();

		moveTask.cancel();
		moveTask = null;
	}

	public class ExecuteMove extends BukkitRunnable
	{
		@Override
		public void run()
		{
			if (!isStopped())
			{
				redFlag.checkNear(arenaPlayers);
				blueFlag.checkNear(arenaPlayers);
			}
			else
			{
				onStop();
			}
		}
	}

	@Override
	public void announceWinner()
	{
		if (lastcap != null)
			tellPlayers("&e{0} &3team won the game!", lastcap);
	}
}