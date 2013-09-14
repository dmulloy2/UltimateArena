package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.flags.CTFFlagBase;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author dmulloy2
 */

public class CTFArena extends Arena
{
	public CTFFlagBase flagred;
	public CTFFlagBase flagblue;
	public int redcap;
	public int bluecap;
	private BukkitTask moveTask;
	private String lastcap;

	public CTFArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.CTF;
		this.startTimer = 120;
		this.maxGameTime = 60 * 15;
		this.maxDeaths = 990;

		this.flagred = new CTFFlagBase(this, az.getFlags().get(0), 1, plugin);
		this.flagblue = new CTFFlagBase(this, az.getFlags().get(1), 2, plugin);

		flagred.initialize();
		flagblue.initialize();

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

		if (redcap >= 3 || bluecap >= 3)
		{
			this.setWinningTeam(1);
			this.lastcap = "&cRED";

			if (bluecap >= 3)
			{
				this.setWinningTeam(2);
				this.lastcap = "&9BLUE";
			}

			winGame();
		}
		else
		{
			flagred.getFlag().tick();
			flagblue.getFlag().tick();
		}
	}

	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}

	public void winGame()
	{
		if (redcap >= 3 && bluecap >= 3)
		{
			setWinningTeam(-1);

			stop();

			rewardTeam(-1, true);
			return;
		}

		stop();

		rewardTeam(winningTeam, false);
	}

	@Override
	public void onStop()
	{
		flagred.getFlag().setStopped(true);
		flagblue.getFlag().setStopped(true);

		flagred.getFlag().getReturnto().getBlock().setType(Material.AIR);
		flagblue.getFlag().getReturnto().getBlock().setType(Material.AIR);
		flagred.getFlag().despawn();
		flagblue.getFlag().despawn();

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
				flagred.checkNear(arenaPlayers);
				flagblue.checkNear(arenaPlayers);
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
		if (winningTeam == -1)
		{
			tellPlayers("&3Game ended in a tie! Half prize to everyone!");
		}
		else
		{
			if (lastcap != null)
				tellPlayers("&e{0} &3team won the game!", lastcap);
		}
	}
}