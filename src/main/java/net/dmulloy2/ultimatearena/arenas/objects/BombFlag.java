package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.BOMBArena;
import net.dmulloy2.ultimatearena.util.FormatUtil;
import net.dmulloy2.ultimatearena.util.Util;

public class BombFlag extends ArenaFlag
{
	private int bnum;
	private int fuser = 0;
	private int timer = 45;
	
	private boolean fused = false;
	private boolean exploded = false;
	
	public BombFlag(Arena arena, Location loc, final UltimateArena plugin)
	{
		super(arena, loc, plugin);
	}
	
	@Override
	public void checkNear(List<ArenaPlayer> arenaplayers) 
	{
		if (fused) 
		{
			timer--;
			Util.playEffect(Effect.STEP_SOUND, this.getLoc(), 4);

			if (timer == 30 || timer == 20 || timer == 10 || timer <= 5) 
			{
				getArena().tellPlayers("&7Bomb &6{0} &7will expode in &d{1} &7seconds!", getBnum(), timer);
			}
			
			if (timer < 1) 
			{
				if (!isExploded()) 
				{
					Util.playEffect(Effect.EXTINGUISH, this.getLoc(), 4);
					BOMBArena ba = null;
					if (getArena() instanceof BOMBArena)
					{
						ba = (BOMBArena)getArena();
					}
					
					if (ba != null)
					{
						int amte = 0;
						if (ba.bomb1.isExploded())
							amte++;
						if (ba.bomb2.isExploded())
							amte++;
						
						if (amte == 0)
							getArena().killAllNear(this.getLoc(), 12);
					}
					
					setExploded(true);
					fused = false;
					getArena().tellPlayers("&cRED &7team blew up bomb &6{0}&7!", getBnum());
				}
			}
		}
		
		boolean fuse = false;
		boolean defuse = false;
		ArenaPlayer capturer = null;
		List<Player> players = new ArrayList<Player>();
		for (int i = 0; i < arenaplayers.size(); i++) 
		{
			ArenaPlayer apl = arenaplayers.get(i);
			Player pl = apl.getPlayer();
			if (pl != null)
			{
				if (Util.pointDistance(pl.getLocation(), getLoc()) < 3.0 && pl.getHealth() > 0) 
				{
					players.add(pl);
					capturer = apl;
					if (apl.getTeam() == 1) 
					{
						fuse = true;
					}
					else
					{
						defuse = true;
					}
				}
			}
		}
		
		if (!(fuse && defuse) && !isExploded())
		{
			if (capturer != null) 
			{
				Player pl = capturer.getPlayer();
				if (fuse)
				{
					if (!fused)
					{ 
						//team 1 is fusing
						fuser++;
						pl.sendMessage(plugin.getPrefix() + FormatUtil.format("&7Fusing Bomb &6{0}! &7(&d{1}&7/&d10)", getBnum(), fuser));
						if (fuser > 10)
						{
							fuser = 0;
							fused = true;
							getArena().tellPlayers("&7Bomb &6{0} &7is now &dfused&7!", getBnum());
						}
					}
				}
				else if (defuse)
				{
					//team 2 is desfusing
					if (fused) 
					{
						fuser++;
						pl.sendMessage(plugin.getPrefix() + FormatUtil.format("&7Defusing Bomb &6{0}! &7(&d{1}&7/&d10)", getBnum(), fuser));
						if (fuser > 10)
						{
							fuser = 0;
							fused = false;
							timer = 45;
							getArena().tellPlayers("&7Bomb &6{0} &7is now &ddefused&7!", getBnum());
						}
					}
				}
				
			}
		}
	}

	public int getBnum() 
	{
		return bnum;
	}

	public void setBnum(int bnum) 
	{
		this.bnum = bnum;
	}

	public boolean isExploded() 
	{
		return exploded;
	}

	public void setExploded(boolean exploded) 
	{
		this.exploded = exploded;
	}
}