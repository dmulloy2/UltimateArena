package com.orange451.UltimateArena;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.BOMBArena;
import com.orange451.UltimateArena.Arenas.CONQUESTArena;
import com.orange451.UltimateArena.Arenas.CTFArena;
import com.orange451.UltimateArena.Arenas.FFAArena;
import com.orange451.UltimateArena.Arenas.HUNGERArena;
import com.orange451.UltimateArena.Arenas.INFECTArena;
import com.orange451.UltimateArena.Arenas.KOTHArena;
import com.orange451.UltimateArena.Arenas.MOBArena;
import com.orange451.UltimateArena.Arenas.PVPArena;
import com.orange451.UltimateArena.Arenas.SPLEEFArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaClass;
import com.orange451.UltimateArena.Arenas.Objects.ArenaConfig;
import com.orange451.UltimateArena.Arenas.Objects.ArenaCreator;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.Arenas.Objects.WhiteListCommands;
import com.orange451.UltimateArena.PermissionInterface.PermissionInterface;
import com.orange451.UltimateArena.commands.PBaseCommand;
import com.orange451.UltimateArena.commands.PCommandCreate;
import com.orange451.UltimateArena.commands.PCommandDelete;
import com.orange451.UltimateArena.commands.PCommandDisable;
import com.orange451.UltimateArena.commands.PCommandDislike;
import com.orange451.UltimateArena.commands.PCommandEnable;
import com.orange451.UltimateArena.commands.PCommandForceJoin;
import com.orange451.UltimateArena.commands.PCommandForceStop;
import com.orange451.UltimateArena.commands.PCommandHelp;
import com.orange451.UltimateArena.commands.PCommandInfo;
import com.orange451.UltimateArena.commands.PCommandJoin;
import com.orange451.UltimateArena.commands.PCommandKick;
import com.orange451.UltimateArena.commands.PCommandLeave;
import com.orange451.UltimateArena.commands.PCommandLike;
import com.orange451.UltimateArena.commands.PCommandList;
import com.orange451.UltimateArena.commands.PCommandPause;
import com.orange451.UltimateArena.commands.PCommandRefresh;
import com.orange451.UltimateArena.commands.PCommandSetDone;
import com.orange451.UltimateArena.commands.PCommandSetPoint;
import com.orange451.UltimateArena.commands.PCommandStart;
import com.orange451.UltimateArena.commands.PCommandStats;
import com.orange451.UltimateArena.commands.PCommandStop;
import com.orange451.UltimateArena.listeners.PluginBlockListener;
import com.orange451.UltimateArena.listeners.PluginEntityListener;
import com.orange451.UltimateArena.listeners.PluginPlayerListener;
import com.orange451.UltimateArena.util.Util;

public class UltimateArena extends JavaPlugin {

	private PluginBlockListener blockListener = new PluginBlockListener(this);
	private PluginPlayerListener playerListener = new PluginPlayerListener(this);
	private PluginEntityListener entityListener = new PluginEntityListener(this);
	private List<PBaseCommand> commands = new ArrayList<PBaseCommand>();
	private boolean loaded = false;
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
	public ArrayList<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	public ArrayList<String> fieldTypes = new ArrayList<String>();
	public ArrayList<String> loggedOut = new ArrayList<String>();
	public ArrayList<String> loggedOutInArena = new ArrayList<String>();
	public ArrayList<String> isUAAdmin = new ArrayList<String>();
	public WhiteListCommands wcmd = new WhiteListCommands();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> parameters = new ArrayList<String>(Arrays.asList(args));
		
		if (commandLabel.equals("tt")) {
			testAPI tt = new testAPI();
			tt.test((Player)sender);
			return false;
		}
		
