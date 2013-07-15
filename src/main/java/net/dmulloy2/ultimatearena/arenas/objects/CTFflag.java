package net.dmulloy2.ultimatearena.arenas.objects;

import java.util.List;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CTFflag
{
	private Player riding;
	
	private String flagType = "";
	
	private Arena arena;
	
	private Location returnto;
	private Location myloc;
	private Location toloc;
	private Location lastloc;
	
	private int lastBlockType;
	private int team;
	private int timer = 15;
	
	private boolean pickedUp;
	private boolean stopped;
	
	private byte color;
	private byte lastBlockDat;

	public CTFflag(Arena a, Location loc, int team)
	{
		this.setTeam(team);
		this.arena = a;
		
		this.setReturnto(loc.clone());
		this.myloc = loc.clone();
		this.lastloc = loc.clone();
		this.toloc = loc.clone();
		
		loc.getBlock().setTypeIdAndData(0, (byte)0, false);
		
		setup();
	}
	
	public void respawn() 
	{
		timer = 15;
		setPickedUp(false);
		setRiding(null);
		toloc = getReturnto().clone();
		myloc = toloc.clone();
		setFlag();
	}
	
	public void notifyTime() 
	{
		if (timer % 5 == 0 || timer < 10) 
		{
			sayTimeLeft();
		}
	}
	
	public void sayTimeLeft() 
	{
		arena.tellPlayers("&d{0} &7seconds left until &6{1} &7flag returns!", timer, getFlagType());
	}
	
	public void setup()
	{
		final Block current = myloc.getBlock();
		lastBlockDat = current.getData();
		lastBlockType = current.getTypeId();
		colorize();
	}
	
	public void colorize() 
	{
		Block current = myloc.getBlock();
		if (getTeam() == 1) 
		{
			color = 14; // Red team
			setFlagType(ChatColor.RED + "RED");
		}
		else
		{
			color = 11; // Blue team
			setFlagType(ChatColor.BLUE + "BLUE");
		}
		
		setFlagBlock(current);
	}
	
	public void fall() 
	{
		arena.tellPlayers("&b{0} &7has dropped the &6{1} &7flag!", getRiding().getName(), getFlagType());
		timer = 15;
		toloc = getRiding().getLocation();
		setPickedUp(false);
		setRiding(null);
		
		myloc = toloc.clone();
	    		
		int count = 0;
		boolean can = true;
		for (int i = 1; i < 128; i++) 
		{
			if (can) 
			{
				Block BlockUnder = ((myloc.clone()).subtract(0,i,0)).getBlock(); 
				if (BlockUnder != null) 
				{
					if (BlockUnder.getType().equals(Material.AIR) || BlockUnder.getType().equals(Material.WATER))
					{
						count++;
					}
					else
					{
						can = false; 
					}
				}
			}
		}
	    		
		toloc = myloc.clone().subtract(0, count, 0);
		setFlag();
	}
	
	public void checkNear(List<ArenaPlayer> arenaplayers)
	{
		if (isStopped())
			return;
		
		if (!isPickedUp()) 
		{
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				ArenaPlayer apl = arenaplayers.get(i);
				Player pl = apl.getPlayer();
				if (pl != null)
				{
					if (Util.pointDistance(pl.getLocation(), myloc) < 1.75 && pl.getHealth() > 0)
					{
						if (! apl.isOut())
						{
							if (apl.getTeam() != getTeam()) 
							{
								// If the guy is on the other team
								setPickedUp(true);
								setRiding(pl);
								pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * (60 * 4), 1));
								pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * (60 * 4), 1));
								arena.tellPlayers("&b{0} &7picked up the &6{1} &7flag!", apl.getUsername(), getFlagType());
								return;
							}
							else
							{
								if (!myloc.equals(getReturnto())) 
								{ 
									// If the flag is not at its flagstand
									apl.sendMessage("&aFlag Returned! &c+50 XP");
									arenaplayers.get(i).setGameXP(arenaplayers.get(i).getGameXP() + 50);
									arena.tellPlayers("&b{0} &7returned the &6{1} &7flag!", pl.getName(), getFlagType());
									respawn();
									return;
								}
							}
						}
					}
				}
			}
		}
		if (isPickedUp())
		{
			if (getRiding().isOnline() && !getRiding().isDead()) 
			{
				//if player is alive
				toloc = getRiding().getLocation().clone().add(0, 3, 0);
			}
			else
			{
				fall();
			}
			
			myloc = toloc.clone();
			setFlag();
		}
	}
	
	public void despawn() 
	{
		setStopped(true);
		
		Block last = lastloc.getBlock();
		last.setTypeIdAndData(lastBlockType, lastBlockDat, false);
	}
	
	public void tick()
	{
		if (isStopped())
			return;
		
		if (!isPickedUp()) 
		{
			if (!myloc.equals(getReturnto())) 
			{
				//if the flag is not at its flagstand
				timer--;
				if (timer <= 0) 
				{
					respawn();
					
					arena.tellPlayers("&7The {0} &7flag has respawned!", getFlagType());
				}
				else
				{
					notifyTime();
				}
			}
		}
	}
	
	public void setFlag() 
	{
		if (isStopped())
			return;
		
		Block last = lastloc.getBlock();
		Block current = myloc.getBlock();

    	if (! Util.checkLocation(lastloc, myloc))
    	{
	    	last.setTypeIdAndData(lastBlockType, lastBlockDat, true);
	    	lastBlockDat = current.getData();
			lastBlockType = current.getTypeId();
			lastloc = myloc.clone();
	    	//last.setTypeIdAndData(0, (byte) 0, true);
			setFlagBlock(current);
    	}
	}
	
	private void setFlagBlock(Block c) 
	{
		if (color == 11)
			c.setTypeIdAndData(Material.LAPIS_BLOCK.getId(), (byte)0, true);
		if (color == 14)
			c.setTypeIdAndData(Material.NETHERRACK.getId(), (byte)0, true);
	}

	public int getTeam() 
	{
		return team;
	}

	public void setTeam(int team) 
	{
		this.team = team;
	}

	public boolean isPickedUp()
	{
		return pickedUp;
	}

	public void setPickedUp(boolean pickedUp)
	{
		this.pickedUp = pickedUp;
	}

	public Player getRiding()
	{
		return riding;
	}

	public void setRiding(Player riding) 
	{
		this.riding = riding;
	}

	public String getFlagType() 
	{
		return flagType;
	}

	public void setFlagType(String flagType)
	{
		this.flagType = flagType;
	}

	public boolean isStopped()
	{
		return stopped;
	}

	public void setStopped(boolean stopped) 
	{
		this.stopped = stopped;
	}

	public Location getReturnto()
	{
		return returnto;
	}

	public void setReturnto(Location returnto) 
	{
		this.returnto = returnto;
	}
}