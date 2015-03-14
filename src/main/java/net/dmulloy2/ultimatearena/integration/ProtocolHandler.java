/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.integration;

import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.integration.DependencyProvider;
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

@Getter
public class ProtocolHandler extends DependencyProvider<ProtocolLibrary>
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