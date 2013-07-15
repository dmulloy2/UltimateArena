package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.CTFFlagBase;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CTFArena extends Arena
{
	public CTFFlagBase flagred;
	public CTFFlagBase flagblue;
	public int redcap;
	public int bluecap;
	private BukkitTask ExecuteMove;
	private String lastcap;
	
	public CTFArena(ArenaZone az)
	{
		super(az);
		
		this.type = FieldType.CTF;
		setStarttimer(120);
		setGametimer(0);
		setMaxgametime(60 * 15);
		setMaxDeaths(990);

		flagred = new CTFFlagBase(this, az.getFlags().get(0), 1, plugin);
		flagblue = new CTFFlagBase(this, az.getFlags().get(1), 2, plugin);
			
		flagred.initialize();
		flagblue.initialize();
		
		ExecuteMove = new ExecuteMove().runTaskTimer(plugin, 12, 1);
	}
	
	@Override
	public void check()
	{
		if (getStarttimer() <= 0) 
		{
			if (!simpleTeamCheck(false)) 
			{
				this.tellPlayers("&9One team is empty! game ended!");
				this.stop();
			}
			else
			{
				if (this.getAmtPlayersStartingInArena() <= 1) 
				{
					this.tellPlayers("&9Not enough people to play!");
					this.stop();
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
			this.setWinningTeam(-1);
			this.stop();
			this.rewardTeam(-1, "Tie! Half prize to everyone", true);
			return;
		}
		
		this.tellPlayers(lastcap + "&7team won the game!");
		this.stop();
		this.rewardTeam(this.getWinningTeam(), "You win!", false);
	}
	
	@Override
	public void onStop()
	{
		flagred.getFlag().setStopped(true);
		flagblue.getFlag().setStopped(true);

		flagred.getFlag().getReturnto().getBlock().setTypeIdAndData(0, (byte)0, false);
		flagblue.getFlag().getReturnto().getBlock().setTypeIdAndData(0, (byte)0, false);
		flagred.getFlag().despawn();
		flagblue.getFlag().despawn();

		ExecuteMove.cancel();
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
}