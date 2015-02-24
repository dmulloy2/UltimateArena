package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.util.Util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Wool;

/**
 * @author dmulloy2
 */

@Getter @Setter
public abstract class ArenaFlag extends FlagBase
{
	protected Team owningTeam;
	protected Team cappingTeam;
	protected int power;

	protected boolean capped;

	public ArenaFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);

		Wool wool = new Wool();
		wool.setColor(getColor(8));
		Util.setData(notify, wool);
	}

	@Override
	protected void setup()
	{
		super.setup();
		location.getBlock().setType(Material.WOOL);
	}

	@Override
	public abstract void checkNear(ArenaPlayer[] arenaPlayers);

	protected final void setOwningTeam(Team team)
	{
		this.owningTeam = team;

		Wool wool = new Wool();
		wool.setColor(team == Team.RED ? DyeColor.RED : DyeColor.BLUE);
		Util.setData(notify, wool);
	}

	@Deprecated
	protected final DyeColor getColor(int color)
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
}