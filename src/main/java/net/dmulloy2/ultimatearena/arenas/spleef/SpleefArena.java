package net.dmulloy2.ultimatearena.arenas.spleef;

import java.util.Random;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.ffa.FFAArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.Field3D;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author dmulloy2
 */

@Getter
public class SpleefArena extends FFAArena
{
	private Field3D spleefGround;
	private Field3D outZone;

	private Material specialType;

	public SpleefArena(ArenaZone az)
	{
		super(az);

		ArenaLocation pos1 = az.getFlags().get(0);
		ArenaLocation pos2 = az.getFlags().get(1);
		this.spleefGround = new Field3D(pos1, pos2);
		this.spleefGround.setType(specialType); // Refresh the ground

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
		if (mat == specialType)
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
		if (isInGame())
		{
			if (isEmpty())
			{
				setWinningTeam(-1);

				if (startingAmount > 1)
				{
					if (active.size() > 0)
					{
						this.winner = active.get(0);
					}
				}

				stop();

				if (startingAmount > 1)
				{
					rewardTeam(winningTeam);
				}
				else
				{
					tellPlayers("&3Not enough people to play!");
				}

				// Refresh the ground
				spleefGround.setType(specialType);
			}
			else
			{
				for (ArenaPlayer ap : getActivePlayers())
				{
					Location loc = ap.getPlayer().getLocation();
					if (ap.getPlayer().getHealth() > 0.0D)
					{
						if (outZone.isUnder(loc))
						{
							ap.getPlayer().setHealth(0.0D);
						}
					}
				}
			}
		}
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers("&e{0} &3has won the Spleef match at &e{1}", winner.getName(), name);
	}

	@Override
	public void onReload()
	{
		this.specialType = ((SpleefZone) az).getSpecialType();
	}
}