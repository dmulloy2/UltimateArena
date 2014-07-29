package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.BOMBArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.util.Util;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class BombFlag extends ArenaFlag
{
	protected int fuser;
	protected int timer;
	protected int bombNumber;

	protected boolean fused;
	protected boolean exploded;

	public BombFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
		this.timer = 45;
	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		if (fused)
		{
			timer--;
			Util.playEffect(Effect.STEP_SOUND, location, Material.COBBLESTONE);

			if (timer == 30 || timer == 20 || timer == 10 || timer <= 5)
			{
				arena.tellPlayers("&3Bomb &e{0} &3will explode in &e{1} &3seconds!", bombNumber, timer);
			}

			if (timer < 1)
			{
				if (! exploded)
				{
					Util.playEffect(Effect.EXTINGUISH, location, null);

					BOMBArena ba = null;
					if (arena instanceof BOMBArena)
					{
						ba = (BOMBArena) arena;
					}

					if (ba != null)
					{
						int amte = 0;
						if (ba.getBomb1().isExploded())
							amte++;
						if (ba.getBomb2().isExploded())
							amte++;

						if (amte == 0)
							arena.killAllNear(location, 12.0D);
					}

					this.fused = false;
					this.exploded = true;

					arena.tellPlayers("&cRED &3team blew up bomb &e{0}&3!", bombNumber);
				}
			}
		}

		boolean fuse = false;
		boolean defuse = false;
		ArenaPlayer capturer = null;

		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() > 0.0D && player.getWorld().getUID().equals(location.getWorld())
					&& player.getLocation().distance(location) < 3.0D)
			{
				capturer = ap;
				if (ap.getTeam() == 1)
					fuse = true;
				else
					defuse = true;
			}
		}

		if (! (fuse && defuse) && ! exploded)
		{
			if (capturer != null)
			{
				if (fuse)
				{
					if (! fused)
					{
						// team 1 is fusing
						fuser++;
						capturer.sendMessage("&3Fusing Bomb &e{0}! &3(&e{1}&3/&e10)", bombNumber, fuser);
						if (fuser >= 10)
						{
							fuser = 0;
							fused = true;
							arena.tellPlayers("&3Bomb &e{0} &3is now &efused&3!", bombNumber);
						}
					}
				}
				else if (defuse)
				{
					// team 2 is desfusing
					if (fused)
					{
						fuser++;
						capturer.sendMessage("&3Defusing Bomb &e{0}! &3(&e{1}&3/&e10&3)", bombNumber, fuser);
						if (fuser >= 10)
						{
							fuser = 0;
							fused = false;
							timer = 45;
							arena.tellPlayers("&3Bomb &e{0} &3is now &edefused&3!", bombNumber);
						}
					}
				}
			}
		}
	}
}