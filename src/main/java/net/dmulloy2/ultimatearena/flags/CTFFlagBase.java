package net.dmulloy2.ultimatearena.flags;

import java.util.List;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.CTFArena;
import net.dmulloy2.ultimatearena.types.ArenaPlayer;
import net.dmulloy2.ultimatearena.util.TeamHelper;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

@Getter
public class CTFFlagBase extends FlagBase
{
	protected CTFArena ctf;
	protected CTFFlag flag;
	protected CTFFlag enemyflag;

	protected int team;

	public CTFFlagBase(Arena arena, Location loc, int team, final UltimateArena plugin)
	{
		super(arena, loc, plugin);
		this.arena = arena;
		this.team = team;
		this.ctf = (CTFArena) arena;

		flag.setTeam(team);
		flag.colorize();
	}

	@Override
	public void setup()
	{
		super.setup();
		this.setFlag(new CTFFlag(getArena(), location.clone().add(0, 1, 0), team));

		Location flag = location.clone().add(0, 5, 0);
		setNotify(flag.getBlock());
		getNotify().setType(Material.AIR);
	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaPlayers)
	{
		flag.checkNear(arenaPlayers);

		if (! enemyflag.isPickedUp())
			return;

		if (enemyflag.getRiding() == null)
			return;

		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap.getPlayer().isOnline() && ! ap.getPlayer().isDead())
			{
				if (ap.getTeam() == team)
				{
					// If the arena player is on my team
					Player p = ap.getPlayer();
					if (enemyflag.getRiding().getName().equals(p.getName()))
					{
						// If the player selected is carrying the enemy flag
						if (Util.pointDistance(p.getLocation(), location.clone().add(0, 1, 0)) < 2.75)
						{
							// If hes close to my flag stand, REWARD!
							enemyflag.respawn();
							ap.sendMessage("&aFlag Captured! &c+ 500 XP");

							p.removePotionEffect(PotionEffectType.SLOW);
							p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

							for (int ii = 0; i < arenaPlayers.size(); ii++)
							{
								ArenaPlayer apl = arenaPlayers.get(ii);
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
								ctf.setRedCap(ctf.getRedCap() + 1);
								arena.tellPlayers("&e{0} &3team has &e{1}&3/&e3 &3captures!", TeamHelper.getTeam(team), ctf.getRedCap());
							}

							if (team == 2)
							{
								ctf.setBlueCap(ctf.getBlueCap() + 1);
								arena.tellPlayers("&e{0} &3team has &e{1}&3/&e3 &3captures!", TeamHelper.getTeam(team), ctf.getBlueCap());
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
			this.enemyflag = ctf.getBlueFlag().getFlag();
		if (team == 2)
			this.enemyflag = ctf.getRedFlag().getFlag();
	}

	public CTFFlag getFlag()
	{
		return flag;
	}

	public void setFlag(CTFFlag flag)
	{
		this.flag = flag;
	}
}