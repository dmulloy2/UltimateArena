package net.dmulloy2.ultimatearena.flags;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

/**
 * @author dmulloy2
 */

@Getter
public class ArenaFlag extends FlagBase
{
	protected int team;
	protected int power;
	protected int added;
	protected int color = 14;

	protected boolean capped;

	public ArenaFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);
	}

	@Override
	protected void setup()
	{
		super.setup();
		location.getBlock().setType(Material.WOOL);
	}

	public final void step()
	{
		this.capped = false;
		this.color = 8;
		this.team = 0;

		if (added > 50)
		{
			this.color = 14;
			this.team = 1;
		}
		if (added < -50)
		{
			this.color = 11;
			this.team = 2;
		}
		if (added >= 150)
		{
			this.added = 150;
			this.capped = true;
		}
		if (added <= -150)
		{
			this.added = -150;
			this.capped = true;
		}

		if (capped)
		{
			notify.setType(Material.WOOL);
			((Wool) notify.getState().getData()).setColor(getColor(color));
			notify.getState().update(true);
		}
		else
		{
			notify.setType(Material.WOOL);
			((Wool) notify.getState().getData()).setColor(getColor(8));
			notify.getState().update(true);
		}
	}

	private final DyeColor getColor(int color)
	{
		if (color == 8)
			return DyeColor.SILVER;
		else if (color == 11)
			return DyeColor.BLUE;
		else if (color == 14)
			return DyeColor.RED;
		else
			return DyeColor.WHITE;

	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		int team1 = 0;
		int team2 = 0;

		List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			Player player = ap.getPlayer();
			if (player != null)
			{
				if (Util.pointDistance(player.getLocation(), location) < 4.5 && player.getHealth() > 0)
				{
					players.add(ap);

					if (ap.getTeam() == 1)
						team1++;
					else
						team2++;
				}
			}
		}

		int percent = 0;
		if (color == 14)
		{
			percent = added - 50;
		}
		else if (color == 11)
		{
			percent = Math.abs(added) - 50;
		}
		else
		{
			percent = added + 50;
		}

		if (team1 > team2)
		{
			added += (team1 - team2) * 5;
			for (int i = 0; i < players.size(); i++)
			{
				ArenaPlayer player = players.get(i);
				if (percent < 100)
				{
					player.sendMessage("&3Capping! &e{0}&3%");
				}
			}
		}
		else if (team2 > team1)
		{
			added -= (team2 - team1) * 5;
			for (int i = 0; i < players.size(); i++)
			{
				ArenaPlayer player = players.get(i);
				if (percent < 100)
				{
					player.sendMessage("&3Capping! &e{0}&3%");
				}
			}
		}
	}
}