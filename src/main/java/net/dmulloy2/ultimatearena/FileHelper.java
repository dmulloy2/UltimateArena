package net.dmulloy2.ultimatearena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaSign;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author dmulloy2
 */

public class FileHelper 
{
	private final UltimateArena plugin;
	public FileHelper(final UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	/**Generate Whitelisted Commands File**/
	public void generateWhitelistedCmds()
	{
		File file = new File(plugin.getDataFolder(), "whiteListedCommands.yml");
		if (file.exists())
			return;
		
		try
		{
			file.createNewFile();
		
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		
			List<String> words = new ArrayList<String>();
			words.add("/f c");
			words.add("/msg");
			words.add("/r");
			words.add("/who");
			words.add("/gms");
			words.add("/god");
			words.add("/list");
			words.add("/t");
			words.add("/msg");
			words.add("/tell");
		
			fc.set("whiteListedCmds", words);
		
			fc.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not generate white listed commands: {0}", e.getMessage());
		}
	}
	
	/**Generate Arena Configurations**/
	public void generateArenaConfig(String field)
	{
		File folder = new File(plugin.getDataFolder(), "configs");
		File file = new File(folder, field + "Config.yml");
		if (file.exists())
			return;
		
		try
		{
			file.createNewFile();
			
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			if (field.equals("bomb"))
			{
				fc.set("gameTime", 900);
				fc.set("lobbyTime", 70);
				fc.set("maxDeaths", 990);
				fc.set("allowTeamKilling", false);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,1");
				rewards.add("46,4");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("cq"))
			{
				fc.set("gameTime", 1200);
				fc.set("lobbyTime", 180);
				fc.set("maxDeaths", 900);
				fc.set("allowTeamKilling", false);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,3");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("ctf"))
			{
				fc.set("gameTime", 440);
				fc.set("lobbyTime", 90);
				fc.set("maxDeaths", 999);
				fc.set("allowTeamKilling", false);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,4");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("ffa"))
			{
				fc.set("gameTime", 600);
				fc.set("lobbyTime", 70);
				fc.set("maxDeaths", 3);
				fc.set("allowTeamKilling", true);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,9");
				rewards.add("46,3");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("hunger"))
			{
				fc.set("gameTime", 9000);
				fc.set("lobbyTime", 70);
				fc.set("maxDeaths", 1);
				fc.set("allowTeamKilling", true);
				fc.set("cashReward", 1000);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,3");
				rewards.add("46,3");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("infect"))
			{
				fc.set("gameTime", 180);
				fc.set("lobbyTime", 90);
				fc.set("maxDeaths", 2);
				fc.set("allowTeamKilling", false);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,6");
				rewards.add("46,2");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("koth"))
			{
				fc.set("gameTime", 1200);
				fc.set("lobbyTime", 80);
				fc.set("maxDeaths", 900);
				fc.set("allowTeamKilling", true);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,3");
				rewards.add("46,3");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("mob"))
			{
				fc.set("gameTime", 1200);
				fc.set("lobbyTime", 90);
				fc.set("maxDeaths", 0);
				fc.set("maxWave", 15);
				fc.set("allowTeamKilling", false);
				fc.set("cashReward", 15);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,3");
				rewards.add("46,2");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("pvp"))
			{
				fc.set("gameTime", 600);
				fc.set("lobbyTime", 90);
				fc.set("maxDeaths", 3);
				fc.set("allowTeamKilling", false);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,3");
				rewards.add("46,2");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
			
			if (field.equals("spleef"))
			{
				fc.set("gameTime", 600);
				fc.set("lobbyTime", 80);
				fc.set("maxDeaths", 2);
				fc.set("allowTeamKilling", true);
				fc.set("cashReward", 100);
				
				List<String> rewards = new ArrayList<String>();
				rewards.add("266,3");
				rewards.add("46,2");
				
				fc.set("rewards", rewards);
				
				fc.save(file);
			}
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not generate configuration for \"{0}\": {1}", field, e.getMessage());
		}
	}

	/**Generate Stock Classes**/
	public void generateStockClasses() 
	{
		try
		{
			File dir = new File(plugin.getDataFolder(), "classes");
			File archerFile = new File(dir, "archer.yml");
			if (! archerFile.exists())
			{ 
				archerFile.createNewFile(); 
			}

			generateClass(archerFile, "archer");
			
			File bruteFile = new File(dir, "brute.yml");
			if (! bruteFile.exists())
			{ 
				bruteFile.createNewFile(); 
			}

			generateClass(bruteFile, "brute");
			
			File dumbassFile = new File(dir, "dumbass.yml");
			if (! dumbassFile.exists())
			{ 
				dumbassFile.createNewFile(); 
			}

			generateClass(dumbassFile, "dumbass");
			
			File gunnerFile = new File(dir, "gunner.yml");
			if (! gunnerFile.exists())
			{ 
				gunnerFile.createNewFile(); 
			}

			generateClass(gunnerFile, "gunner");
			
			File healerFile = new File(dir, "healer.yml");
			if (! healerFile.exists())
			{ 
				healerFile.createNewFile(); 
			}

			generateClass(healerFile, "healer");
			
			File shotgunFile = new File(dir, "shotgun.yml");
			if (! shotgunFile.exists())
			{ 
				shotgunFile.createNewFile(); 
			}

			generateClass(shotgunFile, "shotgun");
			
			File sniperFile = new File(dir, "sniper.yml");
			if (! sniperFile.exists())
			{ 
				sniperFile.createNewFile(); 
			}

			generateClass(sniperFile, "sniper");
			
			File spleefFile = new File(dir, "spleef.yml");
			if (! spleefFile.exists())
			{ 
				spleefFile.createNewFile(); 
			}
			
			generateClass(spleefFile, "spleef");
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not generate stock classes: {0}", e.getMessage());
		}
	}
	
	/**Generates a Class File**/
	public void generateClass(File file, String type)
	{
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		try
		{
			if (type.equals("archer"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "261,1,inf:1");
				fc.set("tools.2", "262,1");
				fc.set("tools.3", "267,1,sharp:2");
			}
			
			if (type.equals("brute"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "276,1");
				fc.set("tools.2", "333,2");
				fc.set("tools.3", "341,3");
			}
			
			if (type.equals("dumbass"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "283,1");
				fc.set("tools.2", "259,1");
			}
			
			if (type.equals("gunner"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "292,1");
				fc.set("tools.2", "318,1");
				fc.set("tools.3", "341,3");
				fc.set("tools.4", "322,2");
				fc.set("tools.5", "261,1,sharp:1");
			}
			
			if (type.equals("healer"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "286,1");
				fc.set("tools.2", "290,1");
				fc.set("tools.3", "322,1");
				fc.set("tools.4", "potion: regen, 1, 1, true");
			}
			
			if (type.equals("shotgun"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "291,1");
				fc.set("tools.2", "295,1");
				fc.set("tools.3", "341,3");
				fc.set("tools.4", "322,2");
				fc.set("tools.5", "267,1");
			}
			
			if (type.equals("sniper"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "294,1");
				fc.set("tools.2", "337,1");
				fc.set("tools.3", "341,3");
				fc.set("tools.4", "322,2");
				fc.set("tools.5", "267,1");
			}
			
			if (type.equals("spleef"))
			{
				fc.set("armor.chestplate", "307");
				fc.set("armor.leggings", "308");
				fc.set("armor.boots", "309");
				fc.set("tools.1", "277,1");
			}
			
			fc.set("useEssentials", false);
			fc.set("essentialsKit", "");
			
			fc.set("useHelmet", true);
			
			fc.set("permissionNode", "");
			
			fc.set("hasPotionEffects", false);
			fc.set("potionEffects", "");
			
			fc.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not save class file \"{0}\": {1}", type, e.getMessage());
		}
	}

	/**Save an ArenaZone**/
	public void save(ArenaZone az)
	{
		try
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.getArenaName() + ".dat");
			if (!file.exists())
				file.createNewFile();

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			
			fc.set("type", az.getType().getName());
			fc.set("world", az.getWorld().getName());
			
			Location lobby1 = az.getLobby1();
			fc.set("lobby1.x", lobby1.getBlockX());
			fc.set("lobby1.z", lobby1.getBlockZ());
			
			Location lobby2 = az.getLobby2();
			fc.set("lobby2.x", lobby2.getBlockX());
			fc.set("lobby2.z", lobby2.getBlockZ());
			
			Location arena1 = az.getArena1();
			fc.set("arena1.x", arena1.getBlockX());
			fc.set("arena1.z", arena1.getBlockZ());
			
			Location arena2 = az.getArena2();
			fc.set("arena2.x", arena2.getBlockX());
			fc.set("arena2.z", arena2.getBlockZ());
			
			String arenaType = az.getType().getName().toLowerCase();
			if (arenaType.equals("pvp"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
			}
			if (arenaType.equals("mob"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				fc.set("spawnsAmt", az.getSpawns().size());
				for (int i = 0; i < az.getSpawns().size(); i++) 
				{
					Location loc = az.getSpawns().get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
			}
			if (arenaType.equals("cq")) 
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
				
				fc.set("flagsAmt", az.getFlags().size());
				for (int i = 0; i < az.getFlags().size(); i++) 
				{
					Location loc = az.getFlags().get(i);
					String path = "flags." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
			}
			if (arenaType.equals("koth"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				fc.set("spawnsAmt", az.getSpawns().size());
				for (int i = 0; i < az.getSpawns().size(); i++) 
				{
					Location loc = az.getSpawns().get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
				
				fc.set("flag.x", az.getFlags().get(0).getBlockX());
				fc.set("flag.y", az.getFlags().get(0).getBlockY());
				fc.set("flag.z", az.getFlags().get(0).getBlockZ());
			}
			if (arenaType.equals("ffa") || arenaType.equals("hunger"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				fc.set("spawnsAmt", az.getSpawns().size());
				for (int i = 0; i < az.getSpawns().size(); i++) 
				{
					Location loc = az.getSpawns().get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
				
			}
			if (arenaType.equals("spleef"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				fc.set("specialType", az.getSpecialType());
				
				for (int i = 0; i < 4; i++) 
				{
					Location loc = az.getFlags().get(i);
					String path = "flags." + i + ".";

					fc.set(path + "x", loc.getBlockX());
					fc.set(path + "y", loc.getBlockY());
					fc.set(path + "z", loc.getBlockZ());
				}
			}
			if (arenaType.equals("bomb"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
				
				fc.set("flag0.x", az.getFlags().get(0).getBlockX());
				fc.set("flag0.y", az.getFlags().get(0).getBlockY());
				fc.set("flag0.z", az.getFlags().get(0).getBlockZ());
				
				fc.set("flag1.x", az.getFlags().get(1).getBlockX());
				fc.set("flag1.y", az.getFlags().get(1).getBlockY());
				fc.set("flag1.z", az.getFlags().get(1).getBlockZ());
			}
			if (arenaType.equals("ctf")) 
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
				
				fc.set("flag0.x", az.getFlags().get(0).getBlockX());
				fc.set("flag0.y", az.getFlags().get(0).getBlockY());
				fc.set("flag0.z", az.getFlags().get(0).getBlockZ());
				
				fc.set("flag1.x", az.getFlags().get(1).getBlockX());
				fc.set("flag1.y", az.getFlags().get(1).getBlockY());
				fc.set("flag1.z", az.getFlags().get(1).getBlockZ());
			}
			if (arenaType.equals("infect"))
			{
				Location lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getBlockX());
				fc.set("lobbyRed.y", lobbyRed.getBlockY());
				fc.set("lobbyRed.z", lobbyRed.getBlockZ());
				
				Location lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getBlockX());
				fc.set("lobbyBlue.y", lobbyBlue.getBlockY());
				fc.set("lobbyBlue.z", lobbyBlue.getBlockZ());
				
				Location team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getBlockX());
				fc.set("team1.y", team1.getBlockY());
				fc.set("team1.z", team1.getBlockZ());
				
				Location team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getBlockX());
				fc.set("team2.y", team2.getBlockY());
				fc.set("team2.z", team2.getBlockZ());
			}
			
			fc.set("maxPlayers", az.getMaxPlayers());
			fc.set("defaultClass", az.getDefaultClass());
			
			fc.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not save arena \"{0}\": {1}", az.getArenaName(), e.getMessage());
		}
	}
	
	/**Load an ArenaZone**/
	public void load(ArenaZone az)
	{
		try
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.getArenaName() + ".dat");
	
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			
			String arenaType = fc.getString("type");
			az.setArenaType(FieldType.getByName(fc.getString("type")));
			
			World world = plugin.getServer().getWorld(fc.getString("world"));
			az.setWorld(world);
			
			az.setLobby1(new Location(world, fc.getInt("lobby1.x"), 0, fc.getInt("lobby1.z")));
			az.setLobby2(new Location(world, fc.getInt("lobby2.x"), 0, fc.getInt("lobby2.z")));
			
			az.setArena1(new Location(world, fc.getInt("arena1.x"), 0, fc.getInt("arena1.z")));
			az.setArena2(new Location(world, fc.getInt("arena2.x"), 0, fc.getInt("arena2.z")));
	
			if (arenaType.equals("pvp"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));
				
				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));
				
				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
			}
			if (arenaType.equals("mob"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));
				
