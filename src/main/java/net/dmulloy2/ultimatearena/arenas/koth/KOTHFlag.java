package net.dmulloy2.ultimatearena.arenas.koth;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaFlag;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class KOTHFlag extends ArenaFlag
{
	protected ArenaPlayer leader;
	protected KOTHArena arena;

	public KOTHFlag(KOTHArena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
		this.arena = arena;
	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		if (! arena.isInGame())
			return;

		int amt = 0;
		ArenaPlayer capturer = null;

		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (player.getHealth() > 0.0D && player.getWorld().getUID().equals(location.getWorld().getUID())
					&& player.getLocation().distance(location) < 3.0D)
			{
				amt++;
				capturer = ap;
			}
		}

		if (amt == 1)
		{
			if (capturer != null)
			{
				capturer.putData("kothPoints", capturer.getDataInt("kothPoints") + 1);
				capturer.sendMessage("&3You have capped for &e1 &3point! (&e{0}&3/&e{1}&3)", capturer.getDataInt("kothPoints"),
						arena.getMaxPoints());
				leadChange();
			}
		}
	}

	private final void leadChange()
	{
		List<ArenaPlayer> lb = arena.getLeaderboard();
		ArenaPlayer ap = lb.get(0);
		if (ap != null)
		{
			if (leader == null || ! ap.getUniqueId().equals(leader.getUniqueId()))
			{
				arena.tellPlayers("&e{0} &3has taken the lead!", ap.getName());
				leader = ap;
			}
		}
	}
}