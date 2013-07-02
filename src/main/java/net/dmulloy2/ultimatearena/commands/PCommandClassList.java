package net.dmulloy2.ultimatearena.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaClass;

public class PCommandClassList extends UltimateArenaCommand
{
	public PCommandClassList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "classlist";
		this.aliases.add("cl");
		this.aliases.add("classes");
		this.description = "List UltimateArena classes";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + "UltimateArena Classes" + ChatColor.DARK_RED + " ====");
		for (ArenaClass classes : plugin.classes)
		{
			String name = classes.name;
			sendMessage(ChatColor.DARK_RED + "====[ " + ChatColor.GOLD + name + ChatColor.DARK_RED + " ]====");
			List<Integer> weapons = new ArrayList<>();
			weapons.clear();
			int i = 0;
			int weapon1 = classes.weapon1; if (weapon1 != 0) weapons.add(weapon1); 
			int weapon2 = classes.weapon2; if (weapon2 != 0) weapons.add(weapon2); 
			int weapon3 = classes.weapon3; if (weapon3 != 0) weapons.add(weapon3); 
			int weapon4 = classes.weapon4; if (weapon4 != 0) weapons.add(weapon4); 
			int weapon5 = classes.weapon5; if (weapon5 != 0) weapons.add(weapon5); 
			int weapon6 = classes.weapon6; if (weapon6 != 0) weapons.add(weapon6); 
			int weapon7 = classes.weapon7; if (weapon7 != 0) weapons.add(weapon7); 
			int weapon8 = classes.weapon8; if (weapon8 != 0) weapons.add(weapon8); 
			int weapon9 = classes.weapon9; if (weapon9 != 0) weapons.add(weapon9);
			
			for (Integer weaponint : weapons)
			{ 
				i++;
				ItemStack item = new ItemStack (weaponint, 1);
				Material type = item.getType();
				String weaponname = type.toString().toLowerCase().replaceAll("_", " ");
				sendMessage(ChatColor.GOLD + "" + i + ChatColor.DARK_RED + ") " + ChatColor.GOLD + weaponname);
			}
			weapons.clear();
		}
	}
}
