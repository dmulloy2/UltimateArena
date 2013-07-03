package net.dmulloy2.ultimatearena.arenas;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.CTFFlagBase;

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
		
		setType("Ctf");
		setStarttimer(120);
		setGametimer(0);
		setMaxgametime(60 * 15);
		setMaxDeaths(990);

		flagred = new CTFFlagBase(this, az.getFlags().get(0), 1);
		flagblue = new CTFFlagBase(this, az.getFlags().get(1), 2);
			
		flagred.initialize();
		flagblue.initialize();
		
		
		ExecuteMove = new ExecuteMove().runTaskTimer(getPlugin(), 12, 1);
	}
	
	@Override
	public void check()
	{
		if (getStarttimer() <= 0) 
		{
			if (!simpleTeamCheck(false)) 
			{
				this.tellPlayers(ChatColor.BLUE + "One team is empty! game ended!");
				this.stop();
			}
			else
			{
				if (this.getAmtPlayersStartingInArena() <= 1) 
				{
					this.tellPlayers(ChatColor.BLUE + "Not enough people to play!");
					this.stop();
				}
			}
		}
		
		if (redcap >= 3 || bluecap >= 3) 
		{
			this.setWinningTeam(1);
			lastcap = ChatColor.RED + "RED";
			if (bluecap >= 3) 
			{
				this.setWinningTeam(2);
				lastcap = ChatColor.BLUE + "BLUE";
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
		
		this.tellPlayers(lastcap + ChatColor.GRAY + " team won the game!");
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
				flagred.checkNear(getArenaplayers());
				flagblue.checkNear(getArenaplayers());
			}
			else
			{
				onStop();
			}
		}
	}
}