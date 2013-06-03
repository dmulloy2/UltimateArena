package com.orange451.UltimateArena.Arenas;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.Arenas.Objects.CTFflagbase;

public class CTFArena extends Arena
{
	public CTFflagbase flagred;
	public CTFflagbase flagblue;
	public int redcap;
	public int bluecap;
	private int ExecuteMove;
	private String lastcap;
	
	public CTFArena(ArenaZone az)
	{
		super(az);
		
		type = "Ctf";
		starttimer = 120;
		gametimer = 0;
		maxgametime = 60 * 15;
		maxDeaths = 990;
		
		try
		{
			flagred = new CTFflagbase(this, az.flags.get(0), 1);
			flagblue = new CTFflagbase(this, az.flags.get(1), 2);
			
			flagred.initialize();
			flagblue.initialize();
			ExecuteMove = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new ExecuteMove(), 12, 1);
		}
		catch(Exception e) 
		{
			plugin.getLogger().severe("Error setting up CTF:");
			e.printStackTrace();
		}
	}
	
	@Override
	public void check()
	{
		try
		{
			if (starttimer <= 0) 
			{
				if (!simpleTeamCheck(false)) 
				{
					this.tellPlayers(ChatColor.BLUE + "One team is empty! game ended!");
					this.stop();
				}
				else
				{
					if (this.amtPlayersStartingInArena <= 1) 
					{
						this.tellPlayers(ChatColor.BLUE + "Not enough people to play!");
						this.stop();
					}
				}
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().severe("Error with CTF:");
			e.printStackTrace();
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
			flagred.flag.tick();
			flagblue.flag.tick();
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
		this.rewardTeam(this.winningTeam, "You win!", false);
	}
	
	@Override
	public void onStop()
	{
		try
		{
			flagred.flag.stopped = true;
			flagblue.flag.stopped = true;
		}
		catch(Exception e)
		{
			//
		}
		try
		{
			flagred.flag.returnto.getBlock().setTypeIdAndData(0, (byte)0, false);
			flagblue.flag.returnto.getBlock().setTypeIdAndData(0, (byte)0, false);
			flagred.flag.despawn();
			flagblue.flag.despawn();
		}
		catch(Exception e)
		{
			//
		}
		
		plugin.getServer().getScheduler().cancelTask(ExecuteMove);
	}
	
	class ExecuteMove implements Runnable 
	{
		public ExecuteMove()
		{
		}
		
		public void run()
		{
			try
			{
				if (!stopped)
				{
					flagred.checkNear(arenaplayers);
					flagblue.checkNear(arenaplayers);
				}
				else
				{
					onStop();
				}
			}
			catch(Exception ex)
			{
				plugin.getLogger().severe("Error executing move:");
				ex.printStackTrace();
			}
		}
	}
}