package com.orange451.UltimateArena;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.orange451.UltimateArena.Arenas.*;
import com.orange451.UltimateArena.Arenas.Objects.*;
import com.orange451.UltimateArena.commands.*;
import com.orange451.UltimateArena.listeners.*;
import com.orange451.UltimateArena.util.Util;

public class UltimateArena extends JavaPlugin
{
	private @Getter Economy economy;
	private @Getter FileHelper fileHelper;
	
	private List<PBaseCommand> commands = new ArrayList<PBaseCommand>();
	public int maxArenasRunning = 1024;
	public int arenasPlayed = 0;
	public String uaAdmin = "ultimatearena.admin";
	public String uaBuilder = "ultimatearena.builder";
	public String uaUser = "ultimatearena.player";
	public List<ArenaClass> classes = new ArrayList<ArenaClass>();
	public List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	public List<ArenaZone> loadedArena = new ArrayList<ArenaZone>();
	public List<Arena> activeArena = new ArrayList<Arena>();
	public List<RemindTask> waiting = new ArrayList<RemindTask>();
	public List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	public List<String> fieldTypes = new ArrayList<String>();
	public List<String> loggedOut = new ArrayList<String>();
	public List<String> loggedOutInArena = new ArrayList<String>();
	public WhiteListCommands wcmd = new WhiteListCommands();
	public HashMap<Player, HashMap<Integer, Location>> savedPlayers;
	
	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();
 
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
		commands.add(new PCommandHelp(this));
		commands.add(new PCommandInfo(this));
		commands.add(new PCommandList(this));
		commands.add(new PCommandJoin(this));
		commands.add(new PCommandLeave(this));
		commands.add(new PCommandStats(this));
		commands.add(new PCommandLike(this));
		commands.add(new PCommandDislike(this));
		
		commands.add(new PCommandCreate(this));
		commands.add(new PCommandSetPoint(this));
		commands.add(new PCommandSetDone(this));
		commands.add(new PCommandDelete(this));
		commands.add(new PCommandStop(this));
		
		commands.add(new PCommandForceStop(this));
		commands.add(new PCommandRefresh(this));
		commands.add(new PCommandForceJoin(this));
		commands.add(new PCommandDisable(this));
		commands.add(new PCommandEnable(this));
		commands.add(new PCommandKick(this));
		commands.add(new PCommandStart(this));
		commands.add(new PCommandPause(this));
		
		commands.add(new PCommandClasses(this));
		
		fileHelper = new FileHelper(this);
		
		savedPlayers = fileHelper.savedPlayers();
		for (Player player : getServer().getOnlinePlayers())
		{
			if (savedPlayers.containsKey(player))
			{
				for (Entry<Player, HashMap<Integer, Location>> entrySet : savedPlayers.entrySet())
				{
					if (player.getName() == entrySet.getKey().getName())
					{
						if (player != null && player.isOnline())
						{
							int exp = 0;
							Location loc = null;
							HashMap<Integer, Location> hashMap = entrySet.getValue();
							for (Entry<Integer, Location> entrySet2 : hashMap.entrySet())
							{
								exp = entrySet2.getKey();
								loc = entrySet2.getValue();
							}
							
							normalize(player);
							player.setExp(exp);
							player.teleport(loc);
							removePotions(player);
						}
					}
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
		
		fileHelper.onDisable(activeArena);
		
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));
		
		if (commandLabel.equals("tt"))
		{
			testAPI tt = new testAPI();
			if (sender instanceof Player)
				tt.test((Player)sender);
			else
				getLogger().warning("This can only be used by players!");
			return false;
		}
		
		this.handleCommand(sender, parameters);
		return true;
	}
	
	public void handleCommand(CommandSender sender, List<String> parameters) 
	{
		if (parameters.size() == 0)
		{
			this.commands.get(0).execute(sender, parameters);
			return;
		}
		
		String commandName = parameters.get(0).toLowerCase();
		
		for (PBaseCommand fcommand : this.commands) 
		{
			if (fcommand.getAliases().contains(commandName)) 
			{
				fcommand.execute(sender, parameters);
				return;
			}
		}
		
		sender.sendMessage(ChatColor.YELLOW + "Unknown UltimateArena command \""+commandName+"\". Try /ua help");
	}
	
