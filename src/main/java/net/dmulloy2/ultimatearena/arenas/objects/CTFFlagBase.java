package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.CTFArena;
import net.dmulloy2.ultimatearena.util.Util;

public class CTFFlagBase extends FlagBase 
{
	public CTFArena ctf;
	public CTFflag flag;
	public CTFflag enemyflag;
	
	public int team;
	
	public CTFFlagBase(Arena arena, Location loc, int team) 
	{
		super(arena, loc);
		this.arena = arena;
		this.plugin = arena.az.plugin;
		this.team = team;
		this.ctf = (CTFArena)arena;
		this.flag.team = team;
		this.flag.colorize();
	}
	
	@Override
	public void setup() 
	{
		super.setup();
		this.flag = new CTFflag(arena, loc.clone().add(0,1,0), team);

		Location flag = getLoc().clone().add(0, 5, 0);
		notify = flag.getBlock();
		notify.setType(Material.AIR);
	}
	
	@Override
	public void checkNear(List<ArenaPlayer> arenaplayers) 
	{
		flag.checkNear(arenaplayers);
		if (!enemyflag.pickedUp) 
			return;
		if (enemyflag.riding == null)
			return;
		
		for (int i = 0; i < arenaplayers.size(); i++)
		{
			ArenaPlayer a = arenaplayers.get(i);
			try
			{
				if (!a.out && a.player.isOnline() && !a.player.isDead()) 
				{
					if (a.team == team)
					{ 
						//if the arena player is on my team
						Player p = a.player;
						if (enemyflag.riding.getName().equals(p.getName()))
						{
							//if the player selected is carrying the enemy flag
							if (Util.pointDistance(p.getLocation(), getLoc().clone().add(0, 1, 0)) < 2.75) 
							{ 
								//if hes close to my flag stand, REWARD!
								enemyflag.respawn();
								p.sendMessage(ChatColor.GRAY + "Flag Captured! " + ChatColor.RED + " +500 XP");
								
								p.removePotionEffect(PotionEffectType.SLOW);
								p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
								
								for (int ii = 0; ii < arenaplayers.size(); ii++) 
								{
									try
									{
										ArenaPlayer ap = arenaplayers.get(ii);
										if (!ap.out) 
										{
											if (ap.team == a.team)
											{
												ap.player.sendMessage(ChatColor.GREEN + "Unlocked 10 seconds of crits!");
												ap.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
												ap.player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 1));
											}
										}
									}
									catch(Exception e) 
									{
										//
									}
								}
								
								a.gameXP += 500;
								arena.tellPlayers(ChatColor.GREEN + a.player.getName() + ChatColor.GRAY + " captured the " + enemyflag.flagType + ChatColor.GRAY + " flag");
								if (team == 1)
								{
									ctf.redcap++;
									arena.tellPlayers("&c{0} &7team has &a{1}&7/&a3 &7captures!", TeamHelper.getTeam(team), ctf.redcap);
								}
								if (team == 2) 
								{
									ctf.bluecap++;
									arena.tellPlayers("&c{0} &7team has &a{1}&7/&a3 &7captures!", TeamHelper.getTeam(team), ctf.bluecap);
								}
								return;
							}
						}
					}
				}
			}
			catch(Exception e) 
			{
				plugin.getLogger().severe("Error with CTF flagbase: " + e.getMessage());
			}
		}
	}

	public void initialize() 
	{
		if (team == 1)
			this.enemyflag = ctf.flagblue.flag;
		if (team == 2)
			this.enemyflag = ctf.flagred.flag;
	}
}