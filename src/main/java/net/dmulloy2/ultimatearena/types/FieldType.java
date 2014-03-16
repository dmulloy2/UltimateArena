package net.dmulloy2.ultimatearena.types;

import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
public enum FieldType
{
	BOMB("bomb", "Bomb"), 
	CONQUEST("cq", "CQ"), 
	CTF("ctf", "CTF"),
	FFA("ffa", "FFA"),
	HUNGER("hunger", "Hunger"), 
	INFECT("infect", "Infect"), 
	KOTH("koth", "KOTH"),
	MOB("mob", "Mob"),
	PVP("pvp", "PvP"), 
	SPLEEF("spleef", "Spleef");

	private String name;
	private String stylized;
	private FieldType(String name, String stylized)
	{
		this.name = name;
		this.stylized = stylized;
	}

	public static FieldType getByName(String string)
	{
		for (FieldType type : FieldType.values())
		{
			if (type.getName().equalsIgnoreCase(string))
				return type;
		}

		return null;
	}

	public static boolean contains(String type)
	{
		return getByName(type) != null;
	}
}