package net.dmulloy2.ultimatearena.arenas;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.ultimatearena.arenas.objects.ArenaFlag;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.arenas.objects.FieldType;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.Location;

public class CONQUESTArena extends Arena
{
	public int REDTEAMPOWER = 100;
	public int BLUETEAMPOWER = 100;
	
	public CONQUESTArena(ArenaZone az) 
	{
		super(az);
		
		this.type = FieldType.CONQUEST;
		this.startTimer = 180;
		this.maxGameTime = 60 * 20;
		this.maxDeaths = 900;
		
		for (int i = 0; i < az.getFlags().size(); i++) 
		{
			flags.add( new ArenaFlag(this, az.getFlags().get(i), plugin) );
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		this.REDTEAMPOWER = getActivePlayers() * 4;
		this.BLUETEAMPOWER = REDTEAMPOWER;
		if (REDTEAMPOWER < 4) 
		{
			REDTEAMPOWER = 4;
		}
		if (REDTEAMPOWER > 150) 
		{
			REDTEAMPOWER = 150;
		}
		if (BLUETEAMPOWER < 4) 
		{
			BLUETEAMPOWER = 4;
		}
		if (BLUETEAMPOWER > 150)
		{
			BLUETEAMPOWER = 150;
		}
	}
	
	@Override
	public Location getSpawn(ArenaPlayer ap)
	{
		if (isInLobby())
		{
			return super.getSpawn(ap);
		}
		
		if (ap != null) 
		{
			if (!ap.isOut())
			{
				List<ArenaFlag> spawnto = new ArrayList<ArenaFlag>();
				for (int i = 0; i < getFlags().size(); i++)
				{
					if (getFlags().get(i).team == ap.getTeam())
					{
						if (getFlags().get(i).capped)
						{
							spawnto.add(getFlags().get(i));
						}
					}
				}
				
				if (! spawnto.isEmpty())
				{
					int rand = Util.random(spawnto.size());
					ArenaFlag flag = spawnto.get(rand);
					if (flag != null)
					{
						return flag.getLoc();
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void onPlayerDeath(ArenaPlayer pl) 
	{
		super.onPlayerDeath(pl);
		
		int majority = 0;
		int red = 0;
		int blu = 0;
		for (int i = 0; i < getFlags().size(); i++) 
		{
			if (getFlags().get(i).color == 14) 
			{
				if (getFlags().get(i).capped == true) 
				{
					red++;
				}
			}
			else if (getFlags().get(i).color == 11) 
			{
				if (getFlags().get(i).capped == true) 
				{
					blu++;
				}
			}
		}
		
		if (blu > red) { majority = 1; }
		if (red > blu) { majority = 2; }
	
		if (majority == 1) 
		{
			REDTEAMPOWER--;
		}
		else if (majority == 2) 
		{
			BLUETEAMPOWER--;
		}
		
		if (pl.getTeam() == 1) 
		{
			REDTEAMPOWER--;
			for (int i = 0; i < arenaPlayers.size(); i++) 
			{
				ArenaPlayer apl = arenaPlayers.get(i);
				if (apl != null && ! apl.isOut())
				{ 
					if (apl.getTeam() == 1)
					{
						apl.sendMessage("&cYour power is now: &6{0}", REDTEAMPOWER);
					}
					else
					{
						apl.sendMessage("&cThe other team's power is now: &6{0}", REDTEAMPOWER);
					}
				}
			}
		}
		else if (pl.getTeam() == 2) 
		{
			BLUETEAMPOWER--;
			for (int i = 0; i < arenaPlayers.size(); i++) 
			{
				ArenaPlayer apl = arenaPlayers.get(i);
				if (apl != null && ! apl.isOut())
				{ 
					if (apl.getTeam() == 2)
					{
						apl.sendMessage("&cYour power is now: &6{0}", BLUETEAMPOWER);
					}
					else
					{
						apl.sendMessage("&cThe other team's power is now: &6{0}", BLUETEAMPOWER);
					}
				}
			}
		}
	}
	
	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}
	
	@Override
	public void check() 
	{
		for (int i = 0; i < arenaPlayers.size(); i++)
		{
			ArenaPlayer ap = arenaPlayers.get(i);
			if (ap != null && ! ap.isOut())
			{
				if (BLUETEAMPOWER <= 0) 
				{
					if (ap.getTeam() == 2)
					{
						endPlayer(ap, ap.getPlayer(), false);
					}
				}
				else if (REDTEAMPOWER <= 0) 
				{
					if (ap.getTeam() == 1)
					{
						endPlayer(ap, ap.getPlayer(), false);
					}
				}
			}
		}
		
		if (BLUETEAMPOWER <= 0)
		{
			setWinningTeam(1);
		}
		
		if (REDTEAMPOWER <= 0) 
		{
			setWinningTeam(2);
		}
			
		for (int i = 0; i < getFlags().size(); i++)
		{
			ArenaFlag flag = getFlags().get(i);
			
			flag.step();
			flag.checkNear(arenaPlayers);
		}
		
		if (startTimer <= 0) 
		{
			if (! simpleTeamCheck(false)) 
			{
				stop();
				
				rewardTeam(-1, false);
			}
		}
	}
}