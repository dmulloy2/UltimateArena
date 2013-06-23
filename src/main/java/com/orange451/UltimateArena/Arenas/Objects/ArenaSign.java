package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;

public class ArenaSign 
{
	public Sign sign;
	public Location loc;
	public boolean autoAssign = false;
	public ArenaZone zone;
	
	public UltimateArena plugin;
	public ArenaSign(UltimateArena plugin, Sign sign, Location loc)
	{
		this.plugin = plugin;
		this.sign = sign;
		this.loc = loc;
		this.autoAssign = true;
	}
	
	public ArenaSign(UltimateArena plugin, Sign sign, Location loc, ArenaZone zone)
	{
		this.plugin = plugin;
		this.sign = sign;
		this.loc = loc;
		this.zone = zone;
	}
	
	public void update()
	{
		sign.setLine(0, "[UltimateArena]");
		sign.setLine(1, "Click to join");
		sign.setLine(2, zone.arenaName);
		
		String line3 = "";
		if (plugin.getArena(zone.arenaName) != null)
		{
			StringBuilder line = new StringBuilder();
			Arena a = plugin.getArena(zone.arenaName);
			if (a.starttimer > 1)
			{
				line.append("LOBBY ");
			}
			else
			{
				line.append("INGAME ");
			}
			line.append(a.amtPlayersInArena + "/" + zone.maxPlayers);
			line3 = line.toString();
		}
		else
		{
			StringBuilder line = new StringBuilder();
			if (zone.disabled)
			{
				line.append("DISABLED (0/0)");
			}
			else
			{
				line.append("IDLE (0/");
				line.append(zone.maxPlayers);
				line.append(")");
			}
			line3 = line.toString();
		}
		
		sign.setLine(3, line3);
		sign.update(true);
	}
	
	public void save()
	{
		plugin.getFileHelper().saveSign(this);
	}
}