package net.dmulloy2.ultimatearena.arenas.objects;

public enum FieldType 
{
	BOMB("bomb"),
	CONQUEST("cq"),
	CTF("ctf"),
	FFA("ffa"),
	HUNGER("hunger"),
	INFECT("infect"),
	KOTH("koth"),
	MOB("mob"),
	PVP("pvp"),
	SPLEEF("spleef");
	
	private String name;
	FieldType(String name)
	{
		this.name= name;
	}
	
	public static FieldType getByName(String string)
	{
		for (FieldType type : FieldType.values())
		{
			if (type.name.equalsIgnoreCase(string))
				return type;
		}
		
		return null;
	}
	
	public static String getName(FieldType type)
	{
		return type.name;
	}
	
	public static boolean contains(String type)
	{
		return (getByName(type) != null);
	}
}