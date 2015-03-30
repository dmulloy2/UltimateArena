/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.handlers;

import java.io.File;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.arenas.spleef.SpleefZone;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class FileHandler
{
	private final UltimateArena plugin;

	/**
	 * Loads a legacy ArenaZone.
	 *
	 * @param az {@link ArenaZone} to load
	 * @deprecated Serialization
	 */
	@Deprecated
	public final void loadLegacy(ArenaZone az)
	{
		plugin.debug("Loading Arena: {0}", az.getName());

		try
		{
			File folder = new File(plugin.getDataFolder(), "arenas");
			File file = new File(folder, az.getName() + ".dat");

			YamlConfiguration fc = new YamlConfiguration();
			fc.load(file);

			String worldName = fc.getString("world");
			if (worldName == null || worldName.isEmpty())
			{
				plugin.getLogHandler().log(Level.SEVERE, "Could not load Arena {0}: World cannot be null!", az.getName());
				az.setLoaded(false);
				return;
			}

			az.setWorldName(worldName);

			az.setLobby1(new ArenaLocation(worldName, fc.getInt("lobby1.x"), 0, fc.getInt("lobby1.z")));
			az.setLobby2(new ArenaLocation(worldName, fc.getInt("lobby2.x"), 0, fc.getInt("lobby2.z")));

			az.setArena1(new ArenaLocation(worldName, fc.getInt("arena1.x"), 0, fc.getInt("arena1.z")));
			az.setArena2(new ArenaLocation(worldName, fc.getInt("arena2.x"), 0, fc.getInt("arena2.z")));

			ArenaType type = az.getType();
			String arenaType = type.getName().toLowerCase();
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
				((SpleefZone) az).setSpecialType(MaterialUtil.getMaterial(specialType));

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

			az.getConfig().setMaxPlayers(fc.getInt("maxPlayers", 24));
			az.getConfig().setDefaultClass(fc.getString("defaultClass"));

			az.setLoaded(true);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading legacy arena " + az.getName()));
			az.setLoaded(false);
		}
	}
}
