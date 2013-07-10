package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.KOTHArena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KothFlag extends ArenaFlag
{
	private KOTHArena marena;
	public KothFlag(KOTHArena arena, Location loc, final UltimateArena plugin)
	{
		super(arena, loc, plugin);
		this.marena = arena;
	}
	
	public void checkNear(ArrayList<ArenaPlayer> arenaplayers) 
	{
		int amt = 0;
		ArenaPlayer capturer = null;
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			Player pl = arenaplayers.get(i).getPlayer();
			if (pl != null)
			{
				if (Util.pointDistance(pl.getLocation(), getLoc()) < 3.0 && pl.getHealth() > 0) 
				{
					players.add(pl);
					amt++;
					capturer = arenaplayers.get(i);
				}
			}
		}
		
		if (amt == 1) 
		{
			if (capturer != null) 
			{
				Player pl = capturer.getPlayer();
				capturer.setPoints(capturer.getPoints() + 1);
				
				pl.sendMessage(plugin.getPrefix() + 
						FormatUtil.format("&7You have capped for &d1 &7point! (&d{0}&7/&d{1}&7)", capturer.getPoints(), marena.MAXPOWER));
			}
		}
	}
}