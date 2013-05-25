package com.orange451.UltimateArena.Arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import com.orange451.UltimateArena.Arenas.Objects.ArenaSpawn;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.util.Util;

public class FFAArena extends Arena {

	public FFAArena(ArenaZone az) {
		super(az);
		
		type = "Ffa";
		starttimer = 120;
		maxgametime = 60 * 10;
		maxDeaths = 4;
		allowTeamKilling = true;
		
		for (int i = 0; i < this.az.spawns.size(); i++) {
			this.spawns.add( new ArenaSpawn(this.az.spawns.get(i).getWorld(), this.az.spawns.get(i).getBlockX(), this.az.spawns.get(i).getBlockY(), this.az.spawns.get(i).getBlockZ()) );
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
	public void check() {
		if (starttimer <= 0) {
			if (isEmpty()) {
				if (amtPlayersInArena == 1) {
					this.setWinningTeam(-1);
					stop();
					for (int i = 0; i < arenaplayers.size(); i++) {
						spawn(arenaplayers.get(i).username, false);
					}
					if (this.amtPlayersStartingInArena > 1) {
						this.rewardTeam(winningTeam, ChatColor.BLUE + "You won!", false);
					}else{
						this.tellPlayers(ChatColor.BLUE + "Not enough people to play!");
					}
				}
			}
		}
	}
}