package net.dmulloy2.ultimatearena.arenas;

import java.util.List;

import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.ArenaSpawn;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Color;
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
			spawns.add(new ArenaSpawn(az.getSpawns().get(i)));
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
		// Determine Hat
		int num = Util.random(15);

		Color color = null;
		if (num == 0)
			color = Color.AQUA;
		if (num == 1)
			color = Color.BLACK;
		if (num == 2)
			color = Color.BLUE;
		if (num == 3)
			color = Color.FUCHSIA;
		if (num == 4)
			color = Color.GRAY;
		if (num == 5)
			color = Color.GREEN;
		if (num == 6)
			color = Color.LIME;
		if (num == 7)
			color = Color.MAROON;
		if (num == 8)
			color = Color.NAVY;
		if (num == 9)
			color = Color.OLIVE;
		if (num == 10)
			color = Color.ORANGE;
		if (num == 11)
			color = Color.PURPLE;
		if (num == 12)
			color = Color.RED;
		if (num == 13)
			color = Color.SILVER;
		if (num == 14)
			color = Color.TEAL;
		if (num == 15)
			color = Color.YELLOW;

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

				if (getStartingAmount() > 1)
				{
					List<ArenaPlayer> arenaPlayers = getValidPlayers();
					for (int i = 0; i < arenaPlayers.size(); i++)
					{
						this.winner = arenaPlayers.get(i);
					}
				}

				stop();

				if (getStartingAmount() > 1)
				{
					rewardTeam(winningTeam, false);
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
			tellPlayers("&e{0} &3has won the HungerGames!", winner.getName());
	}

	@Override
	public void onPlayerDeath(ArenaPlayer pl)
	{
		super.onPlayerDeath(pl);

		pl.getPlayer().getWorld().strikeLightningEffect(pl.getPlayer().getLocation());
		tellPlayers("&3Tribute &e{0} &3has fallen!", pl.getName());
	}
}