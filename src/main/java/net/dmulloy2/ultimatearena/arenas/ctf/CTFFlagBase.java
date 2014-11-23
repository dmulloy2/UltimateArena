package net.dmulloy2.ultimatearena.arenas.ctf;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaLocation;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.types.FlagBase;
import net.dmulloy2.ultimatearena.types.TeamHelper;

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

	protected final int team;
	protected final CTFFlag flag;
	protected final CTFArena arena;

	public CTFFlagBase(CTFArena arena, ArenaLocation location, int team, UltimateArena plugin)
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
	public void checkNear(List<ArenaPlayer> arenaPlayers)
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
							ap.sendMessage("&aFlag Captured! &c+ 500 XP");

							ap.getPlayer().removePotionEffect(PotionEffectType.SLOW);
							ap.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

							for (ArenaPlayer apl : arenaPlayers)
							{
								if (ap.getTeam() == apl.getTeam())
								{
									apl.sendMessage("&aUnlocked 10 seconds of crits!");
									apl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
									apl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 1));
								}
							}

							ap.setGameXP(ap.getGameXP() + 500);
							arena.tellPlayers("&e{0} &3captured the &e{1} &3flag!", ap.getName(), enemyflag.getFlagType());

							if (team == 1)
							{
								arena.setRedCap(arena.getRedCap() + 1);
								arena.tellPlayers("&e{0} &3team has &e{1}&3/&e3 &3captures!", TeamHelper.getTeam(team),
										arena.getRedCap());
							}

							if (team == 2)
							{
								arena.setBlueCap(arena.getBlueCap() + 1);
								arena.tellPlayers("&e{0} &3team has &e{1}&3/&e3 &3captures!", TeamHelper.getTeam(team),
										arena.getBlueCap());
							}

							return;
						}
					}
				}
			}
		}
	}

	public void initialize()
	{
		if (team == 1)
			this.enemyflag = arena.getBlueFlag().getFlag();
		if (team == 2)
			this.enemyflag = arena.getRedFlag().getFlag();
	}
}