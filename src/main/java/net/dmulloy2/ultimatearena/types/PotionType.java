package net.dmulloy2.ultimatearena.types;

/**
 * @author dmulloy2
 */

public enum PotionType 
{
	FIRE_RESISTANCE("fireres"),
	INSTANT_DAMAGE("damage"),
	INSTANT_HEAL("heal"),
	INVISIBILITY("invis"),
	NIGHT_VISION("nvg"),
	POISON("poison"),
	REGEN("regen"),
	SLOWNESS("slow"),
	SPEED("speed"),
	STRENGTH("strength"),
	WATER("water"),
	WEAKNESS("weak");
	
	public String name;
	PotionType(String name) 
	{
		this.name = name;
	}
	
	public static org.bukkit.potion.PotionType toType(String string)
	{
		for (PotionType type : PotionType.values())
		{
			if (type.name.equalsIgnoreCase(string))
				return org.bukkit.potion.PotionType.valueOf(type.toString());
		}
		
		return null;
	}
}