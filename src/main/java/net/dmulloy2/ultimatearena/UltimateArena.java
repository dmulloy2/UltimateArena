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
package net.dmulloy2.ultimatearena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.ultimatearena.arenas.*;
import net.dmulloy2.ultimatearena.arenas.objects.*;
import net.dmulloy2.ultimatearena.commands.*;
import net.dmulloy2.ultimatearena.listeners.*;
import net.dmulloy2.ultimatearena.permissions.*;
import net.dmulloy2.ultimatearena.util.*;
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

public class UltimateArena extends JavaPlugin
{
	private @Getter FileHelper fileHelper;
	private @Getter Economy economy;
	
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	
	public List<ArenaJoinTask> waiting = new ArrayList<ArenaJoinTask>();
	public List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	public List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	public List<ArenaClass> classes = new ArrayList<ArenaClass>();
	public List<ArenaSign> arenaSigns = new ArrayList<ArenaSign>();
	public List<ArenaZone> loadedArena = new ArrayList<ArenaZone>();
	public List<Arena> activeArena = new ArrayList<Arena>();
	
	public WhiteListCommands wcmd = new WhiteListCommands();
	
	public int arenasPlayed = 0;
	
	private @Getter String prefix = ChatColor.GOLD + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "UA" + ChatColor.GOLD + "] ";

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// IO Stuff
		checkDirectories();
		saveDefaultConfig();

		// Register Handlers and Helpers
		permissionHandler =  new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
		
		fileHelper = new FileHelper(this);

		// Add Commands
		commandHandler.setCommandPrefix("ua");
		commandHandler.registerCommand(new CmdHelp(this));
		commandHandler.registerCommand(new CmdInfo(this));
		commandHandler.registerCommand(new CmdList(this));
		commandHandler.registerCommand(new CmdJoin(this));
		commandHandler.registerCommand(new CmdLeave(this));
		commandHandler.registerCommand(new CmdStats(this));
		commandHandler.registerCommand(new CmdLike(this));
		commandHandler.registerCommand(new CmdDislike(this));
		
		commandHandler.registerCommand(new CmdCreate(this));
		commandHandler.registerCommand(new CmdSetPoint(this));
		commandHandler.registerCommand(new CmdSetDone(this));
		commandHandler.registerCommand(new CmdDelete(this));
		commandHandler.registerCommand(new CmdStop(this));
		
		commandHandler.registerCommand(new CmdForceStop(this));
		commandHandler.registerCommand(new CmdReload(this));
		commandHandler.registerCommand(new CmdForceJoin(this));
		commandHandler.registerCommand(new CmdDisable(this));
		commandHandler.registerCommand(new CmdEnable(this));
		commandHandler.registerCommand(new CmdKick(this));
		commandHandler.registerCommand(new CmdStart(this));
		commandHandler.registerCommand(new CmdPause(this));
		
		commandHandler.registerCommand(new CmdClassList(this));
		commandHandler.registerCommand(new CmdClass(this));
		
