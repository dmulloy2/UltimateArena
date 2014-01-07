package net.dmulloy2.ultimatearena.arenas;

import java.util.Random;

import lombok.Getter;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Field3D;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter
public class SPLEEFArena extends FFAArena
{
	private Field3D spleefGround;
	private Field3D outZone;

	public SPLEEFArena(ArenaZone az)
	{
		super(az);
		this.type = FieldType.SPLEEF;

		ArenaLocation pos1 = az.getFlags().get(0);
		ArenaLocation pos2 = az.getFlags().get(1);
		this.spleefGround = new Field3D(pos1, pos2);
		this.spleefGround.setType(az.getSpecialType()); // Refresh the ground

		ArenaLocation pos3 = az.getFlags().get(2);
		ArenaLocation pos4 = az.getFlags().get(3);
		this.outZone = new Field3D(pos3, pos4);
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}

		return getBlockInSpleefArena(0);
	}

	public Location getBlockInSpleefArena(int repeat)
	{
		Random rand = new Random();
		Location ret = null;

		int checkx = rand.nextInt(spleefGround.getWidth() - 1);
		int checkz = rand.nextInt(spleefGround.getLength() - 1);

		Block b = spleefGround.getBlockAt(checkx + 1, 0, checkz + 1);

		Material mat = b.getType();
		if (mat == az.getSpecialType())
		{
			ret = b.getLocation();
		}
		else
		{
			if (repeat < (spleefGround.getWidth() * spleefGround.getHeight()) / 2)
			{
				ret = getBlockInSpleefArena(repeat + 1);
			}
		}

		return ret;
	}

	@Override
	public void check()
	{
		if (active.size() == 1)
		{
			if (startingAmount > 1)
			{
				setWinningTeam(-1);
			}
		}

		if (! checkEmpty())
		{
			for (ArenaPlayer ap : getActivePlayers())
			{
				Player pl = ap.getPlayer();
				Location loc = pl.getLocation();
				if (pl.getHealth() < 0)
				{
					if (outZone.isUnder(loc))
					{
						pl.setHealth(0.0D);
					}
				}
			}
		}
		else
		{
			spleefGround.setType(az.getSpecialType()); // Refresh the ground

			if (startingAmount > 1)
			{
				rewardTeam(-1);

				if (active.size() > 0)
				{
					this.winner = active.get(0);
				}
			}

			stop();
		}
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers("&e{0} &3has won the Spleef match at &e{1}", winner.getName(), name);
	}
}