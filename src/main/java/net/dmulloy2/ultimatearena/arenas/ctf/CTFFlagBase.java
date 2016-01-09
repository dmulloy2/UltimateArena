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
package net.dmulloy2.ultimatearena.arenas.ctf;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.FlagBase;
import net.dmulloy2.ultimatearena.types.Team;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class CTFFlagBase extends FlagBase
{
	protected CTFFlag enemyflag;

	protected final Team team;
	protected final CTFFlag flag;
	protected final CTFArena arena;

	public CTFFlagBase(CTFArena arena, ArenaLocation location, Team team, UltimateArena plugin)
	{
		super(arena, location, plugin);
		this.arena = arena;
		this.team = team;

		this.flag = new CTFFlag(arena, location.getLocation().clone().add(0, 1, 0), team);
		this.flag.setTeam(team);
		this.flag.colorize();
	}

	@Override
	protected void setup()
	{
		this.notify = location.clone().add(0.0D, 5.0D, 0.0D).getBlock();
		this.notify.setType(Material.AIR);
	}

	@Override
	public void checkNear(ArenaPlayer[] arenaPlayers)
	{
		flag.checkNear(arenaPlayers);

		if (! enemyflag.isPickedUp())
			return;

		if (enemyflag.getRiding() == null)
			return;

		for (ArenaPlayer ap : arenaPlayers)
		{
			Player player = ap.getPlayer();
			if (ap.isOnline() && player.getHealth() > 0.0D)
			{
				if (ap.getTeam() == team)
				{
					// If the arena player is on my team
					if (enemyflag.getRiding().getName().equals(ap.getName()))
					{
						// If the player selected is carrying the enemy flag
						if (player.getWorld().getUID().equals(location.getWorld().getUID())
								&& player.getLocation().distance(location.clone().add(0.0D, 1.0D, 0.0D)) < 2.75D)
						{
							// If hes close to my flag stand, REWARD!
							enemyflag.respawn();
							ap.sendMessage(getMessage("youCapturedFlag"));

							ap.getPlayer().removePotionEffect(PotionEffectType.SLOW);
							ap.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

							for (ArenaPlayer apl : arenaPlayers)
							{
								if (ap.getTeam() == apl.getTeam())
								{
									apl.sendMessage(getMessage("youUnlockedCrits"));
									apl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
									apl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 1));
								}
							}

							ap.setGameXP(ap.getGameXP() + 500);

							arena.tellPlayers(getMessage("flagCaptured"), ap.getName(), enemyflag.getFlagType());
							arena.capture(team);
							return;
						}
					}
				}
			}
		}
	}

	public void initialize()
	{
		if (team == Team.RED)
			this.enemyflag = arena.getBlueFlag().getFlag();
		if (team == Team.BLUE)
			this.enemyflag = arena.getRedFlag().getFlag();
	}
}
