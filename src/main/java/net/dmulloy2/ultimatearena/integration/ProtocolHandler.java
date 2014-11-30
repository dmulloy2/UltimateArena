/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.integration;

import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.integration.IntegrationHandler;
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
public class ProtocolHandler extends IntegrationHandler
{
	private final UltimateArena plugin;
	private ProtocolManager manager;
	private boolean enabled;

	public ProtocolHandler(UltimateArena plugin)
	{
		this.plugin = plugin;
		this.setup();
	}

	@Override
	public void setup()
	{
		try
		{
			if (plugin.getServer().getPluginManager().isPluginEnabled("ProtocolLib"))
			{
				manager = ProtocolLibrary.getProtocolManager();
				enabled = true;

				plugin.getLogHandler().log("ProtocolLib integration successful!");
			}
		}
		catch (Throwable ex)
		{
			enabled = false;
		}
	}

	public void forceRespawn(Player player)
	{
		try
		{
			PacketContainer packet = manager.createPacket(PacketType.Play.Client.CLIENT_COMMAND);
			packet.getClientCommands().write(0, EnumWrappers.ClientCommand.PERFORM_RESPAWN);
			manager.sendServerPacket(player, packet);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "forcing " + player.getName() + " to respawn"));
		}
	}
}