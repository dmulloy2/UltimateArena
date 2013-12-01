package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.util.MaterialUtil;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author dmulloy2
 */

public class FileHandler
{
	private final UltimateArena plugin;
	public FileHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * Saves an ArenaZone
	 * 
	 * @param az - {@link ArenaZone} to save
	 */
	public void save(ArenaZone az)
	{
		try
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.getArenaName() + ".dat");
			if (file.exists())
				file.delete();

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

				fc.set("specialType", az.getSpecialType().toString());

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

			fc.set("liked", az.getLiked());
			fc.set("played", az.getTimesPlayed());

			fc.set("maxPlayers", az.getMaxPlayers());
			fc.set("defaultClass", az.getDefaultClass());

			fc.save(file);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "saving Arena: " + az.getArenaName()));
		}
	}

	/**
	 * Loads an ArenaZone
	 * 
	 * @param az - {@link ArenaZone} to load
	 */
	public void load(ArenaZone az)
	{
		plugin.debug("Loading Arena: {0}", az.getArenaName());

		try
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.getArenaName() + ".dat");
	
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
	
			String arenaType = fc.getString("type");
			if (! FieldType.contains(arenaType))
			{
				plugin.outConsole(Level.SEVERE, "Could not load Arena {0}: {1} is not a valid FieldType!", az.getArenaName(), arenaType);
				az.setLoaded(false);
				return;
			}
	
			az.setType(FieldType.getByName(arenaType));
	
			World world = plugin.getServer().getWorld(fc.getString("world"));
			if (world == null)
			{
				plugin.outConsole(Level.SEVERE, "Could not load Arena {0}: World cannot be null!", az.getArenaName());
				az.setLoaded(false);
				return;
			}
	
			az.setWorld(world);
	
			Location lobby1 = new Location(world, fc.getInt("lobby1.x"), 0, fc.getInt("lobby1.z"));
			Location lobby2 = new Location(world, fc.getInt("lobby2.x"), 0, fc.getInt("lobby2.z"));
			if (lobby1 == null || lobby2 == null)
			{
				plugin.outConsole(Level.SEVERE, "Could not load Arena {0}: Lobby locations cannot be null!", az.getArenaName());
				az.setLoaded(false);
				return;
			}
	
			az.setLobby1(lobby1);
			az.setLobby2(lobby2);
	
			Location arena1 = new Location(world, fc.getInt("arena1.x"), 0, fc.getInt("arena1.z"));
			Location arena2 = new Location(world, fc.getInt("arena2.x"), 0, fc.getInt("arena2.z"));
			if (arena1 == null || arena2 == null)
			{
				plugin.outConsole(Level.SEVERE, "Could not load Arena {0}: Arena locations cannot be null!", az.getArenaName());
				az.setLoaded(false);
				return;
			}
	
			az.setArena1(arena1);
			az.setArena2(arena2);

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

					Location loc = new Location(world, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

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

					Location loc = new Location(world, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

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

					Location loc = new Location(world, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

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

					Location loc = new Location(world, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getSpawns().add(loc);
				}

			}
			if (arenaType.equals("spleef"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setSpecialType(MaterialUtil.getMaterial(fc.getString("specialType")));

				for (int i = 0; i < 4; i++)
				{
					String path = "flags." + i + ".";

					Location loc = new Location(world, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getFlags().add(loc);
				}
			}
			if (arenaType.equals("bomb"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));

				az.getFlags().add(new Location(world, fc.getInt("flag0.x"), fc.getInt("flag0.y"), fc.getInt("flag0.z")));
				az.getFlags().add(new Location(world, fc.getInt("flag1.x"), fc.getInt("flag1.y"), fc.getInt("flag1.z")));
			}
			if (arenaType.equals("ctf"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));

				az.getFlags().add(new Location(world, fc.getInt("flag0.x"), fc.getInt("flag0.y"), fc.getInt("flag0.z")));
				az.getFlags().add(new Location(world, fc.getInt("flag1.x"), fc.getInt("flag1.y"), fc.getInt("flag1.z")));
			}
			if (arenaType.equals("infect"))
			{
				az.setLobbyREDspawn(new Location(world, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new Location(world, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new Location(world, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new Location(world, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
			}

			if (fc.isSet("liked"))
			{
				az.setLiked(fc.getInt("liked"));
				az.setTimesPlayed(fc.getInt("played"));
			}

			az.setMaxPlayers(fc.getInt("maxPlayers"));
			az.setDefaultClass(fc.getString("defaultClass"));

			az.setLoaded(true);
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, Util.getUsefulStack(e, "loading Arena: " + az.getArenaName()));
			az.setLoaded(false);
		}
	}
}