		// SwornGuns
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("SwornGuns"))
		{
			pm.registerEvents(new SwornGunsListener(this), this);
			debug("Found SwornGuns, enabling SwornGunsListener!");
		}
		
		// Register Other Listeners
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);
		
		// Vault
		checkVault(pm);

		// Arena Updater
		new ArenaUpdateTask().runTaskTimer(this, 2L, 20L);

		// Load Arenas
		loadFiles();
		
		// Arena Signs
		arenaSigns = fileHelper.loadSigns();
		outConsole("Loaded {0} arena signs!", arenaSigns.size());
		
		new SignUpdateTask().runTaskTimer(this, 2L, 20L);
		
		long finish = System.currentTimeMillis();
		
		outConsole("{0} has been enabled ({1}ms)", getDescription().getFullName(), finish - start);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();
		
		// Unregister
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);

		// Stop all arenas
		stopAll();
		
		// Save Signs
		for (ArenaSign sign : arenaSigns)
		{
			sign.save();
			debug("Saved sign {0} on shutdown!", sign.getId());
		}
		
		// Clear Memory
		clearMemory();
		
		long finish = System.currentTimeMillis();
		
		outConsole("{0} has been disabled ({1}ms)", getDescription().getFullName(), finish - start);
	}
	
	// Console logging
	public void outConsole(Level level, String string, Object...objects)
	{
		String out = FormatUtil.formatLog(string, objects);
		getLogger().log(level, out);
	}
	
	
	public void outConsole(String string, Object...objects)
	{
		outConsole(Level.INFO, string, objects);
	}
	
	public void debug(String string, Object...objects)
	{
		if (getConfig().getBoolean("debug", false))
		{
			outConsole(Level.INFO, "[Debug] " + string, objects);
		}
	}
	
	public void broadcast(String string, Object...objects)
	{
		String broadcast = FormatUtil.format(string, objects);
		getServer().broadcastMessage(prefix + broadcast);
		
		debug("Broadcasted message: {0}", broadcast);
	}
	
	// Create Directories
	public void checkDirectories()
	{
		File arenaFile = new File(getDataFolder(), "arenas");
		if (!arenaFile.exists())
		{
			arenaFile.mkdir();
			debug("Created arenas directory!");
		}
		
		File playersFile = new File(getDataFolder(), "players");
		if (playersFile.exists())
		{
			File[] children = playersFile.listFiles();
			if (children != null && children.length > 0)
			{
				for (File file : children)
				{
					file.delete();
				}
			}
			
			playersFile.delete();
			debug("Deleted players directory!");
		}
		
		File classFile = new File(getDataFolder(), "classes");
		if (!classFile.exists())
		{
			classFile.mkdir();
			debug("Created classes directory!");
		}
		
		File configsFile = new File(getDataFolder(), "configs");
		if (!configsFile.exists())
		{
			configsFile.mkdir();
			debug("Created configs directory!");
		}
	}

	// Normalize player if in arena
	public void onQuit(Player player)
	{
		if (isPlayerCreatingArena(player)) 
		{
			debug("Player {0} left the game, stopping the creation of an arena", player.getName());
			makingArena.remove(getArenaCreator(player));
		}
		
		if (isInArena(player))
		{
			debug("Player {0} leaving arena from quit", player.getName());
			leaveArena(player);
		}
	}

	public void leaveArena(Player player)
	{
		if (isInArena(player))
		{
			Arena a = getArena(player);
			a.endPlayer(getArenaPlayer(player), false);
			a.tellPlayers("&b{0} has left the arena!", player.getName());
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Error, you are not in an arena");
		}
	}
	
	public void loadArenas()
	{
		File folder = new File(getDataFolder(), "arenas");
		File[] children = folder.listFiles();
		
		int total = 0;
		for (File file : children)
		{
			ArenaZone az = new ArenaZone(this, file);
			if (az.isLoaded())
			{
				loadedArena.add(az);
				debug("Successfully loaded arena {0}!", az.getArenaName());
				total++;
			}
		}
		
		outConsole("Loaded {0} arena files!", total);
	}
	
	public void loadConfigs() 
	{
		int total = 0;
		for (FieldType type : FieldType.values())
		{
			if (loadConfig(type.name))
				total++;
		}
		
		outConsole("Loaded {0} arena config files!", total);
		
		loadWhiteListedCommands();
	}
	
	public void loadWhiteListedCommands()
	{
		File file = new File(getDataFolder(), "whiteListedCommands.yml");
		if (!file.exists())
		{
			outConsole("Whitelisted commands file not found! Generating you a new one!");
			fileHelper.generateWhitelistedCmds();
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		List<String> whiteListedCommands = fc.getStringList("whiteListedCmds");
		for (String whiteListed : whiteListedCommands)
		{
			wcmd.addCommand(whiteListed);
			debug("Added whitelisted command: \"{0}\"!", whiteListed);
		}
		
		outConsole("Loaded {0} whitelisted commands!", wcmd.size());
	}
	
	public boolean loadConfig(String str)
	{
		File folder = new File(getDataFolder(), "configs");
		File file = new File(folder, str + "Config.yml");
		if (!file.exists())
		{
			outConsole("Could not find config for arena type \"{0}\"! Generating a new one!", str);
			fileHelper.generateArenaConfig(str);
		}
		
		ArenaConfig a = new ArenaConfig(this, str, file);
		if (a.isLoaded())
		{
			configs.add(a);
			return true;
		}
		
		return false;
	}
	
	public void loadClasses() 
	{
		File folder = new File(getDataFolder(), "classes");
		File[] children = folder.listFiles();
		if (children.length == 0)
		{
			fileHelper.generateStockClasses();
			outConsole("No classes found! Generating stock classes!");
		}

		children = folder.listFiles();

		int total = 0;
		for (File file : children)
		{
			ArenaClass ac = new ArenaClass(this, file);
			if (ac.isLoaded())
			{
				classes.add(ac);
				total++;
			}
		}
		
		outConsole("Loaded {0} Arena Classes!", total);
	}
	
	public ArenaConfig getConfig(String type) 
	{
		for (ArenaConfig ac : configs)
		{
			if (ac.getArenaName().equalsIgnoreCase(type))
				return ac;
		}
		
		return null;
	}

	public void stopAll()
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena arena = activeArena.get(i);
			if (arena != null)
			{
				arena.stop();
			}
		}
		activeArena.clear();
	}

	public ArenaSign getArenaSign(Location loc)
	{
		for (ArenaSign sign : arenaSigns)
		{
			if (Util.checkLocation(sign.getLocation(), loc))
				return sign;
		}
		
		return null;
	}
	
	public void deleteSign(ArenaSign sign)
	{
		debug("Deleting sign {0}!", sign.getId());
		
		arenaSigns.remove(sign);

		fileHelper.refreshSignSave();
	}
	
	public ArenaClass getArenaClass(String line)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.getName().equalsIgnoreCase(line))
				return ac;
		}
		
		return null;
	}
	
	public void deleteArena(Player player, String str) 
	{
		File folder = new File(getDataFolder(), "arenas");
		File file = new File(folder, str + ".dat");
		if (file.exists())
		{
			for (int i = 0; i < activeArena.size(); i++)
			{
				Arena a = activeArena.get(i);
				if (a.getName().equalsIgnoreCase(str))
				{
					a.stop();
				}
			}
				
			loadedArena.remove(getArenaZone(str));
				
			file.delete();
			
			for (ArenaSign as : arenaSigns)
			{
				if (as.getArena().equalsIgnoreCase(str))
				{
					deleteSign(as);
				}
			}
			
			player.sendMessage(prefix + ChatColor.YELLOW + "Successfully deleted arena: " + str + "!");
			
			outConsole("Successfully deleted arena: {0}!", str);
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Could not find an arena by the name of \"" + str + "\"!");
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
				return getArena(az.getArenaName());
		}
		
		return null;
	}
	
	public boolean isInArena(Location loc)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.checkLocation(loc))
				return true;
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
				a.setStartingAmount(a.getStartingAmount() - 1);
				ArenaPlayer ap = a.getArenaPlayer(player);
				if (ap != null) 
				{
					a.getArenaPlayers().remove(ap);
				}
			}
		}
	}
	
	public void removeFromArena(String str) 
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			for (int ii=0; ii<a.getArenaPlayers().size(); ii++)
			{
				ArenaPlayer ap = a.getArenaPlayers().get(ii);
				if (ap != null)
				{
					Player player = Util.matchPlayer(ap.getPlayer().getName());
					if (player != null)
					{
						if (player.getName().equals(str))
						{
							a.getArenaPlayers().remove(ap);
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
			if (ap != null && !ap.isOut())
			{
				if (ap.getPlayer().getName().equals(player.getName())) 
					return ap;
			}
		}
		
		return null;
	}

	public void fight(Player player, String name)
	{
		if (!permissionHandler.hasPermission(player, PermissionType.JOIN.permission))
		{
			player.sendMessage(prefix + ChatColor.RED + "You do not have permission to do this!");
			return;
		}
		
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(prefix + ChatColor.RED + "You are in the middle of making an arena!");
			return;
		}
		
		if (!InventoryHelper.isEmpty(player.getInventory()))
		{
			if (!getConfig().getBoolean("saveInventories"))
			{
				player.sendMessage(prefix + ChatColor.RED + "Please clear your inventory!");
				return;
			}
		}
		
		ArenaZone a = getArenaZone(name);
		if (a == null)
		{
			player.sendMessage(prefix + ChatColor.RED + "That arena doesn't exist!");
			return;
		}
		
		if (isInArena(player))
		{
			player.sendMessage(prefix + ChatColor.RED + "You're already in an arena!");
			return;
		}
		
		ArenaPlayer ap = getArenaPlayer(player);
		if (ap != null)
		{
			player.sendMessage(prefix + ChatColor.RED + "You cannot leave and rejoin an arena!");
			return;
		}
		
		for (ArenaJoinTask task : waiting)
		{
			if (task.getPlayer().getName().equals(player.getName()))
			{
				player.sendMessage(prefix + ChatColor.RED + "You are already waiting!");
				return;
			}
		}
		
		ArenaJoinTask join = new ArenaJoinTask(this, player, name);
		if (getConfig().getBoolean("joinTimer.enabled"))
		{
			int seconds = getConfig().getInt("joinTimer.wait");
			int wait = seconds * 20;
			
			join.runTaskLater(this, wait);
			waiting.add(join);
			
			String message = FormatUtil.format(prefix + "&6Please stand still for {0} seconds!", seconds);
			player.sendMessage(message);
		}
		else
		{
			join.run();
		}			
	}
	
	public void joinBattle(boolean forced, Player player, String name) 
	{
		debug("Player {0} is attempting to join arena {1}. Forced: {2}", player.getName(), name, forced);
		
		ArenaZone a = getArenaZone(name);
		if (getArena(name) != null)
		{
			if (getArena(name).getStarttimer() < 1 && !forced) 
			{
				player.sendMessage(prefix + ChatColor.RED + "This arena has already started!");
			}
			else
			{
				Arena tojoin = getArena(name);
				int maxplayers = tojoin.getArenaZone().getMaxPlayers();
				int players = tojoin.getAmtPlayersInArena();
				if (players + 1 <= maxplayers)
				{
					getArena(name).addPlayer(player);
				}
				else
				{
					player.sendMessage(prefix + ChatColor.RED + "This arena is full, sorry!");
				}
			}
		}
		else
		{
			Arena ar = null;
			boolean disabled = false;
			for (Arena aar : activeArena)
			{
				if (aar.isDisabled() && aar.getArenaZone().equals(a))
				{
					disabled = true;
				}
			}
			for (ArenaZone aaz : loadedArena)
			{
				if (aaz.isDisabled() && aaz.equals(a))
				{
					disabled = true;
				}
			}
			
			if (!disabled)
			{
				String arenaType = a.getType().name().toLowerCase();
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
				player.sendMessage(prefix + ChatColor.RED + "Error, This arena is disabled!");
			}
		}
	}
	
	public Arena getArena(Player player)
	{
		for (Arena ac : activeArena)
		{
			ArenaPlayer ap = ac.getArenaPlayer(player);
			if (ap != null)
			{
				Player pl = Util.matchPlayer(ap.getPlayer().getName());
				if (pl != null && pl.isOnline())
				{
					if (pl.getName().equals(player.getName()))
						return ac;
				}
			}
		}
		
		return null;
	}
	
	public Arena getArena(String name) 
	{
		for (Arena ac : activeArena)
		{
			if (ac.getName().equals(name))
				return ac;
		}
		
		return null;
	}
	
	public ArenaZone getArenaZone(String name)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.getArenaName().equals(name)) 
				return az;
		}
		
		return null;
	}
	
	public void setPoint(Player player) 
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null)
		{
			ac.setPoint(player);
			if (!ac.getMsg().equals(""))
			{
				player.sendMessage(prefix + ChatColor.GRAY + ac.getMsg());
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Error, you aren't editing a field!");
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
			player.sendMessage(prefix + ChatColor.RED + "Error, you aren't editing a field!");
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
			if (ac.getPlayer().equalsIgnoreCase(player.getName()))
			{
				makingArena.remove(ac);
				player.sendMessage(prefix + FormatUtil.format("&eStopping the creation of {0}!", ac.getArenaName()));
			}
		}
	}

	public ArenaCreator getArenaCreator(Player player)
	{
		for (int i=0; i<makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.getPlayer().equalsIgnoreCase(player.getName()))
				return ac;
		}
		
		return null;
	}
	
	public void createField(Player player, String name, String type)
	{
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(prefix + ChatColor.RED + "You are already creating an arena!");
			return;
		}
		
		if (!FieldType.contains(type.toLowerCase()))
		{
			player.sendMessage(prefix + ChatColor.RED + "This is not a valid field type!");
			return;
		}
		
		for (ArenaZone az : loadedArena)
		{
			if (az.getArenaName().equalsIgnoreCase(name))
			{
				player.sendMessage(prefix + ChatColor.RED + "An arena by this name already exists!");
				return;
			}
		}
		
		outConsole("Player {0} has started the creation of {1}. Type: {2}", player.getName(), name, type);

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
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		arenaSigns.clear();
		waiting.clear();
		classes.clear();
		configs.clear();
		wcmd.clear();
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
		{
			setupEconomy();
			
			if (economy != null)
			{
				outConsole("Economy integration through {0} was succesful!", economy.getName());
			}
		}
	}
	
    /**Set up vault economy**/
    private boolean setupEconomy() 
	{
    	debug("Setting up Vault economy");
    	
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) 
		{
			economy = ((Economy)economyProvider.getProvider());
		}
 
		return economy != null;
	}
    
    /** Updaters **/
    public class ArenaUpdateTask extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (int i = 0; i < activeArena.size(); i++) 
			{
				Arena arena = activeArena.get(i);
				arena.update();
			}
		}
	}
    
    public class SignUpdateTask extends BukkitRunnable
    {
    	@Override
    	public void run()
    	{
    		for (int i = 0; i < arenaSigns.size(); i++)
    		{
    			ArenaSign sign = arenaSigns.get(i);
    			sign.update();
    		}
    	}
    }
}