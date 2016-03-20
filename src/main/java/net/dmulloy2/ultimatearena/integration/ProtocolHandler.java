/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2016 dmulloy2
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
package net.dmulloy2.ultimatearena.integration;

import java.util.logging.Level;

import net.dmulloy2.integration.TypelessProvider;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

/**
 * @author dmulloy2
 */

public class ProtocolHandler extends TypelessProvider
{
	private ProtocolManager manager;

	public ProtocolHandler(UltimateArena plugin)
	{
		super(plugin, "ProtocolLib");
	}

	@Override
	public void onEnable()
	{
		if (! isEnabled())
			return;

		try
		{
			manager = ProtocolLibrary.getProtocolManager();
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "setup()"));
		}
	}

	@Override
	public void onDisable()
	{
		manager = null;
	}

	public final void forceRespawn(Player player)
	{
		if (manager == null)
			return;

		try
		{
			PacketContainer packet = manager.createPacket(PacketType.Play.Client.CLIENT_COMMAND);
			packet.getClientCommands().write(0, EnumWrappers.ClientCommand.PERFORM_RESPAWN);
			manager.recieveClientPacket(player, packet);
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "forcing {0} to respawn", player.getName()));
		}
	}
}
