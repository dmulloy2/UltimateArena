package net.dmulloy2.ultimatearena.arenas;

import java.util.Random;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaSpawn;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class FFAArena extends Arena 
{
	public FFAArena(ArenaZone az)
	{
		super(az);
		
		setType("Ffa");
		setStarttimer(120);
		setMaxgametime(60 * 10);
		setMaxDeaths(4);
		setAllowTeamKilling(true);
		
		for (int i = 0; i < this.getArenaZone().getSpawns().size(); i++) 
		{
			this.getSpawns().add( new ArenaSpawn(this.getArenaZone().getSpawns().get(i).getWorld(), this.getArenaZone().getSpawns().get(i).getBlockX(), this.getArenaZone().getSpawns().get(i).getBlockY(), this.getArenaZone().getSpawns().get(i).getBlockZ()) );
		}
	}
	
	@Override
	public void spawn(String name, boolean alreadySpawned)
	{
		super.spawn(name, false);
		spawnRandom(name);
		Player p = Util.matchPlayer(name);
		if (p != null)
		{
			/**Determine Hat**/
			Random rand = new Random();
			int num = rand.nextInt(15);
			
			Color color = null;
			if (num == 0) color = Color.AQUA;
			if (num == 1) color = Color.BLACK;
			if (num == 2) color = Color.BLUE;
			if (num == 3) color = Color.FUCHSIA;
			if (num == 4) color = Color.GRAY;
			if (num == 5) color = Color.GREEN;
			if (num == 6) color = Color.LIME;
			if (num == 7) color = Color.MAROON;
			if (num == 8) color = Color.NAVY;
			if (num == 9) color = Color.OLIVE;
			if (num == 10) color = Color.ORANGE;
			if (num == 11) color = Color.PURPLE;
			if (num == 12) color = Color.RED;
			if (num == 13) color = Color.SILVER;
			if (num == 14) color = Color.TEAL;
			if (num == 15) color = Color.YELLOW;
			
			ItemStack itemStack = new ItemStack(Material.LEATHER_HELMET, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
			meta.setColor(color);
			itemStack.setItemMeta(meta);
			p.getInventory().setHelmet(itemStack);
		}
	}

	@Override
	public void check()
	{
		if (getStarttimer() <= 0) 
		{
			if (isEmpty())
			{
				if (getAmtPlayersInArena() == 1) 
				{
					this.setWinningTeam(-1);
					stop();
					
					for (int i = 0; i < arenaPlayers.size(); i++) 
					{
						spawn(arenaPlayers.get(i).getUsername(), false);
					}
					
					if (this.getAmtPlayersStartingInArena() > 1) 
					{
						this.rewardTeam(getWinningTeam(), "&9You won!", false);
					}
					else
					{
						this.tellPlayers("&9Not enough people to play!");
					}
				}
			}
		}
	}
}