package net.dmulloy2.ultimatearena.arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import net.dmulloy2.ultimatearena.Field3D;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

public class SPLEEFArena extends FFAArena 
{
	public Field3D spleefGround;
	public Field3D outZone;
	
	public SPLEEFArena(ArenaZone az)
	{
		super(az);
		
		setType("Spleef");
		setMaxDeaths(2);

		spleefGround = new Field3D();
		Location pos1 = az.getFlags().get(0);
		Location pos2 = az.getFlags().get(1);
		spleefGround.setParam(pos1.getWorld(), pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ(), 
				pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
		
		spleefGround.setType(getArenaZone().getSpecialType());
			
		outZone = new Field3D();
		Location pos3 = az.getFlags().get(2);
		Location pos4 = az.getFlags().get(3);
		outZone.setParam(pos3.getWorld(), pos3.getBlockX(), pos3.getBlockY(), pos3.getBlockZ(),
				pos4.getBlockX(), pos3.getBlockY(), pos3.getBlockZ());
	}
	
	@Override
	public void spawn(String name, boolean alreadySpawned)
	{
		super.spawn(name, false);
		Player p = Util.matchPlayer(name);
		if (p != null)
		{
			Location loc = getBlockInSpleefArena(0);
			if (loc != null) 
			{
				teleport(p, loc.clone().add(0,2,0));
			}
		}
	}

	public Location getBlockInSpleefArena(int repeat)
	{
		Random rand = new Random();
		Location ret = null;
		int checkx = rand.nextInt(spleefGround.getWidth()-1);
		int checkz = rand.nextInt(spleefGround.getLength()-1);
		Block b = spleefGround.getBlockAt(checkx + 1, 0, checkz + 1);
		Material mat = b.getType();
		if (mat.getId() == getArenaZone().getSpecialType())
		{
			ret = b.getLocation();
		}
		else
		{
			if (repeat < (spleefGround.getWidth()*spleefGround.getHeight()) / 2) 
			{
				ret = getBlockInSpleefArena(repeat+1);
			}
		}
		
		return ret;
	}

	@Override
	public void check() 
	{
		if (this.getAmtPlayersInArena() == 1)
		{
			if (this.getAmtPlayersStartingInArena() > 1)
			{
				this.setWinningTeam(-1);
			}
		}
		if (!checkEmpty())
		{
			for (int i = 0; i < getArenaplayers().size(); i++)
			{
				ArenaPlayer apl = getArenaplayers().get(i);
				Player pl = apl.getPlayer();
				Location ploc = pl.getLocation();
				if (pl.getHealth() > 0) {
					if (outZone.isUnder(ploc))
					{
						pl.setHealth(0);
					}
				}
			}
		}
		else
		{
			rewardTeam(-1, ChatColor.BLUE + "You won!", false);
			stop();
		}
	}
}