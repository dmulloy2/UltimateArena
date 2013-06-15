/**
* UltimateArena - a bukkit plugin
* Copyright (C) 2013 Minesworn/dmulloy2
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.orange451.UltimateArena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.orange451.UltimateArena.Arenas.*;
import com.orange451.UltimateArena.Arenas.Objects.*;
import com.orange451.UltimateArena.commands.*;
import com.orange451.UltimateArena.listeners.*;
import com.orange451.UltimateArena.permissions.PermissionHandler;
import com.orange451.UltimateArena.util.FormatUtil;
import com.orange451.UltimateArena.util.InventoryHelper;
import com.orange451.UltimateArena.util.Util;

public class UltimateArena extends JavaPlugin
{
	private @Getter Economy economy;
	private @Getter FileHelper fileHelper;
	private @Getter FileConverter fileConverter;
	
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	
	public int arenasPlayed = 0;
	
	public List<ArenaClass> classes = new ArrayList<ArenaClass>();
	public List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	public List<ArenaZone> loadedArena = new ArrayList<ArenaZone>();
	public List<Arena> activeArena = new ArrayList<Arena>();
	public List<RemindTask> waiting = new ArrayList<RemindTask>();
	public List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	public List<String> fieldTypes = new ArrayList<String>();
	public WhiteListCommands wcmd = new WhiteListCommands();
	public List<SavedArenaPlayer> savedPlayers = new ArrayList<SavedArenaPlayer>();

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();
		
		permissionHandler =  new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
		
		fileHelper = new FileHelper(this);
		fileConverter = new FileConverter(this);

		File arenaFile = new File(getDataFolder(), "arenas");
		if (!arenaFile.exists())
		{
			arenaFile.mkdir();
		}
		
		File playersFile = new File(getDataFolder(), "players");
		if (!playersFile.exists())
		{
			playersFile.mkdir();
		}
		
		File classFile = new File(getDataFolder(), "classes");
		if (!classFile.exists())
		{
			classFile.mkdir();
		}
		
		File configsFile = new File(getDataFolder(), "configs");
		if (!configsFile.exists())
		{
			configsFile.mkdir();
		}
		
		saveDefaultConfig();
		
		// Add fields
		fieldTypes.add("pvp");
		fieldTypes.add("mob");
		fieldTypes.add("cq");
		fieldTypes.add("koth");
		fieldTypes.add("bomb");
		fieldTypes.add("ffa");
		fieldTypes.add("spleef");
		fieldTypes.add("infect");
		fieldTypes.add("ctf");
		fieldTypes.add("hunger");

		// Add Commands
		getCommand("ua").setExecutor(commandHandler);
		
		commandHandler.setCommandPrefix("ua");
		commandHandler.registerCommand(new PCommandHelp(this));
		commandHandler.registerCommand(new PCommandInfo(this));
		commandHandler.registerCommand(new PCommandList(this));
		commandHandler.registerCommand(new PCommandJoin(this));
		commandHandler.registerCommand(new PCommandLeave(this));
		commandHandler.registerCommand(new PCommandStats(this));
		commandHandler.registerCommand(new PCommandLike(this));
		commandHandler.registerCommand(new PCommandDislike(this));
		
		commandHandler.registerCommand(new PCommandCreate(this));
		commandHandler.registerCommand(new PCommandSetPoint(this));
		commandHandler.registerCommand(new PCommandSetDone(this));
		commandHandler.registerCommand(new PCommandDelete(this));
		commandHandler.registerCommand(new PCommandStop(this));
		
		commandHandler.registerCommand(new PCommandForceStop(this));
		commandHandler.registerCommand(new PCommandRefresh(this));
		commandHandler.registerCommand(new PCommandForceJoin(this));
		commandHandler.registerCommand(new PCommandDisable(this));
		commandHandler.registerCommand(new PCommandEnable(this));
		commandHandler.registerCommand(new PCommandKick(this));
		commandHandler.registerCommand(new PCommandStart(this));
		commandHandler.registerCommand(new PCommandPause(this));
		
		commandHandler.registerCommand(new PCommandClasses(this));
		
		loadPlayers();

		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("PVPGunPlus"))
			pm.registerEvents(new PVPGunPlusListener (this), this);
			
		pm.registerEvents(new PluginEntityListener(this), this);
		pm.registerEvents(new PluginBlockListener(this), this);
		pm.registerEvents(new PluginPlayerListener(this), this);
		
		Util.initialize(this);
 
		new ArenaUpdater().runTaskTimer(this, 2L, 20L);
			
		checkVault(pm);
		
		fileConverter.run();

		loadFiles();
		
		long finish = System.currentTimeMillis();
		
		getLogger().info(getDescription().getFullName() + " has been enabled ("+(finish-start)+"ms)");
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		for (int i=0; i<activeArena.size(); i++)
		{
			try
			{
				activeArena.get(i).onDisable();
			}
			catch (Exception e)
			{
				getLogger().severe("Error while stopping arena " + activeArena.get(i).name + ". (" + e.getMessage()+")");
			}
		}
		
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);
		
		clearMemory();
		
		long finish = System.currentTimeMillis();
		
		getLogger().info(getDescription().getFullName() + " has been disabled ("+(finish-start)+"ms)");
	}
	
	public void loadPlayers()
	{
		savedPlayers = fileHelper.getSavedPlayers();
		if (savedPlayers.size() > 0)
		{
			for (Player player : getServer().getOnlinePlayers())
			{
				for (SavedArenaPlayer savedArenaPlayer : savedPlayers)
				{
					if (savedArenaPlayer.getName().equals(player.getName()))
					{
						float exp = savedArenaPlayer.getExp();
						Location loc = savedArenaPlayer.getLocation();
								
						normalize(player);
						player.setExp(exp);
						player.teleport(loc);
						removePotions(player);
								
						fileHelper.deletePlayer(player);
								
						savedPlayers.remove(savedArenaPlayer);
					}
				}
			}
			
			getLogger().info("Loaded " + savedPlayers.size() + " saved players!");
		}
	}

	public void onQuit(Player player)
	{
		if (isPlayerCreatingArena(player)) 
		{
			makingArena.remove(getArenaCreator(player));
		}
		
		if (isInArena(player))
		{
			Arena ar = getArena(player);
			ArenaPlayer ap = getArenaPlayer(player);
			if (ap != null)
			{
				getLogger().info("Player " + player.getName() + " leaving arena " + ar.name + " from quit");
				SavedArenaPlayer loggedOut = new SavedArenaPlayer(player.getName(), ap.startxp, ap.spawnBack);
						
				savedPlayers.add(loggedOut);
				fileHelper.savePlayer(loggedOut);
						
				removeFromArena(player.getName());
			}
		}
	}
	
	public void onJoin(Player player) 
	{
		/**Normalize Saved Players**/
		for (int i=0; i<savedPlayers.size(); i++)
		{
			SavedArenaPlayer savedArenaPlayer = savedPlayers.get(i);
			if (savedArenaPlayer.getName().equals(player.getName()))
			{
				float exp = savedArenaPlayer.getExp();
				Location loc = savedArenaPlayer.getLocation();
						
				normalize(player);
				player.setExp(exp);
				player.teleport(loc);
				removePotions(player);
						
				fileHelper.deletePlayer(player);
						
				savedPlayers.remove(savedArenaPlayer);
			}
		}
	}
	
	public void leaveArena(Player player)
	{
		if (isInArena(player))
		{
			Arena a = getArena(player);
			a.endPlayer(getArenaPlayer(player), false);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Error, you are not in an arena");
		}
	}
	
	public void loadArenas()
	{
		File folder = new File(getDataFolder(), "arenas");
		File[] children = folder.listFiles();
		for (File file : children)
		{
			ArenaZone az = new ArenaZone(this, file);
			if (az.loaded)
			{
				loadedArena.add(az);
			}
		}
		
		getLogger().info("Loaded " + children.length + " arena files!");
	}
	
	public void loadConfigs() 
	{
		for (int i = 0; i < fieldTypes.size(); i++) 
		{
			loadConfig(fieldTypes.get(i));
		}
		
		getLogger().info("Loaded " + fieldTypes.size() + " arena configs!");
		
		loadWhiteListedCommands();
	}
	
	public void loadWhiteListedCommands()
	{
		File file = new File(getDataFolder(), "whiteListedCommands.yml");
		if (!file.exists())
		{
			getLogger().info("Whitelisted commands file not found! Generating you a new one!");
			fileHelper.generateWhitelistedCmds();
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		List<String> whiteListedCommands = fc.getStringList("whiteListedCmds");
		for (String whiteListed : whiteListedCommands)
		{
			wcmd.addCommand(whiteListed);
		}
		
		getLogger().info("Loaded " + whiteListedCommands.size() + " Whitelisted Commands!");
	}
	
	public void loadConfig(String str)
	{
		File folder = new File(getDataFolder(), "configs");
		File file = new File(folder, str + "Config.yml");
		if (!file.exists())
		{
			getLogger().info("Arena config for \"" + str + "\" not found! Generating you a new one!");
			fileHelper.generateArenaConfig(str);
		}
		
		ArenaConfig a = new ArenaConfig(this, str, file);
		configs.add(a);
	}
	
	public void loadClasses() 
	{
		File folder = new File(getDataFolder(), "classes");
		File[] children = folder.listFiles();
		if (children.length == 0)
		{
			fileHelper.generateStockClasses();
			getLogger().info("No classes found! Generating stock classes!");
		}

		for (File file : children)
		{
			ArenaClass ac = new ArenaClass(this, file);
	        classes.add(ac);
		}
		
		getLogger().info("Loaded " + children.length + " class files!");
	}
	
	public ArenaConfig getConfig(String type) 
	{
		for (ArenaConfig ac : configs)
		{
			if (ac.arenaName.equalsIgnoreCase(type))
			{
				return ac;
			}
		}
		return null;
	}

	public void forceStop()
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena arena = activeArena.get(i);
			if (arena != null)
			{
				arena.startingAmount = 0;
				arena.stop();
			}
		}
		activeArena.clear();
	}
	
	public void forceStop(String str)
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena arena = activeArena.get(i);
			if (arena != null)
			{
				arena.forceStop = true;
				arena.stop();
			}
		}
	}
	
	public ArenaClass getArenaClass(String line)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.name.equalsIgnoreCase(line))
			{
				return ac;
			}
		}
		return null;
	}
	
	public void deleteArena(Player player, String str) 
	{
		File folder = new File(getDataFolder(), "arenas");
		File file = new File(folder, str + ".dat");
		if (file.exists())
		{
			forceStop(str);
				
			loadedArena.remove(getArenaZone(str));
				
			file.delete();
			
			player.sendMessage(ChatColor.YELLOW + "Successfully deleted arena: " + str + "!");
			getLogger().info("Successfully deleted arena: " + str + "!");
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Could not find an arena by the name of \"" + str + "\"!");
		}
	}
	
	public boolean isInArena(Block block) 
	{
		return isInArena(block.getLocation());
	}
	
	public Arena getArenaInside(Block block)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.checkLocation(block.getLocation()))
				return getArena(az.arenaName);
		}
		
		return null;
	}
	
	public boolean isInArena(Location loc)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.checkLocation(loc))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isInArena(Player player) 
	{
		return (getArenaPlayer(player) != null);
	}
	
	public void removeFromArena(Player player)
	{
		if (player != null) 
		{
			for (int i=0; i<activeArena.size(); i++)
			{
				Arena a = activeArena.get(i);
				a.startingAmount--;
				ArenaPlayer ap = a.getArenaPlayer(player);
				if (ap != null) 
				{
					a.arenaplayers.remove(ap);
				}
			}
		}
	}
	
	public void removeFromArena(String str) 
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			for (int ii=0; ii<a.arenaplayers.size(); ii++)
			{
				ArenaPlayer ap = a.arenaplayers.get(ii);
				if (ap != null)
				{
					Player player = Util.matchPlayer(ap.player.getName());
					if (player != null)
					{
						if (player.getName().equals(str))
						{
							a.arenaplayers.remove(ap);
						}
					}
				}
			}
		}
	}

	public ArenaPlayer getArenaPlayer(Player player) 
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			ArenaPlayer ap = a.getArenaPlayer(player);
			if (ap != null)
			{
				if (!ap.out) 
				{
					if (ap.player.getName().equals(player.getName())) 
					{
						return ap;
					}
				}
			}
		}
		return null;
	}

	public void fight(Player player, String name)
	{
		if (player == null)
			return;
		
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(ChatColor.RED + "You are in the middle of making an arena!");
			return;
		}
		
		if (!InventoryHelper.isEmpty(player.getInventory()))
		{
			// TODO: Store inventories?
			player.sendMessage(ChatColor.RED + "Please clear your inventory!");
			return;
		}
		
		ArenaZone a = getArenaZone(name);
		if (a == null)
		{
			player.sendMessage(ChatColor.RED + "That arena doesn't exist!");
			return;
		}
		
		if (isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "You're already in an arena!");
			return;
		}
		
		ArenaPlayer ap = getArenaPlayer(player);
		if (ap != null)
		{
			player.sendMessage(ChatColor.RED + "You cannot leave and rejoin an arena!");
			return;
		}
		
		for (int i = 0; i < waiting.size(); i++)
		{
			if (waiting.get(i).player.getName().equals(player.getName()))
			{
				player.sendMessage(ChatColor.RED + "You are already waiting!");
				return;
			}
		}
		
		RemindTask rmd = new RemindTask(player, name);
		rmd.runTaskLater(this, 40L); // TODO: Make time configurable / essentials dependant?
		player.sendMessage(ChatColor.GOLD + "Please stand still for 2 seconds!");
		waiting.add(rmd);				
	}
	
	public void joinBattle(boolean forced, Player player, String name) 
	{
		try
		{
			ArenaZone a = getArenaZone(name);
			if (getArena(name) != null)
			{
				if (getArena(name).starttimer < 1 && !forced) 
				{
					player.sendMessage(ChatColor.RED + "This arena has already started!");
				}
				else
				{
					Arena tojoin = getArena(name);
					int maxplayers = tojoin.az.maxPlayers;
					int players = tojoin.amtPlayersInArena;
					if (players + 1 <= maxplayers)
					{
						getArena(name).addPlayer(player);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "This arena is full, sorry!");
					}
				}
			}
			else
			{
				Arena ar = null;
				boolean disabled = false;
				for (Arena aar : activeArena)
				{
					if (aar.disabled && aar.az.equals(a))
					{
						disabled = true;
					}
				}
				for (ArenaZone aaz : loadedArena)
				{
					if (aaz.disabled && aaz.equals(a))
					{
						disabled = true;
					}
				}
				
				if (!disabled)
				{
					String arenaType = a.arenaType.toLowerCase();
					if (arenaType.equals("pvp"))
					{
						ar = new PVPArena(a);
					}
					else if (arenaType.equals("mob")) 
					{
						ar = new MOBArena(a);
					}
					else if (arenaType.equals("cq"))
					{
						ar = new CONQUESTArena(a);
					}
					else if (arenaType.equals("koth")) 
					{
						ar = new KOTHArena(a);
					}
					else if (arenaType.equals("bomb")) 
					{
						ar = new BOMBArena(a);
					}
					else if (arenaType.equals("ffa"))
					{
						ar = new FFAArena(a);
					}
					else if (arenaType.equals("hunger")) 
					{
						ar = new HUNGERArena(a);
					}
					else if (arenaType.equals("spleef")) 
					{
						ar = new SPLEEFArena(a);
					}
					else if (arenaType.equals("infect"))
					{
						ar = new INFECTArena(a);
					}
					else if (arenaType.equals("ctf"))
					{	
						ar = new CTFArena(a);
					}
					if (ar != null) 
					{
						activeArena.add(ar);
						ar.addPlayer(player);
						ar.announce();
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Error, This arena is disabled!");
				}
			}
		}
		catch(Exception e) 
		{
			getLogger().severe("Error while joining battle: " + e.getMessage());
		}
	}
	
	public Arena getArena(Player player)
	{
		for (Arena ac : activeArena)
		{
			ArenaPlayer ap = ac.getArenaPlayer(player);
			if (ap != null)
			{
				Player pl = Util.matchPlayer(ap.player.getName());
				if (pl != null && pl.isOnline())
				{
					if (pl.getName() == player.getName())
					{
						return ac;
					}
				}
			}
		}
		return null;
	}
	
	public Arena getArena(String name) 
	{
		for (Arena ac : activeArena)
		{
			if (ac.name.equals(name))
			{
				return ac;
			}
		}
		return null;
	}
	
	public ArenaZone getArenaZone(String name)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.arenaName.equals(name)) 
			{
				return az;
			}
		}
		return null;
	}
	
	public void setPoint(Player player) 
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null)
		{
			ac.setPoint(player);
			if (!(ac.msg.equals(""))) 
			{
				player.sendMessage(ChatColor.GRAY + ac.msg);
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Error, you aren't editing a field!");
		}
	}
	
	public void setDone(Player player)
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null) 
		{
			ac.setDone(player);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Error, you aren't editing a field!");
		}
	}
	
	public boolean isPlayerCreatingArena(Player player) 
	{
		return (getArenaCreator(player) != null);
	}
	
	public void stopCreatingArena(Player player)
	{ 
		for (int i=0; i<makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.player.equalsIgnoreCase(player.getName()))
			{
				makingArena.remove(ac);
				player.sendMessage(FormatUtil.format("&eStopping the creation of {0}!", ac.arenaName));
			}
		}
	}

	public ArenaCreator getArenaCreator(Player player)
	{
		for (int i=0; i<makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.player.equalsIgnoreCase(player.getName()))
			{
				return ac;
			}
		}
		return null;
	}
	
	public void createField(Player player, String name, String type)
	{
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(ChatColor.RED + "You are already creating an arena!");
			return;
		}
		
		if (!fieldTypes.contains(type.toLowerCase()))
		{
			player.sendMessage(ChatColor.RED + "This is not a valid field type!");
			return;
		}
		
		for (ArenaZone az : loadedArena)
		{
			if (az.arenaName.equalsIgnoreCase(name))
			{
				player.sendMessage(ChatColor.RED + "An arena by this name already exists!");
				return;
			}
		}
		
		getLogger().info(player.getName() + " is making arena " + name + ". Arena type: " + type);
		ArenaCreator ac = new ArenaCreator(this, player);
		ac.setArena(name, type);
		makingArena.add(ac);
	}
	
	public void normalizeAll()
	{
		for (Player player : getServer().getOnlinePlayers())
		{
			Location loc = player.getLocation();
			if (isInArena(loc))
			{
				normalize(player);
				if (isInArena(player))
				{
					removeFromArena(player);
				}
			}
		}
	}
	
	public void normalize(Player player)
	{
		PlayerInventory inv = player.getInventory();
		
		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	public void loadFiles() 
	{
		loadClasses();
		loadConfigs();
		loadArenas();
	}
	
	public void clearMemory()
	{
		savedPlayers.clear();
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		fieldTypes.clear();
		waiting.clear();
		classes.clear();
		configs.clear();
		wcmd.clear();
	}
	
	public class ArenaUpdater extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (int i = 0; i < activeArena.size(); i++) 
			{
				try
				{
					activeArena.get(i).step();
				}
				catch(Exception e) 
				{
					//
				}
			} 
		}
	}
	
	public class RemindTask extends BukkitRunnable 
	{
		public Player player;
		public String name;
		public int t;
		
		public RemindTask(Player player, String name) 
		{
			this.player = player;
			this.name = name;
		}

		@Override
		public void run() 
		{
			waiting.remove(this);
			joinBattle(false, player, name);
			this.cancel();
		}
	}

	public void removePotions(Player pl) 
	{
		for (PotionEffect effect : pl.getActivePotionEffects())
		{
			pl.removePotionEffect(effect.getType());
		}
	}
	
    /**Vault Check**/
	private void checkVault(PluginManager pm) 
	{
		if (pm.isPluginEnabled("Vault"))
			setupEconomy();
	}
	
    /**Set up vault economy**/
    private boolean setupEconomy() 
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) 
		{
			economy = ((Economy)economyProvider.getProvider());
		}
 
		return economy != null;
	}
}