package net.dmulloy2.ultimatearena.arenas.koth;

import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.integration.WorldEditHandler;
import net.dmulloy2.ultimatearena.types.ArenaCreator;
import net.dmulloy2.ultimatearena.types.ArenaLocation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class KOTHCreator extends ArenaCreator
{
	public KOTHCreator(Player player, String name, ArenaType type)
	{
		super(player, name, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPoint(String[] args)
	{
		Player player = getPlayer();
		switch (stepNumber)
		{
			case 1: // Arena
			{
				WorldEditHandler worldEdit = plugin.getWorldEditHandler();
				if (worldEdit != null && worldEdit.isEnabled())
				{
					if (! worldEdit.hasSelection(player))
					{
						sendMessage("&cYou must have a WorldEdit selection to do this!");
						return;
					}

					com.sk89q.worldedit.bukkit.selections.Selection sel = worldEdit.getSelection(player);
					if (! worldEdit.isCuboidSelection(sel))
					{
						sendMessage("&cYou must have a cuboid selection to do this!");
						return;
					}

					Location arena1 = sel.getMaximumPoint();
					Location arena2 = sel.getMinimumPoint();

					// Perform some checks
					if (arena1 == null || arena2 == null)
					{
						sendMessage("&cPlease make sure you have two valid points in your selection!");
						return;
					}

					if (plugin.isInArena(arena1) || plugin.isInArena(arena2))
					{
						sendMessage("&4These points overlap an existing arena: &c{0}&4!", plugin.getZoneInside(arena1));
						sendMessage("&4This is known to cause some errors! Type &c/ua undo &4to undo!");
					}

					target.setArena1(new ArenaLocation(arena1));
					target.setArena2(new ArenaLocation(arena2));

					sendMessage("&3Arena points set!");
					break; // Step completed
				}
				else
				{
					if (target.getArena1() == null)
					{
						target.setArena1(new ArenaLocation(player));
						sendMessage("&3First point set.");
						sendMessage("&3Please set the &e2nd &3point.");
						return;
					}
					else
					{
						target.setArena2(new ArenaLocation(player));
						sendMessage("&3Second point set!");
						break; // Step completed
					}
				}
			}
			case 2: // Lobby
			{
				WorldEditHandler worldEdit = plugin.getWorldEditHandler();
				if (worldEdit != null && worldEdit.isEnabled())
				{
					if (! worldEdit.hasSelection(player))
					{
						sendMessage("&cYou must have a WorldEdit selection to do this!");
						return;
					}

					com.sk89q.worldedit.bukkit.selections.Selection sel = worldEdit.getSelection(player);
					if (! worldEdit.isCuboidSelection(sel))
					{
						sendMessage("&cYou must have a cuboid selection to do this!");
						return;
					}

					Location lobby1 = sel.getMaximumPoint();
					Location lobby2 = sel.getMinimumPoint();

					// Perform some checks
					if (lobby1 == null || lobby2 == null)
					{
						sendMessage("&cPlease make sure you have two valid points in your selection!");
						return;
					}

					if (plugin.isInArena(lobby1) || plugin.isInArena(lobby2))
					{
						sendMessage("&4These points overlap an existing arena: &c{0}&4!", plugin.getZoneInside(lobby1));
						sendMessage("&4This is known to cause some errors! Type &c/ua undo &4to undo!");
					}

					if (lobby1.getWorld().getUID() != target.getArena1().getWorld().getUID())
					{
						sendMessage("&cYou must make your lobby in the same world as your arena!");
						return;
					}

					target.setLobby1(new ArenaLocation(lobby1));
					target.setLobby2(new ArenaLocation(lobby2));

					sendMessage("&3Lobby points set!");
					break; // Step completed
				}
				else
				{
					ArenaLocation loc = new ArenaLocation(player);
					if (plugin.isInArena(loc))
					{
						sendMessage("&cThis point overlaps an existing arena!");
						return;
					}

					if (loc.getWorld().getUID() != target.getArena1().getWorld().getUID())
					{
						sendMessage("&cYou must make your lobby in the same world as your arena!");
						return;
					}

					if (target.getLobby1() == null)
					{
						target.setLobby1(new ArenaLocation(player));
						sendMessage("&3First point set.");
						sendMessage("&3Please set the &e2nd &3point.");
						return;
					}
					else
					{
						target.setLobby2(new ArenaLocation(player));
						sendMessage("&3Second point set!");
						break; // Step completed
					}
				}
			}
			case 3:
			{
				target.setLobbyREDspawn(new ArenaLocation(player));
				sendMessage("&eLobby &3spawnpoint set!");
				break; // Step completed
			}
			case 4:
			{
				target.getFlags().add(new ArenaLocation(player));
				sendMessage("&eKOTH flag &3spawnpoint set.");
				break; // Step completed
			}
			case 5:
			{
				if (args.length > 0 && args[0].equalsIgnoreCase("done"))
				{
					sendMessage("&3Done setting player spawns!");
					break; // Step completed
				}
				else
				{
					target.getSpawns().add(new ArenaLocation(player));
					sendMessage("&ePlayer &3spawn set. Total: &e{0}", target.getSpawns().size());
					return;
				}
			}
		}

		stepUp(); // Next step
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stepInfo()
	{
		switch (stepNumber)
		{
			case 1:
				sendMessage("&3Please set &e2 &3points for the arena");
				break;
			case 2:
				sendMessage("&3Please set &e2 &3points for the lobby.");
				break;
			case 3:
				sendMessage("&3Please set the &eLobby &3spawn.");
				break;
			case 4:
				sendMessage("&3Please set the &eKOTH flag &3spawnpoint.");
				break;
			case 5:
				sendMessage("&3Please add some &eplayer &3spawns.");
				sendMessage("&3Use &e/ua sp done &3when they are set.");
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSteps()
	{
		this.steps = 5;
	}
}