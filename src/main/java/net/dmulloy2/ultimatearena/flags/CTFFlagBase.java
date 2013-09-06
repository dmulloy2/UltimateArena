package net.dmulloy2.ultimatearena.flags;

import java.util.List;

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

public class CTFFlagBase extends FlagBase
{
	private CTFArena ctf;
	private CTFFlag flag;
	private CTFFlag enemyflag;

	private int team;

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
		this.setFlag(new CTFFlag(getArena(), getLoc().clone().add(0, 1, 0), team));

		Location flag = getLoc().clone().add(0, 5, 0);
		setNotify(flag.getBlock());
		getNotify().setType(Material.AIR);
	}

	@Override
	public void checkNear(List<ArenaPlayer> arenaplayers)
	{
		flag.checkNear(arenaplayers);

		if (!enemyflag.isPickedUp())
			return;

		if (enemyflag.getRiding() == null)
			return;

		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer a = arenaplayers.get(i);
			if (!a.isOut() && a.getPlayer().isOnline() && !a.getPlayer().isDead())
			{
				if (a.getTeam() == team)
				{
					// If the arena player is on my team
					Player p = a.getPlayer();
					if (enemyflag.getRiding().getName().equals(p.getName()))
					{
						// If the player selected is carrying the enemy flag
						if (Util.pointDistance(p.getLocation(), getLoc().clone().add(0, 1, 0)) < 2.75)
						{
							// If hes close to my flag stand, REWARD!
							enemyflag.respawn();
							a.sendMessage("&aFlag Captured! &c+ 500 XP");

							p.removePotionEffect(PotionEffectType.SLOW);
							p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

							for (int ii = 0; ii < arenaplayers.size(); ii++)
							{
								ArenaPlayer ap = arenaplayers.get(ii);
								if (!ap.isOut())
								{
									if (ap.getTeam() == a.getTeam())
									{
										ap.sendMessage("&aUnlocked 10 seconds of crits!");
										ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
										ap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 1));
									}
								}
							}

							a.setGameXP(a.getGameXP() + 500);
							arena.tellPlayers("&e{0} &3captured the &e{1} &3flag!", a.getName(), enemyflag.getFlagType());

							if (team == 1)
							{
								ctf.redcap++;
								getArena().tellPlayers("&e{0} &3team has &e{1}&3/&e3 &3captures!", TeamHelper.getTeam(team), ctf.redcap);
							}
							if (team == 2)
							{
								ctf.bluecap++;
								getArena().tellPlayers("&e{0} &3team has &e{1}&3/&e3 &3captures!", TeamHelper.getTeam(team), ctf.bluecap);
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
			this.enemyflag = ctf.flagblue.getFlag();
		if (team == 2)
			this.enemyflag = ctf.flagred.getFlag();
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