package net.dmulloy2.ultimatearena.arenas;

import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author dmulloy2
 */

public class HUNGERArena extends Arena
{
	private ArenaPlayer winner;

	public HUNGERArena(ArenaZone az)
	{
		super(az);

		this.type = FieldType.HUNGER;
		this.startTimer = 120;
		this.maxGameTime = 60 * 10 * 3;
		this.maxDeaths = 1;
		this.allowTeamKilling = true;

		for (int i = 0; i < az.getSpawns().size(); i++)
		{
			spawns.add(az.getSpawns().get(i));
		}
	}

	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}

		return getRandomSpawn(ap);
	}

	@Override
	public void onSpawn(ArenaPlayer ap)
	{
		DyeColor rand = DyeColor.values()[Util.random(DyeColor.values().length)];
		Color color = rand.getColor();

		ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(color);
		itemStack.setItemMeta(meta);
		ap.getPlayer().getInventory().setHelmet(itemStack);
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
					if (! activePlayers.isEmpty())
					{
						this.winner = activePlayers.get(0);
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
			}
		}
	}

	@Override
	public void announceWinner()
	{
		if (winner != null)
			tellAllPlayers("&e{0} &3is the victor!", winner.getName());
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		pl.getPlayer().getWorld().strikeLightningEffect(pl.getPlayer().getLocation());
		tellPlayers("&3Tribute &e{0} &3has fallen!", pl.getName());
	}
}