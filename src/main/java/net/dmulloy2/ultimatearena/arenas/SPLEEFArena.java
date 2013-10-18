package net.dmulloy2.ultimatearena.arenas;

import java.util.Random;

import lombok.Getter;
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
		this.maxDeaths = 2;

		this.spleefGround = new Field3D();
		Location pos1 = az.getFlags().get(0);
		Location pos2 = az.getFlags().get(1);
		spleefGround.setParam(pos1.getWorld(), pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ(), pos2.getBlockX(), pos2.getBlockY(),
				pos2.getBlockZ());

		spleefGround.setType(az.getSpecialType()); // Refresh the ground

		this.outZone = new Field3D();
		Location pos3 = az.getFlags().get(2);
		Location pos4 = az.getFlags().get(3);
		outZone.setParam(pos3.getWorld(), pos3.getBlockX(), pos3.getBlockY(), pos3.getBlockZ(), pos4.getBlockX(), pos3.getBlockY(),
				pos3.getBlockZ());
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

		int checkx = rand.nextInt(getSpleefGround().getWidth() - 1);
		int checkz = rand.nextInt(getSpleefGround().getLength() - 1);

		Block b = getSpleefGround().getBlockAt(checkx + 1, 0, checkz + 1);

		Material mat = b.getType();
		if (mat == az.getSpecialType())
		{
			ret = b.getLocation();
		}
		else
		{
			if (repeat < (getSpleefGround().getWidth() * getSpleefGround().getHeight()) / 2)
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

			rewardTeam(-1);
			
			if (getStartingAmount() > 1)
			{
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
			tellAllPlayers("&e{0} &3has won the Spleef tourney at &e{1}", winner.getName(), name);
	}
}