package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.util.Util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

/**
 * @author dmulloy2
 */

@Getter @Setter
public abstract class ArenaFlag extends FlagBase
{
	protected int owningTeam;
	protected int cappingTeam;
	protected int power;

	protected boolean capped;

	public ArenaFlag(Arena arena, ArenaLocation location, UltimateArena plugin)
	{
		super(arena, location, plugin);

		Wool wool = new Wool();
		wool.setColor(getColor(8));
		setData(notify, wool);
	}

	@SuppressWarnings("deprecation")
	protected final void setData(Block block, MaterialData data)
	{
		Util.setData(block, data);
	}

	@Override
	protected void setup()
	{
		super.setup();
		location.getBlock().setType(Material.WOOL);
	}

	@Override
	public abstract void checkNear(List<ArenaPlayer> arenaPlayers);

	protected final void setOwningTeam(int team)
	{
		this.owningTeam = team;

		Wool wool = new Wool();
		wool.setColor(getColor(team == 1 ? 14 : 11));
		setData(notify, wool);
	}

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