		this.handleCommand(sender, parameters);
		return true;
	}
	
	public void handleCommand(CommandSender sender, List<String> parameters) {
		if (parameters.size() == 0) {
			this.commands.get(0).execute(sender, parameters);
			return;
		}
		
		String commandName = parameters.get(0).toLowerCase();
		
		for (PBaseCommand fcommand : this.commands) {
			if (fcommand.getAliases().contains(commandName)) {
				fcommand.execute(sender, parameters);
				return;
			}
		}
		
		sender.sendMessage(ChatColor.YELLOW + "Unknown UltimateArena command \""+commandName+"\". Try /ua help");
	}
	
	public List<PBaseCommand> getCommands() {
		return commands;
	}
	
	public File getRoot() {
		return getDataFolder();
	}

	public void onQuit(Player player) {
		if (isPlayerCreatingArena(player)) {
			makingArena.remove(getArenaCreator(player));
		}
		
		if (this.isInArena(player)) {
			Arena ar = this.getArena(player);
			ArenaPlayer ap = this.getArenaPlayer(player);
			if (ap != null) {
				System.out.println("[UltimateArena] Player " + player.getName() + " leaving arena " + ar + " from quit");
				if (ar != null) {
					if (ar.starttimer > 0 && (!ap.out)) { //in the lobby
						loggedOut.add(player.getName());
						this.removeFromArena(player.getName());
					}
					/*if (ar.starttimer <= 0 && (!ap.out)) {
						loggedOutInArena.add(player.getName());
					}*/
				}
			}
		}
	}
	
	public void onJoin(Player player) 
	{
		if (!this.isInArena(player))
		{
			if (this.isInArena(player.getLocation())) 
			{
				normalize(player);
				removePotions(player);
			}
		}
		
		/*boolean found = false;
		for (int i = 0; i < loggedOutInArena.size(); i++) {
			if (loggedOutInArena.get(i).equals(player.getName())) {
				found = true;
			}
		}
		if (found) {
			Arena a = this.getArena(player);
			if (a == null) {
				normalize(player);
			}
		}*/
		
		boolean found = false;
		for (int i = 0; i < loggedOut.size(); i++) {
			if (loggedOut.get(i).equals(player.getName())) {
				found = true;
			}
		}
		
		if (found) {
			Arena ar = this.getArena(player);
			if (ar == null) {
				loggedOut.remove(player.getName());
				this.normalize(player);
			}else{
				if (ar.starttimer > 0) {
					loggedOut.remove(player.getName());
					ArenaPlayer ap = this.getArenaPlayer(player);
					if (ap != null) {
						ar.endPlayer(ap, true);
					}else{
						this.normalize(player);
					}
				}
			}
		}
	}
	
	public void leaveArena(Player player) {
		if (this.isInArena(player)) {
			Arena a = this.getArena(player);
			a.endPlayer(getArenaPlayer(player), false);
		}else{
			player.sendMessage(ChatColor.RED + "Error, you are not in an arena");
		}
	}
	
	public void loadArenas() {
		String path = getRoot().getAbsolutePath() + "/arenas";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null) {
		    for (int i=0; i<children.length; i++) {
		        String filename = children[i];//.substring(0, children[i].length()-4);
		        //sendMessage(ChatColor.YELLOW +filename);
		        ArenaZone az = new ArenaZone(this, new File(path + "/" + filename));
		        if (az.loaded) {
		        	this.loadedArena.add(az);
		        }
		    }
		}
	}
	
	public void loadConfigs() {
		for (int i = 0; i < fieldTypes.size(); i++) {
			loadConfig(fieldTypes.get(i));
		}
		
		loadWhiteListedCommands();
	}
	
	public void loadWhiteListedCommands() {
		String path = getRoot().getAbsolutePath() + "/whiteListedCommands.txt";
		File f = new File(path);
		if (f.exists()) {
		    try{
				FileInputStream fstream = new FileInputStream(f.getAbsolutePath());
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				while ((strLine = br.readLine()) != null) {
					wcmd.addCommand(strLine);
				}
				br.close();
				in.close();
				fstream.close();
	        }catch (Exception e){
	            System.err.println("Error: " + e.getMessage());
	        }
		}else{
			System.out.println("[UltimateArena] No Whitelisted Commands file!");
		}
	}
	
	public void loadConfig(String str) {
		String path = getRoot().getAbsolutePath() + "/" + str + "CONFIG.txt";
		File f = new File(path);
		if (f.exists()) {
			ArenaConfig a = new ArenaConfig(this, str, f);
			configs.add(a);
			System.out.println("[UltimateArena] Loaded configuration for arena type: " + str);
		}else{
			System.out.println("[UltimateArena] Failed to load configuration for arena type: " + str);
		}
	}
	
	public void loadClasses() {
		String path = getRoot().getAbsolutePath() + "/classes";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null) {
		    for (int i=0; i<children.length; i++) {
		        String filename = children[i];
		        ArenaClass ac = new ArenaClass(this, new File(path + "/" + filename));
		        ac.name = filename;
		        this.classes.add(ac);
		    }
		}
	}
	
	public ArenaConfig getConfig(String type) {
		for (int i = 0; i < configs.size(); i++) {
			ArenaConfig a = configs.get(i);
			if (a.arenaName.equalsIgnoreCase(type)) {
				return a;
			}
		}
		return null;
	}
	
	public void forceStop() {
		for (int i = 0; i < activeArena.size(); i++) {
			try{
				activeArena.get(i).startingAmount = 0;
				activeArena.get(i).stop();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		activeArena.clear();
	}
	
	public void forceStop(String str) {
		Arena a = null;
		for (int i = 0; i < activeArena.size(); i++) {
			if (activeArena.get(i).name.equals(str)) {
				a = activeArena.get(i);
			}
		}
		
		if (a != null) {
			a.forceStop = true;
			a.stop();
		}
	}
	
	public ArenaClass getArenaClass(String line) {
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).name.toLowerCase().equals(line.toLowerCase())) {
				return classes.get(i);
			}
		}
		return null;
	}
	
	public void deleteArena(Player player, String str) {
		try{
			String path = getRoot().getAbsolutePath() + "/arenas";
			File f = new File(path + "/" + str);
			if (f.exists()) {
				f.delete();
			}
			forceStop(str);
			this.loadedArena.remove(this.getArenaZone(str));
			player.sendMessage(ChatColor.YELLOW + "Deleted arena!");
		}catch(Exception e) {
			player.sendMessage(ChatColor.RED + "Failed to delete arena!");
		}
	}
	
	public boolean isInArena(Block block) {
		return isInArena(block.getLocation());
	}
	
	public Arena getArenaInside(Block block) {
		for (int i = 0; i < loadedArena.size(); i++) {
			ArenaZone az = loadedArena.get(i);
			if (az.checkLocation(block.getLocation()))
				return getArena(az.arenaName);
		}
		
		return null;
	}
	
	public boolean isInArena(Location loc) {
		for (int i = 0; i < loadedArena.size(); i++) {
			ArenaZone az = loadedArena.get(i);
			boolean is = az.checkLocation(loc);
			if (is) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isInArena(Player player) {
		if (getArenaPlayer(player) != null) {
			return true;
		}
		return false;
	}
	
	public void removeFromArena(Player player) {
		if (player != null) {
			for (int i = 0; i < activeArena.size(); i++) {
				Arena ac = activeArena.get(i);
				ac.startingAmount--;
				ArenaPlayer ap = ac.getArenaPlayer(player);
				if (ap != null) {
					ac.arenaplayers.remove(ap);
				}
			}
		}
	}
	
	public void removeFromArena(String str) {
		for (int i = 0; i < activeArena.size(); i++) {
			Arena ac = activeArena.get(i);
			for (int ii = ac.arenaplayers.size()-1; ii >= 0; ii--) {
				ArenaPlayer ap = ac.arenaplayers.get(ii);
				if (ap != null) {
					if (ap.player.getName().equals(str)) {
						ac.arenaplayers.remove(ii);
					}
				}
			}
		}
	}

	public ArenaPlayer getArenaPlayer(Player player) {
		try{
			for (int i = 0; i < activeArena.size(); i++) {
				Arena ac = activeArena.get(i);
				ArenaPlayer ap = ac.getArenaPlayer(player);
				if (ap != null) {
					if (!ap.out) {
						if (ap.player.getName().equals(player.getName())) {
							return ap;
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void fight(Player player, String name) {
		if (player != null) {
			if (player.isOnline()) {
				if (!(isPlayerCreatingArena(player))) {
					if (InventoryHelper.isEmpty(player.getInventory())) {
						ArenaZone a = getArenaZone(name);
						if (a != null) {
							if (!isInArena(player)) {
								ArenaPlayer apl = this.getArenaPlayer(player);
								if (apl == null) {
									/////////////////////////////////////////
									/////////////////////////////////////////
										boolean found = false;
										for (int i = 0; i < waiting.size(); i++) {
											if (waiting.get(i).player.getName().equals(player.getName())) {
												found = true;
											}
										}
										if (!found) {
							                RemindTask rmd = new RemindTask(player, name);
							                rmd.runTaskLater(this, 40L);
							                player.sendMessage(ChatColor.GOLD + "Please stand still for 2 seconds!");
							                this.waiting.add(rmd);
											
										}else{
											player.sendMessage(ChatColor.RED + "You are already waiting!");
										}
										//joinBattle(null, player, name);
									/////////////////////////////////////////
									/////////////////////////////////////////
								}else{
									player.sendMessage(ChatColor.RED + "You cannot leave and rejoin an arena!");
								}
							}else{
								player.sendMessage(ChatColor.RED + "You're already in an arena!");
							}
						}else{
							player.sendMessage(ChatColor.RED + "That arena doesn't exist!");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Please clear your inventory!");
					}
				}
			}else{
				player.sendMessage(ChatColor.RED + "You are in the middle of making an arena!");
			}
		}
	}
	
	public void joinBattle(boolean forced, Player player, String name) {
		try{
			ArenaZone a = getArenaZone(name);
			if (getArena(name) != null) {
				if (getArena(name).starttimer < 1 && !forced) {
					//this.waiting.remove(rmd);
					player.sendMessage(ChatColor.RED + "This arena has already started!");
				}else{
					Arena tojoin = getArena(name);
					int maxplayers = tojoin.az.maxPlayers;
					int players = tojoin.amtPlayersInArena;
					if (players + 1 <= maxplayers) {
						getArena(name).addPlayer(player);
					}else{
						player.sendMessage(ChatColor.RED + "This arena is full, sorry!");
					}
				}
			}else{
				Arena ar = null;
				boolean disabled = false;
				for (int ii = 0; ii < activeArena.size(); ii++) {
					Arena aar = activeArena.get(ii);
					if (aar.disabled && aar.az.equals(a)) {
						disabled = true;
					}
				}
				for (int ii = 0; ii < loadedArena.size(); ii++) {
					ArenaZone aaz = loadedArena.get(ii);
					if (aaz.disabled && aaz.equals(a)) {
						disabled = true;
					}
				}
				
				if (!disabled) {
					//if (activeArena.size() + 1 <= maxArenasRunning) {
						if (a.arenaType.equalsIgnoreCase("pvp")) {
							ar = new PVPArena(a);
						}else if (a.arenaType.equalsIgnoreCase("mob")) {
							ar = new MOBArena(a);
						}else if (a.arenaType.equalsIgnoreCase("cq")) {
							ar = new CONQUESTArena(a);
						}else if (a.arenaType.equalsIgnoreCase("koth")) {
							ar = new KOTHArena(a);
						}else if (a.arenaType.equalsIgnoreCase("bomb")) {
							ar = new BOMBArena(a);
						}else if (a.arenaType.equalsIgnoreCase("ffa")) {
							ar = new FFAArena(a);
						}else if (a.arenaType.equalsIgnoreCase("hunger")) {
							ar = new HUNGERArena(a);
						}else if (a.arenaType.equalsIgnoreCase("spleef")) {
							ar = new SPLEEFArena(a);
						}else if (a.arenaType.equalsIgnoreCase("infect")) {
							ar = new INFECTArena(a);
						}else if (a.arenaType.equalsIgnoreCase("ctf")) {
							ar = new CTFArena(a);
						}
						if (ar != null) {
							activeArena.add(ar);
							ar.addPlayer(player);
							ar.announce();
						}
					//}else{
					//	player.sendMessage(ChatColor.RED + "sorry, only " + Integer.toString(maxArenasRunning) + " arena(s) can be ran at a time!");
					//}
				}else{
					player.sendMessage(ChatColor.RED + "Error, This arena is disabled!");
				}
			}
		}catch(Exception e) {
			//
		}
	}
	
	public Arena getArena(Player player) {
		for (int i = 0; i < activeArena.size(); i++) {
			Arena ac = activeArena.get(i);
			try{
				if (ac.getArenaPlayer(player).player.getName().equals(player.getName())) {
					return ac;
				}
			}catch(Exception e) {
				
			}
		}
		return null;
	}
	
	public Arena getArena(String name) {
		for (int i = 0; i < activeArena.size(); i++) {
			Arena ac = activeArena.get(i);
			if (ac.name.equals(name)) {
				return ac;
			}
		}
		return null;
	}
	
	public ArenaZone getArenaZone(String name) {
		for (int i = 0; i < loadedArena.size(); i++) {
			ArenaZone a = loadedArena.get(i);
			if (a.arenaName.equals(name)) {
				return a;
			}
		}
		return null;
	}
	
	public void setPoint(Player player) {
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null) {
			ac.setPoint(player);
			if (!(ac.msg.equals(""))) {
				player.sendMessage(ChatColor.GRAY + ac.msg);
			}
		}else{
			player.sendMessage(ChatColor.RED + "Error, you aren't editing a field!");
		}
	}
	
	public void setDone(Player player) {
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null) {
			ac.setDone(player);
		}else{
			player.sendMessage(ChatColor.RED + "Error, you aren't editing a field!");
		}
	}
	
	public boolean isPlayerCreatingArena(Player player) {
		if (getArenaCreator(player) != null) {
			return true;
		}
		return false;
	}
	
	public void stopCreatingArena(Player player) { 
		for (int i = makingArena.size()-1; i >= 0; i--) {
			ArenaCreator ac = makingArena.get(i);
			if (ac.player.equals(player.getName())) {
				makingArena.remove(i);
			}
		}
	}

	public ArenaCreator getArenaCreator(Player player) {
		for (int i = 0; i < makingArena.size(); i++) {
			ArenaCreator ac = makingArena.get(i);
			if (ac.player.equals(player.getName())) {
				return ac;
			}
		}
		return null;
	}
	
	public void createField(Player player, String name, String type) {
		if (!(isPlayerCreatingArena(player))) {
			boolean found = false;
			for (int i = 0; i < fieldTypes.size(); i++) {
				if (fieldTypes.get(i).equalsIgnoreCase(type)) {
					found = true;
				}
			}
			if (found) {
				System.out.println("[UltimateArena] " + player.getName() + "is making arena: " + name + " with a type: " + type);
				ArenaCreator ac = new ArenaCreator(this, player);
				ac.setArena(name, type);
				makingArena.add(ac);
			}else{
				player.sendMessage(ChatColor.RED + "This is not a valid field type!");
			}
		}else{
			player.sendMessage(ChatColor.RED + "You are already creating an arena!");
		}
	}
	
	public void normalizeAll() {
		ArrayList<Player> arr = (ArrayList<Player>) Util.Who();
		for (int i = 0; i < arr.size(); i++) {
			Player p = arr.get(i);
			Location ploc = p.getLocation();
			if (isInArena(ploc)) {
				normalize(p);
				if (this.isInArena(p)) {
					this.removeFromArena(p);
				}
			}
		}
	}
	
	public void normalize(Player p) {
		try{
			p.getInventory().clear();
			p.getInventory().setHelmet(null);
			p.getInventory().setChestplate(null);
			p.getInventory().setLeggings(null);
			p.getInventory().setBoots(null);
		}catch(Exception e) {
			//
		}
	}

	@Override
	public void onEnable() {
		System.out.println("[UltimateArena] " + getDescription().getFullName() + " has been enabled");
		
		File dir = getDataFolder();
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		File dir2 = new File(getDataFolder().getAbsolutePath() + "/arenas");
		if (!dir2.exists()) {
			dir2.mkdir();
		}
		
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
		
		isUAAdmin.add("orange451");
		isUAAdmin.add("dmulloy2");
		
		PermissionInterface.Initialize(this);
		
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
		
		if (!loaded) {
			loaded = true;
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(entityListener, this);
			pm.registerEvents(blockListener, this);
			pm.registerEvents(playerListener, this);

			Util.Initialize(this);
			
//		    this.timer = new Timer();
//		    this.timer.schedule(new ArenaUpdater(), 100L, 1000L);
			new ArenaUpdater().runTaskTimer(this, 2L, 20L);
		}
		loadFiles();
	}

	@Override
	public void onDisable() {
		System.out.println("[UltimateArena] " + getDescription().getFullName() + " has been disabled");
		isUAAdmin.clear();
		for (int i = activeArena.size()-1; i >= 0; i--) {
			try{
				activeArena.get(i).stop();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		getServer().getScheduler().cancelTasks(this);
//		timer.cancel();
		clearMemory();
	}

	public void loadFiles() {
		loadClasses();
		loadConfigs();
		loadArenas();
	}
	
	public void clearMemory() {
		commands.clear();
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		fieldTypes.clear();
		classes.clear();
		loggedOut.clear();
		loggedOutInArena.clear();
		isUAAdmin.clear();
		configs.clear();
		wcmd.clear();
	}
	
	public class ArenaUpdater extends BukkitRunnable {
		public void run() {
		    for (int i = 0; i < activeArena.size(); i++) {
		    	try{
		    		activeArena.get(i).step();
		    	}catch(Exception e) {
		    		//fuck you
		    	}
		    }
		}
	}
	
	public class RemindTask extends BukkitRunnable {
		public Player player;
		public String name;
		public int t;
		
		public RemindTask(Player player, String name) {
			this.player = player;
			this.name = name;
		}

		public void run() {
			waiting.remove(this);
			joinBattle(false, player, name);
			this.cancel();
		}
	}

	public boolean isUaAdmin(Player player) {
		for (int i = 0; i < this.isUAAdmin.size(); i++) {
			if (isUAAdmin.get(i).equals(player.getName())) {
				return true;
			}
		}
		return false;
	}

	public void removePotions(Player pl) {
		pl.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		pl.removePotionEffect(PotionEffectType.SPEED);
		pl.removePotionEffect(PotionEffectType.SLOW);
		pl.removePotionEffect(PotionEffectType.JUMP);
		pl.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		pl.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
	}
	
}