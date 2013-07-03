package net.dmulloy2.ultimatearena.arenas;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

public class INFECTArena extends PVPArena
{

	ArenaPlayer originalZombie = null;
	
	public INFECTArena(ArenaZone az)
	{
		super(az);
		
		setType("Infect");
		setStarttimer(80);
		setGametimer(0);
		setMaxgametime((60 * 2) + 10); //2 minutes and 10 seconds (by default)
		setMaxDeaths(99);
	}
	
	@Override
	public int getTeam() 
	{
		return 1; //blue team = default
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		chooseInfected(0);
	}
	
	public void chooseInfected(int tries) 
	{
		if (tries < 16)
		{
			ArenaPlayer apl = this.getArenaplayers().get(Util.random(this.getArenaplayers().size()));
			if (apl != null && apl.getPlayer().isOnline())
			{
//				apl.setPlayer(Util.matchPlayer(apl.getUsername()));
				apl.setTeam(2);
//				spawn(apl.player.getName(), true);
				apl.getPlayer().sendMessage(ChatColor.BLUE + "You have been chosen for the infected!");
				onSpawn(apl);
				tellPlayers("&c{0} &bis the zombie!", apl.getPlayer().getName());
			}
			else
			{
				chooseInfected(tries+1);
			}
		}
		else
		{
			this.tellPlayers(ChatColor.RED + "Error starting!");
			stop();
		}
	}

	@Override
	public void onSpawn(ArenaPlayer apl) 
	{
		if (apl.getTeam() == 2)
		{
			Player pl = apl.getPlayer();
			normalize(apl.getPlayer());
			//pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2400, 2));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2400, 1));
			pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 1));
			spawn(apl.getPlayer().getName(), true);
			normalize(apl.getPlayer());
			apl.decideHat(apl.getPlayer());
		}
	}

	@Override
	public void check() 
	{
		if (getStarttimer() <= 0)
		{
			if (!simpleTeamCheck(false)) 
			{
				if (this.getTeam1size() == 0)
				{
					this.setWinningTeam(2);
					this.tellPlayers(ChatColor.BLUE + "Infected Win!");
					this.stop();
					this.rewardTeam(2, ChatColor.YELLOW + "You win!", true);
				}
				else
				{
					this.tellPlayers(ChatColor.BLUE + "One team is empty! game ended!");
					this.stop();
				}
			}
			else
			{
				if (this.getAmtPlayersStartingInArena() <= 1) 
				{
					this.tellPlayers(ChatColor.BLUE + "Not enough people to play!");
					this.stop();
				}
			}
		}
	}
	
	@Override
	public void onPreOutOfTime()
	{
		this.setWinningTeam(1);
	}
	
	@Override
	public void onOutOfTime() 
	{
		this.rewardTeam(1, ChatColor.BLUE + "You survived!", false);
	}
	
	@Override
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		if (pl.getTeam() == 1)
		{
			pl.getPlayer().sendMessage(ChatColor.AQUA + "You have joined the Infected!");
		}
		pl.setTeam(2);
	}
}