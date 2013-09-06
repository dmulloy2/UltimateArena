package net.dmulloy2.ultimatearena.arenas;

import java.util.Random;

import net.dmulloy2.ultimatearena.Field3D;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
		if (mat.getId() == getArenaZone().getSpecialType())
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
		if (getActivePlayers() == 1)
		{
			if (getStartingAmount() > 1)
			{
				this.setWinningTeam(-1);
			}
		}
		if (!checkEmpty())
		{
			for (int i = 0; i < arenaPlayers.size(); i++)
			{
				ArenaPlayer apl = arenaPlayers.get(i);
				Player pl = apl.getPlayer();
				Location ploc = pl.getLocation();
				if (pl.getHealth() > 0)
				{
					if (outZone.isUnder(ploc))
					{
						pl.setHealth(0);
					}
				}
			}
		}
		else
		{
			spleefGround.setType(az.getSpecialType()); // Refresh the ground

			rewardTeam(-1, false);
			stop();
		}
	}

	public Field3D getSpleefGround()
	{
		return spleefGround;
	}

	@Override
	public void announceWinner()
	{
		tellPlayers("&3You won!");
	}
}