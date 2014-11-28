package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

@Getter
public final class Field3D extends Field
{
	public Field3D() { }

	public Field3D(ArenaLocation point1, ArenaLocation point2)
	{
		super(point1, point2);
	}

	@Override
	public boolean isInside(Location loc)
	{
		if (super.isInside(loc))
		{
			int locY = loc.getBlockY();
			return locY >= min.getY() && locY <= max.getY();
		}

		return false;
	}

	public final boolean isUnder(Location loc)
	{
		return super.isInside(loc) && loc.getBlockY() < min.getY();
	}

	public final int getHeight()
	{
		return max.getY() - min.getY();
	}
}