package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ArenaFlag extends FlagBase
{
	protected int power;
	private int color = 14;
	private int team = 0;
	protected int added = 0;
	
	private boolean capped = false;
	
	public ArenaFlag(Arena arena, Location loc, final UltimateArena plugin) 
	{
		super(arena, loc, plugin);
	}
	
	@Override
	public synchronized void setup() 
	{
		super.setup();
		getLoc().getBlock().setType(Material.WOOL);
	}
	
	public void step() 
	{
		setCapped(false);
		setColor(8);
		setTeam(0);
		if (added > 50) 
		{
			setColor(14);
			setTeam(1);
		}
		if (added < -50)
		{
			setColor(11);
			setTeam(2);
		}
		if (added >= 150)
		{ 
			added = 150; 
			setCapped(true); 
		}
		if (added <= -150)
		{
			added = -150;
			setCapped(true);
		}
		
		if (isCapped())
		{
			getNotify().setData((byte) getColor());
		}
		else
		{
			getNotify().setData((byte) 8);
		}
	}
	
	@Override
	public synchronized void checkNear(List<ArenaPlayer> arenaplayers) 
	{
		int team1 = 0;
		int team2 = 0;
		List<Player> players = new ArrayList<Player>();
		synchronized(players) 
		{
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				Player pl = arenaplayers.get(i).getPlayer();
				if (pl != null) 
				{
					if (Util.pointDistance(pl.getLocation(), getLoc()) < 4.5 && pl.getHealth() > 0)
					{
						players.add(pl);
						if (arenaplayers.get(i).getTeam() == 1)
						{
							team1++;
						}
						else
						{
							team2++;
						}
					}
				}
			}
		}
		
		int percent = 0;
		if (getColor() == 14)
		{
			percent = added-50;
		}
		else if (getColor() == 11)
		{
			percent = Math.abs(added)-50;
		}
		else
		{
			percent = added+50;
		}
		
		if (team1 > team2)
		{
			added += (team1-team2) * 5;
			for (int i = 0; i < players.size(); i++)
			{
				Player player = players.get(i);
				if (percent < 100) 
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&7Capping! &6{0}%", percent));
				}
			}
		}
		else if (team2 > team1) 
		{
			added -= (team2-team1) * 5;
			for (int i = 0; i < players.size(); i++) 
			{
				Player player = players.get(i);
				if (percent < 100) 
				{
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&7Capping! &6{0}%", percent));
				}
			}
		}
	}

	public int getTeam()
	{
		return team;
	}

	public void setTeam(int team)
	{
		this.team = team;
	}

	public boolean isCapped()
	{
		return capped;
	}

	public void setCapped(boolean capped)
	{
		this.capped = capped;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}
}