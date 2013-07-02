package net.dmulloy2.ultimatearena.arenas.objects;

public class ArenaReward 
{
	public int amt;
	public byte data;
	public int type;
	
	public ArenaReward(int id, byte dat, int amt) 
	{
		this.type = id;
		this.data = dat;
		this.amt = amt;
	}
}