				int spawnsAmt = fc.getInt("spawnsAmt");
				for (int i = 0; i < spawnsAmt; i++) 
				{
					String path = "spawns." + i + ".";
	
					Location loc = new Location(world, fc.getInt(path + "x"),fc.getInt(path + "y"), fc.getInt(path + "z"));
					
					az.getSpawns().add(loc);
				}
			}
			if (arenaType.equals("cq")) 
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));
				
				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));
				
				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
				
				int flagsAmt = fc.getInt("flagsAmt");
				for (int i = 0; i < flagsAmt; i++) 
				{
					String path = "flags." + i + ".";
	
					Location loc = new Location(world, fc.getInt(path + "x"),fc.getInt(path + "y"), fc.getInt(path + "z"));
					
					az.getFlags().add(loc);
				}
			}
			if (arenaType.equals("koth"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				int spawnsAmt = fc.getInt("spawnsAmt");
				for (int i = 0; i < spawnsAmt; i++) 
				{
					String path = "spawns." + i + ".";
	
					Location loc = new Location(world, fc.getInt(path + "x"),fc.getInt(path + "y"), fc.getInt(path + "z"));
					
					az.getSpawns().add(loc);
				}
				
				Location loc = new Location(world, fc.getInt("flag.x"), fc.getInt("flag.y"), fc.getInt("flag.z"));
				az.getFlags().add(loc);
			}
			if (arenaType.equals("ffa") || arenaType.equals("hunger"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				int spawnsAmt = fc.getInt("spawnsAmt");
				for (int i = 0; i < spawnsAmt; i++) 
				{
					String path = "spawns." + i + ".";
	
					Location loc = new Location(world, fc.getInt(path + "x"),fc.getInt(path + "y"), fc.getInt(path + "z"));
					
					az.getSpawns().add(loc);
				}
				
			}
			if (arenaType.equals("spleef"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setSpecialType(fc.getInt("specialType"));
				
				for (int i = 0; i < 4; i++) 
				{
					String path = "flags." + i + ".";
	
					Location loc = new Location(world, fc.getInt(path + "x"),fc.getInt(path + "y"), fc.getInt(path + "z"));
					
					az.getFlags().add(loc);
				}
			}
			if (arenaType.equals("bomb"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));
				
				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));
				
				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
				
				az.getFlags().add(new Location(world, fc.getInt("flag0.x"),fc.getInt("flag0.y"), fc.getInt("flag0.z")));
				az.getFlags().add(new Location(world, fc.getInt("flag1.x"),fc.getInt("flag1.y"), fc.getInt("flag1.z")));
			}
			if (arenaType.equals("ctf")) 
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));
				
				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));
				
				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
				
				az.getFlags().add(new Location(world, fc.getInt("flag0.x"),fc.getInt("flag0.y"), fc.getInt("flag0.z")));
				az.getFlags().add(new Location(world, fc.getInt("flag1.x"),fc.getInt("flag1.y"), fc.getInt("flag1.z")));
			}
			if (arenaType.equals("infect"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));
				
				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));
				
				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));
				
				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
			}
			
			az.setMaxPlayers(fc.getInt("maxPlayers"));
			az.setDefaultClass(fc.getString("defaultClass"));
			
			az.setLoaded(true);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not load arena \"{0}\": {1}", az.getArenaName(), e.getMessage());
			az.setLoaded(false);
		}
	}

	public void saveSign(ArenaSign sign)
	{
		try
		{
			File signFile = new File(plugin.getDataFolder(), "signs.yml");
			if (! signFile.exists())
			{
				plugin.debug("Creating a new sign save!");
				signFile.createNewFile();
			}
			
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(signFile);
			
			String path = "signs." + sign.getId() + ".";
			String locpath = path + "location.";
			Location loc = sign.getLocation();
			
			fc.set(locpath + "world", loc.getWorld().getName());
			fc.set(locpath + "x", loc.getBlockX());
			fc.set(locpath + "y", loc.getBlockY());
			fc.set(locpath + "z", loc.getBlockZ());
			
			if (fc.isSet(path + "autoAssign"))
				fc.set(path + "autoAssign", null);
			
			fc.set(path + "isJoin", sign.isJoinSign());
			fc.set(path + "isStatus", sign.isStatusSign());
			fc.set(path + "name", sign.getArena());
			
			fc.set("total", plugin.arenaSigns.size());
			
			fc.save(signFile);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Could not save sign {0}: {1}", sign.getId(), e.getMessage());
		}
	}
	
	public void loadSigns()
	{
		plugin.debug("Loading join and status signs!");

		File signFile = new File(plugin.getDataFolder(), "signs.yml");
		if (! signFile.exists())
		{
			plugin.debug("Could not load signs save: No signs exist!");
			return;
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(signFile);
		for (int i = 0; i < fc.getInt("total"); i++)
		{
			String path = "signs." + i + ".";
			String locpath = path + "location.";
			
			World world = plugin.getServer().getWorld(fc.getString(locpath + "world"));
			if (world == null)
			{
				plugin.outConsole(Level.WARNING, "Could not load sign {0}: World cannot be null", i);
				continue;
			}
			
			Location loc = new Location(world, fc.getInt(locpath + "x"), fc.getInt(locpath + "y"), fc.getInt(locpath + "z"));
			
			ArenaZone az = plugin.getArenaZone(fc.getString(path + "name"));
			if (az == null)
			{
				plugin.outConsole(Level.WARNING, "Could not load sign {0}: ArenaZone cannot be null", i);
				continue;
			}

			boolean isJoin = fc.getBoolean(path + "isJoin");
			ArenaSign sign = new ArenaSign(plugin, loc, az, isJoin, i);
			plugin.arenaSigns.add(sign);
		}
	}
	
	public void refreshSignSave()
	{
		plugin.debug("Refreshing sign save!");
		
		File signFile = new File(plugin.getDataFolder(), "signs.yml");
		if (signFile.exists())
		{
			signFile.delete();
		}
		
		try
		{
			signFile.createNewFile(); 
		}
		catch (Exception e) 
		{
			plugin.outConsole(Level.SEVERE, "Could not refresh sign save: {0}", e.getMessage());
		}
		
		for (ArenaSign sign : plugin.arenaSigns)
		{
			saveSign(sign);
		}
	}
}