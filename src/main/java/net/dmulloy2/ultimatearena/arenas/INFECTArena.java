package net.dmulloy2.ultimatearena.arenas;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.util.Util;

public class INFECTArena extends PVPArena
{
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
			ArenaPlayer apl = arenaPlayers.get(Util.random(arenaPlayers.size()));
			if (apl != null && apl.getPlayer().isOnline())
			{
				apl.setTeam(2);
				apl.sendMessage("&9You have been chosen for the infected!");
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
			this.tellPlayers("&cError starting!");
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
					this.tellPlayers("&9Infected Win!");
					this.stop();
					this.rewardTeam(2, "&9You win!", true);
				}
				else
				{
					this.tellPlayers("&9One team is empty! game ended!");
					this.stop();
				}
			}
			else
			{
				if (this.getAmtPlayersStartingInArena() <= 1) 
				{
					this.tellPlayers("&9Not enough people to play!");
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
		this.rewardTeam(1, "&9You survived!", false);
	}
	
	@Override
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		if (pl.getTeam() == 1)
		{
			pl.sendMessage("&bYou have joined the Infected!");
		}
		pl.setTeam(2);
	}
}