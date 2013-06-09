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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	
	public int maxArenasRunning = 1024;
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
	public List<SavedArenaPlayer> loggedOutPlayers = new ArrayList<SavedArenaPlayer>();
	
	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();
		
		permissionHandler =  new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
 
		File dir = getDataFolder();
		if (!dir.exists()) 
		{
			dir.mkdir();
		}
		
		File dir2 = new File(getDataFolder().getAbsolutePath() + "/arenas");
		if (!dir2.exists())
		{
			dir2.mkdir();
		}
		
		saveDefaultConfig();
		
		//Add fields
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

		//Add Commands
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
		
		fileHelper = new FileHelper(this);
		
		savedPlayers = fileHelper.getSavedPlayers();
		for (Player player : getServer().getOnlinePlayers())
		{
			for (SavedArenaPlayer savedArenaPlayer : savedPlayers)
			{
				if (savedArenaPlayer.getPlayer().getName().equals(player.getName()))
				{
					int exp = savedArenaPlayer.getExp();
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

		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("PVPGunPlus"))
			pm.registerEvents(new PVPGunPlusListener (this), this);
			
		pm.registerEvents(new PluginEntityListener(this), this);
		pm.registerEvents(new PluginBlockListener(this), this);
		pm.registerEvents(new PluginPlayerListener(this), this);
		
		Util.Initialize(this);
 
		new ArenaUpdater().runTaskTimer(this, 2L, 20L);
			
		checkVault(pm);

		loadFiles();
		
		long finish = System.currentTimeMillis();
		
		getLogger().info(getDescription().getFullName() + " has been enabled ("+(finish-start)+"ms)");
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();
		
		fileHelper.savePlayers(activeArena, loggedOutPlayers);
		
		for (int i=0; i<activeArena.size(); i++)
		{
			try
			{
				activeArena.get(i).stop();
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
	
	public File getRoot() 
	{
		return getDataFolder();
	}

	public void onQuit(Player player)
	{
		if (isPlayerCreatingArena(player)) 
		{
			makingArena.remove(getArenaCreator(player));
		}
		
		if (isInArena(player))
		{
			Arena ar = this.getArena(player);
			ArenaPlayer ap = this.getArenaPlayer(player);
			if (ap != null)
			{
				getLogger().info("Player " + player.getName() + " leaving arena " + ar.name + " from quit");
				if (ar != null) 
				{
					if (ar.starttimer > 0 && (!ap.out)) 
					{
						int exp = Integer.valueOf(Math.round(ap.startxp));
						SavedArenaPlayer loggedOut = new SavedArenaPlayer(player, exp, ap.spawnBack);
						loggedOutPlayers.add(loggedOut);
						
						removeFromArena(player.getName());
					}
				}
			}
		}
	}
	
	public void onJoin(Player player) 
	{
		/**Normalize Players from Shutdown**/
		for (SavedArenaPlayer savedArenaPlayer : savedPlayers)
		{
			if (savedArenaPlayer.getPlayer().getName().equals(player.getName()))
			{
				int exp = savedArenaPlayer.getExp();
				Location loc = savedArenaPlayer.getLocation();
				
				normalize(player);
				player.setExp(exp);
				player.teleport(loc);
				removePotions(player);
				
				fileHelper.deletePlayer(player);
				
				savedPlayers.remove(savedArenaPlayer);
			}
		}

		/**Normalize Players from Quit**/
		for (SavedArenaPlayer loggedOutPlayer : loggedOutPlayers)
		{
			if (loggedOutPlayer.getPlayer().getName().equals(player.getName()))
			{
				int exp = loggedOutPlayer.getExp();
				Location loc = loggedOutPlayer.getLocation();
				
				normalize(player);
				player.setExp(exp);
				player.teleport(loc);
				removePotions(player);
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
		String path = getRoot().getAbsolutePath() + "/arenas";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null) 
		{
		    for (int i=0; i<children.length; i++)
		    {
		        String filename = children[i];
		        ArenaZone az = new ArenaZone(this, new File(path + "/" + filename));
		        if (az.loaded) 
		        {
		        	loadedArena.add(az);
		        }
		    }
		}
	}
	
	public void loadConfigs() 
	{
		for (int i = 0; i < fieldTypes.size(); i++) 
		{
			loadConfig(fieldTypes.get(i));
		}
		
		loadWhiteListedCommands();
	}
	
	public void loadWhiteListedCommands()
	{
		String path = getRoot().getAbsolutePath() + "/whiteListedCommands.txt";
		File f = new File(path);
		if (!f.exists()) 
		{
			getLogger().info("Whitelisted commands file not found! Generating you a new one!");
			fileHelper.generateWhitelistedCmds();
			
		}
		
		try
		{
			FileInputStream fstream = new FileInputStream(f.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) 
			{
				wcmd.addCommand(strLine);
			}
			br.close();
			in.close();
			fstream.close();
		}
		catch (Exception e)
		{
			getLogger().severe("Error loading whitelisted commands: " + e.getMessage());
		}
	}
	
	public void loadConfig(String str)
	{
		String path = getRoot().getAbsolutePath() + "/" + str + "CONFIG.txt";
		File f = new File(path);
		if (!f.exists())
		{
			getLogger().info("Arena config for \"" + str + "\" not found! Generating you a new one!");
			fileHelper.generateArenaConfig(str);
		}
		
		ArenaConfig a = new ArenaConfig(this, str, f);
		configs.add(a);
		getLogger().info("Loaded configuration for arena type: " + str);
	}
	
	public void loadClasses() 
	{
		String path = getRoot().getAbsolutePath() + "/classes";
		File dir = new File(path);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		
		String[] children = dir.list();
		if (children.length == 0)
		{
			fileHelper.generateStockClasses();
			getLogger().info("No classes found! Generating stock classes!");
		}
		
		dir = new File(path);
		children = dir.list();
		
		for (String filename : children)
		{
			ArenaClass ac = new ArenaClass(this, new File(path + "/" + filename));
	        ac.name = filename;
	        this.classes.add(ac);
		}
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
		try
		{
			String path = getRoot().getAbsolutePath() + "/arenas";
			File f = new File(path + "/" + str);
			if (f.exists()) 
			{
				f.delete();
			}
			forceStop(str);
			this.loadedArena.remove(this.getArenaZone(str));
			player.sendMessage(ChatColor.YELLOW + "Deleted arena!");
			getLogger().info("Deleted arena: " + str);
		}
		catch(Exception e) 
		{
			player.sendMessage(ChatColor.RED + "Failed to delete arena!");
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
			for (int ii=0; i<a.arenaplayers.size(); ii++)
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
		if (player != null) 
		{
			if (player.isOnline())
			{
				if (!(isPlayerCreatingArena(player)))
				{
					if (InventoryHelper.isEmpty(player.getInventory())) 
					{
						ArenaZone a = getArenaZone(name);
						if (a != null)
						{
							if (!isInArena(player)) 
							{
								ArenaPlayer apl = this.getArenaPlayer(player);
								if (apl == null) 
								{
									boolean found = false;
									for (int i = 0; i < waiting.size(); i++)
									{
										if (waiting.get(i).player.getName().equals(player.getName()))
										{
											found = true;
										}
									}
									if (!found)
									{
										RemindTask rmd = new RemindTask(player, name);
										rmd.runTaskLater(this, 40L);
										player.sendMessage(ChatColor.GOLD + "Please stand still for 2 seconds!");
										this.waiting.add(rmd);				
									}
									else
									{
										player.sendMessage(ChatColor.RED + "You are already waiting!");
									}
								}
								else
								{
									player.sendMessage(ChatColor.RED + "You cannot leave and rejoin an arena!");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "You're already in an arena!");
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "That arena doesn't exist!");
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "Please clear your inventory!");
					}
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + "You are in the middle of making an arena!");
			}
		}
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
		if (!(isPlayerCreatingArena(player))) 
		{
			boolean found = false;
			for (int i = 0; i < fieldTypes.size(); i++) 
			{
				if (fieldTypes.get(i).equalsIgnoreCase(type)) 
				{
					found = true;
				}
			}
			if (found)
			{
				getLogger().info(player.getName() + " is making arena " + name + ". Arena type: " + type);
				ArenaCreator ac = new ArenaCreator(this, player);
				ac.setArena(name, type);
				makingArena.add(ac);
			}
			else
			{
				player.sendMessage(ChatColor.RED + "This is not a valid field type!");
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You are already creating an arena!");
		}
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
	
	public void normalize(Player p)
	{
		try
		{
			p.getInventory().clear();
			p.getInventory().setHelmet(null);
			p.getInventory().setChestplate(null);
			p.getInventory().setLeggings(null);
			p.getInventory().setBoots(null);
		}
		catch(Exception e)
		{
			getLogger().severe("Error normalizing player ("+p.getName()+"): " + e);
		}
	}

	public void loadFiles() 
	{
		loadClasses();
		loadConfigs();
		loadArenas();
	}
	
	public void clearMemory()
	{
		loggedOutPlayers.clear();
		savedPlayers.clear();
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		fieldTypes.clear();
		classes.clear();
		configs.clear();
		wcmd.clear();
	}
	
	public class ArenaUpdater extends BukkitRunnable
	{
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
					//fuck you
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