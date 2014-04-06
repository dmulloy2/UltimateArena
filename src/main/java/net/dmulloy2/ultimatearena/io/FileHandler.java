package net.dmulloy2.ultimatearena.io;

import java.io.File;
import java.util.logging.Level;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.FieldType;
import net.dmulloy2.ultimatearena.types.Material;
import net.dmulloy2.ultimatearena.util.Util;

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
	 * @param az
	 *        - {@link ArenaZone} to save
	 *
	 * @deprecated Serialization
	 */
	@Deprecated
	public final void save(ArenaZone az)
	{
		try
		{
			File file = az.getFile();
			if (file.exists())
				file.delete();

			file.createNewFile();

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

			fc.set("type", az.getType().getName());
			fc.set("typeString", az.getType().getName());

			fc.set("world", az.getWorldName());

			ArenaLocation lobby1 = az.getLobby1();
			fc.set("lobby1.x", lobby1.getX());
			fc.set("lobby1.z", lobby1.getZ());

			ArenaLocation lobby2 = az.getLobby2();
			fc.set("lobby2.x", lobby2.getX());
			fc.set("lobby2.z", lobby2.getZ());

			ArenaLocation arena1 = az.getArena1();
			fc.set("arena1.x", arena1.getX());
			fc.set("arena1.z", arena1.getZ());

			ArenaLocation arena2 = az.getArena2();
			fc.set("arena2.x", arena2.getX());
			fc.set("arena2.z", arena2.getZ());

			String arenaType = az.getType().getName();
			if (arenaType.equalsIgnoreCase("pvp"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				ArenaLocation lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getX());
				fc.set("lobbyBlue.y", lobbyBlue.getY());
				fc.set("lobbyBlue.z", lobbyBlue.getZ());

				ArenaLocation team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getX());
				fc.set("team1.y", team1.getY());
				fc.set("team1.z", team1.getZ());

				ArenaLocation team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getX());
				fc.set("team2.y", team2.getY());
				fc.set("team2.z", team2.getZ());
			}
			else if (arenaType.equalsIgnoreCase("mob"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				ArenaLocation team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getX());
				fc.set("team1.y", team1.getY());
				fc.set("team1.z", team1.getZ());

				fc.set("spawnsAmt", az.getSpawns().size());
				for (int i = 0; i < az.getSpawns().size(); i++)
				{
					ArenaLocation loc = az.getSpawns().get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getX());
					fc.set(path + "y", loc.getY());
					fc.set(path + "z", loc.getZ());
				}
			}
			else if (arenaType.equalsIgnoreCase("cq"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				ArenaLocation lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getX());
				fc.set("lobbyBlue.y", lobbyBlue.getY());
				fc.set("lobbyBlue.z", lobbyBlue.getZ());

				ArenaLocation team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getX());
				fc.set("team1.y", team1.getY());
				fc.set("team1.z", team1.getZ());

				ArenaLocation team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getX());
				fc.set("team2.y", team2.getY());
				fc.set("team2.z", team2.getZ());

				fc.set("flagsAmt", az.getFlags().size());
				for (int i = 0; i < az.getFlags().size(); i++)
				{
					ArenaLocation loc = az.getFlags().get(i);
					String path = "flags." + i + ".";

					fc.set(path + "x", loc.getX());
					fc.set(path + "y", loc.getY());
					fc.set(path + "z", loc.getZ());
				}
			}
			else if (arenaType.equalsIgnoreCase("koth"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				fc.set("spawnsAmt", az.getSpawns().size());
				for (int i = 0; i < az.getSpawns().size(); i++)
				{
					ArenaLocation loc = az.getSpawns().get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getX());
					fc.set(path + "y", loc.getY());
					fc.set(path + "z", loc.getZ());
				}

				fc.set("flag.x", az.getFlags().get(0).getX());
				fc.set("flag.y", az.getFlags().get(0).getY());
				fc.set("flag.z", az.getFlags().get(0).getZ());
			}
			else if (arenaType.equalsIgnoreCase("ffa") || arenaType.equals("hunger"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				fc.set("spawnsAmt", az.getSpawns().size());
				for (int i = 0; i < az.getSpawns().size(); i++)
				{
					ArenaLocation loc = az.getSpawns().get(i);
					String path = "spawns." + i + ".";

					fc.set(path + "x", loc.getX());
					fc.set(path + "y", loc.getY());
					fc.set(path + "z", loc.getZ());
				}
			}
			else if (arenaType.equalsIgnoreCase("spleef"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				fc.set("specialType", az.getSpecialType().toString());
				fc.set("specialTypeString", az.getSpecialType().toString());

				for (int i = 0; i < 4; i++)
				{
					ArenaLocation loc = az.getFlags().get(i);
					String path = "flags." + i + ".";

					fc.set(path + "x", loc.getX());
					fc.set(path + "y", loc.getY());
					fc.set(path + "z", loc.getZ());
				}
			}
			else if (arenaType.equalsIgnoreCase("bomb"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				ArenaLocation lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getX());
				fc.set("lobbyBlue.y", lobbyBlue.getY());
				fc.set("lobbyBlue.z", lobbyBlue.getZ());

				ArenaLocation team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getX());
				fc.set("team1.y", team1.getY());
				fc.set("team1.z", team1.getZ());

				ArenaLocation team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getX());
				fc.set("team2.y", team2.getY());
				fc.set("team2.z", team2.getZ());

				fc.set("flag0.x", az.getFlags().get(0).getX());
				fc.set("flag0.y", az.getFlags().get(0).getY());
				fc.set("flag0.z", az.getFlags().get(0).getZ());

				fc.set("flag1.x", az.getFlags().get(1).getX());
				fc.set("flag1.y", az.getFlags().get(1).getY());
				fc.set("flag1.z", az.getFlags().get(1).getZ());
			}
			else if (arenaType.equalsIgnoreCase("ctf"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				ArenaLocation lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getX());
				fc.set("lobbyBlue.y", lobbyBlue.getY());
				fc.set("lobbyBlue.z", lobbyBlue.getZ());

				ArenaLocation team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getX());
				fc.set("team1.y", team1.getY());
				fc.set("team1.z", team1.getZ());

				ArenaLocation team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getX());
				fc.set("team2.y", team2.getY());
				fc.set("team2.z", team2.getZ());

				fc.set("flag0.x", az.getFlags().get(0).getX());
				fc.set("flag0.y", az.getFlags().get(0).getY());
				fc.set("flag0.z", az.getFlags().get(0).getZ());

				fc.set("flag1.x", az.getFlags().get(1).getX());
				fc.set("flag1.y", az.getFlags().get(1).getY());
				fc.set("flag1.z", az.getFlags().get(1).getZ());
			}
			else if (arenaType.equalsIgnoreCase("infect"))
			{
				ArenaLocation lobbyRed = az.getLobbyREDspawn();
				fc.set("lobbyRed.x", lobbyRed.getX());
				fc.set("lobbyRed.y", lobbyRed.getY());
				fc.set("lobbyRed.z", lobbyRed.getZ());

				ArenaLocation lobbyBlue = az.getLobbyBLUspawn();
				fc.set("lobbyBlue.x", lobbyBlue.getX());
				fc.set("lobbyBlue.y", lobbyBlue.getY());
				fc.set("lobbyBlue.z", lobbyBlue.getZ());

				ArenaLocation team1 = az.getTeam1spawn();
				fc.set("team1.x", team1.getX());
				fc.set("team1.y", team1.getY());
				fc.set("team1.z", team1.getZ());

				ArenaLocation team2 = az.getTeam2spawn();
				fc.set("team2.x", team2.getX());
				fc.set("team2.y", team2.getY());
				fc.set("team2.z", team2.getZ());
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
	 * @param az
	 *        - {@link ArenaZone} to load
	 *
	 * @deprecated Serialization
	 */
	@Deprecated
	public void load(ArenaZone az)
	{
		plugin.debug("Loading Arena: {0}", az.getArenaName());

		try
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.getArenaName() + ".dat");

			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);

			String arenaType = fc.getString("type");
			az.setType(FieldType.getByName(arenaType));
			az.setTypeString(arenaType);

			String worldName = fc.getString("world");
			if (worldName == null || worldName.isEmpty())
			{
				plugin.outConsole(Level.SEVERE, "Could not load Arena {0}: World cannot be null!", az.getArenaName());
				az.setLoaded(false);
				return;
			}

			az.setWorldName(worldName);

			az.setLobby1(new ArenaLocation(worldName, fc.getInt("lobby1.x"), 0, fc.getInt("lobby1.z")));
			az.setLobby2(new ArenaLocation(worldName, fc.getInt("lobby2.x"), 0, fc.getInt("lobby2.z")));

			az.setArena1(new ArenaLocation(worldName, fc.getInt("arena1.x"), 0, fc.getInt("arena1.z")));
			az.setArena2(new ArenaLocation(worldName, fc.getInt("arena2.x"), 0, fc.getInt("arena2.z")));

			if (arenaType.equalsIgnoreCase("pvp"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new ArenaLocation(worldName, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new ArenaLocation(worldName, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new ArenaLocation(worldName, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
			}
			else if (arenaType.equalsIgnoreCase("mob"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setTeam1spawn(new ArenaLocation(worldName, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				int spawnsAmt = fc.getInt("spawnsAmt");
				for (int i = 0; i < spawnsAmt; i++)
				{
					String path = "spawns." + i + ".";

					ArenaLocation loc = new ArenaLocation(worldName, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getSpawns().add(loc);
				}
			}
			else if (arenaType.equalsIgnoreCase("cq"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new ArenaLocation(worldName, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new ArenaLocation(worldName, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new ArenaLocation(worldName, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));

				int flagsAmt = fc.getInt("flagsAmt");
				for (int i = 0; i < flagsAmt; i++)
				{
					String path = "flags." + i + ".";

					ArenaLocation loc = new ArenaLocation(worldName, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getFlags().add(loc);
				}
			}
			else if (arenaType.equalsIgnoreCase("koth"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				int spawnsAmt = fc.getInt("spawnsAmt");
				for (int i = 0; i < spawnsAmt; i++)
				{
					String path = "spawns." + i + ".";

					ArenaLocation loc = new ArenaLocation(worldName, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getSpawns().add(loc);
				}

				ArenaLocation loc = new ArenaLocation(worldName, fc.getInt("flag.x"), fc.getInt("flag.y"), fc.getInt("flag.z"));
				az.getFlags().add(loc);
			}
			else if (arenaType.equalsIgnoreCase("ffa") || arenaType.equalsIgnoreCase("hunger"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				int spawnsAmt = fc.getInt("spawnsAmt");
				for (int i = 0; i < spawnsAmt; i++)
				{
					String path = "spawns." + i + ".";

					ArenaLocation loc = new ArenaLocation(worldName, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getSpawns().add(loc);
				}
			}
			else if (arenaType.equalsIgnoreCase("spleef"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				String specialType = fc.getString("specialType");
				az.setSpecialType(Material.matchMaterial(specialType));
				az.setSpecialTypeString(specialType);

				for (int i = 0; i < 4; i++)
				{
					String path = "flags." + i + ".";

					ArenaLocation loc = new ArenaLocation(worldName, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));

					az.getFlags().add(loc);
				}
			}
			else if (arenaType.equalsIgnoreCase("bomb"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new ArenaLocation(worldName, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new ArenaLocation(worldName, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new ArenaLocation(worldName, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));

				az.getFlags().add(new ArenaLocation(worldName, fc.getInt("flag0.x"), fc.getInt("flag0.y"), fc.getInt("flag0.z")));
				az.getFlags().add(new ArenaLocation(worldName, fc.getInt("flag1.x"), fc.getInt("flag1.y"), fc.getInt("flag1.z")));
			}
			else if (arenaType.equalsIgnoreCase("ctf"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new ArenaLocation(worldName, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new ArenaLocation(worldName, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new ArenaLocation(worldName, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));

				az.getFlags().add(new ArenaLocation(worldName, fc.getInt("flag0.x"), fc.getInt("flag0.y"), fc.getInt("flag0.z")));
				az.getFlags().add(new ArenaLocation(worldName, fc.getInt("flag1.x"), fc.getInt("flag1.y"), fc.getInt("flag1.z")));
			}
			else if (arenaType.equalsIgnoreCase("infect"))
			{
				az.setLobbyREDspawn(new ArenaLocation(worldName, fc.getInt("lobbyRed.x"), fc.getInt("lobbyRed.y"), fc.getInt("lobbyRed.z")));

				az.setLobbyBLUspawn(new ArenaLocation(worldName, fc.getInt("lobbyBlue.x"), fc.getInt("lobbyBlue.y"), fc.getInt("lobbyBlue.z")));

				az.setTeam1spawn(new ArenaLocation(worldName, fc.getInt("team1.x"), fc.getInt("team1.y"), fc.getInt("team1.z")));

				az.setTeam2spawn(new ArenaLocation(worldName, fc.getInt("team2.x"), fc.getInt("team2.y"), fc.getInt("team2.z")));
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