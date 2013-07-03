package net.dmulloy2.ultimatearena.arenas.objects;

public class ArenaReward 
{
	private int amt;
	private byte data;
	private int type;
	
	public ArenaReward(int id, byte dat, int amt) 
	{
		this.type = id;
		this.data = dat;
		this.amt = amt;
	}
	
	public int getType()
	{
		return type;
	}
	
	public byte getData()
	{
		return data;
	}
	
	public int getAmount()
	{
		return amt;
	}
}