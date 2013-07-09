package net.dmulloy2.ultimatearena.arenas;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaFlag;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaSpawn;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.KothFlag;
import net.dmulloy2.ultimatearena.util.Util;

public class KOTHArena extends Arena
{
	public int MAXPOWER;
	
	public KOTHArena(ArenaZone az) 
	{
		super(az);
		
		setType("Koth");
		setAllowTeamKilling(true);
		setStarttimer(180);
		setGametimer(0);
		setMaxgametime(60 * 20);
		setMaxDeaths(900); //'dont think anyone will get 900 deaths :P
		MAXPOWER = 60;
		
		for (int i = 0; i < this.getArenaZone().getFlags().size(); i++)
		{
			this.getFlags().add( new KothFlag(this, this.getArenaZone().getFlags().get(i)) );
		}
		for (int i = 0; i < this.getArenaZone().getSpawns().size(); i++) 
		{
			this.getSpawns().add( new ArenaSpawn(this.getArenaZone().getSpawns().get(i).getWorld(), this.getArenaZone().getSpawns().get(i).getBlockX(), this.getArenaZone().getSpawns().get(i).getBlockY(), this.getArenaZone().getSpawns().get(i).getBlockZ()) );
		}
	}
	
	@Override
	public void doKillStreak(ArenaPlayer ap) 
	{
		Player pl = Util.matchPlayer(ap.getPlayer().getName());
		if (pl != null) 
		{
			if (ap.getKillstreak() == 2) 
			{
				givePotion(pl, "strength", 1, 1, false, "2 kills! Unlocked strength potion!");
			}
			if (ap.getKillstreak() == 4) 
			{
				givePotion(pl, "heal", 1, 1, false, "4 kills! Unlocked health potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "4 kills! Unlocked food!");
			}
			if (ap.getKillstreak() == 12) 
			{
				givePotion(pl, "regen", 1, 1, false, "12 kills! Unlocked regen potion!");
				giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "12 kills! Unlocked food!");
			}
		}
	}
	
	@Override
	public void reward(ArenaPlayer p, Player pl, boolean half) 
	{
		if (p.getPoints() >= MAXPOWER) 
		{ 
			//if you scored at least 60 points
			super.reward(p, pl, half);
		}
	}
	
	@Override
	public void spawn(String name, boolean alreadySpawned) 
	{
		super.spawn(name, false);
		spawnRandom(name);
	}
	
	@Override
	public void onStart()
	{
		for (ArenaPlayer ap : arenaPlayers)
		{
			spawn(ap.getUsername(), false);
		}
	}
	
	@Override
	public void check()
	{
		for (ArenaFlag flag : getFlags())
		{
			flag.step();
			flag.checkNear(arenaPlayers);
		}

		checkPlayerPoints(MAXPOWER);
		checkEmpty();
	}
}