	public List<PBaseCommand> getCommands() 
	{
		return commands;
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
		
		if (this.isInArena(player))
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
						loggedOut.add(player.getName());
						this.removeFromArena(player.getName());
					}
				}
			}
		}
	}
	
	public void onJoin(Player player) 
	{
		if (savedPlayers.containsKey(player))
		{
			for (Entry<Player, HashMap<Integer, Location>> entrySet : savedPlayers.entrySet())
			{
				if (player.getName() == entrySet.getKey().getName())
				{
					if (player != null && player.isOnline())
					{
						int exp = 0;
						Location loc = null;
						HashMap<Integer, Location> hashMap = entrySet.getValue();
						for (Entry<Integer, Location> entrySet2 : hashMap.entrySet())
						{
							exp = entrySet2.getKey();
							loc = entrySet2.getValue();
						}
						
						normalize(player);
						player.setExp(exp);
						player.teleport(loc);
						removePotions(player);
					}
				}
			}
		}
		
		if (!this.isInArena(player))
		{
			if (this.isInArena(player.getLocation())) 
			{
				normalize(player);
				removePotions(player);
			}
		}
		
		boolean found = false;
		for (int i = 0; i < loggedOut.size(); i++) 
		{
			if (loggedOut.get(i).equals(player.getName())) 
			{
				found = true;
			}
		}
		
		if (found) 
		{
			Arena ar = this.getArena(player);
			if (ar == null)
			{
				loggedOut.remove(player.getName());
				this.normalize(player);
			}
			else
			{
				if (ar.starttimer > 0) 
				{
					loggedOut.remove(player.getName());
					ArenaPlayer ap = this.getArenaPlayer(player);
					if (ap != null) 
					{
						ar.endPlayer(ap, true);
					}
					else
					{
						this.normalize(player);
					}
				}
			}
		}
	}
	
	public void leaveArena(Player player)
	{
		if (this.isInArena(player))
		{
			Arena a = this.getArena(player);
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
		        	this.loadedArena.add(az);
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
		for (Arena arena : activeArena)
		{
			try
			{
				arena.startingAmount = 0;
				arena.stop();
			}
			catch (Exception e)
			{
				getLogger().severe("Error forcing stop: " + e.getMessage());
			}
		}
		activeArena.clear();
	}
	
	public void forceStop(String str)
	{
		Arena a = null;
		for (Arena arena : activeArena)
		{
			if (arena.name.equalsIgnoreCase(str))
			{
				a = arena;
			}
		}
		
		if (a != null) 
		{
			a.forceStop = true;
			a.stop();
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
		if (getArenaPlayer(player) != null) 
		{
			return true;
		}
		return false;
	}
	
	public void removeFromArena(Player player)
	{
		if (player != null) 
		{
			for (Arena ac : activeArena)
			{
				ac.startingAmount--;
				ArenaPlayer ap = ac.getArenaPlayer(player);
				if (ap != null) 
				{
					ac.arenaplayers.remove(ap);
				}
			}
		}
	}
	
	public void removeFromArena(String str) 
	{
		for (Arena ac : activeArena)
		{
			for (ArenaPlayer ap : ac.arenaplayers)
			{
				if (ap != null)
				{
					if (ap.player.getName().equals(str))
					{
						ac.arenaplayers.remove(ap);
					}
				}
			}
		}
	}

	public ArenaPlayer getArenaPlayer(Player player) 
	{
		try
		{
			for (Arena ac : activeArena)
			{
				ArenaPlayer ap = ac.getArenaPlayer(player);
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
		}
		catch(Exception e)
		{
			getLogger().severe("Error getting arena player: " + e.getMessage());
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
		if (getArenaCreator(player) != null) 
		{
			return true;
		}
		return false;
	}
	
	public void stopCreatingArena(Player player)
	{ 
		for (ArenaCreator ac : makingArena)
		{
			if (ac.player.equalsIgnoreCase(player.getName()))
				makingArena.remove(ac);
		}
	}

	public ArenaCreator getArenaCreator(Player player)
	{
		for (ArenaCreator ac : makingArena)
		{
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
		commands.clear();
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		fieldTypes.clear();
		classes.clear();
		loggedOut.clear();
		loggedOutInArena.clear();
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
		pl.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		pl.removePotionEffect(PotionEffectType.SPEED);
		pl.removePotionEffect(PotionEffectType.SLOW);
		pl.removePotionEffect(PotionEffectType.JUMP);
		pl.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		pl.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
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