package com.orange451.UltimateArena;

import org.bukkit.entity.Player;

public class testAPI 
{
	public UltimateArenaAPI ua;
	
	public void test(Player p)
	{
		/**Returns whether or not a player is IN an arena**/
		UltimateArenaAPI ua = UltimateArenaAPI.hookIntoUA();
		if (ua != null) 
		{
			boolean isPlayerPlayingArena = ua.isPlayerPlayingArena(p);
			boolean isPlayerInsideArena = ua.isPlayerInArenaLocation(p);
			System.out.println("Player is playing arena? " + isPlayerPlayingArena);
			System.out.println("Player is inside arena? " + isPlayerInsideArena);
		}
		else
		{
			System.out.println("Please hook into the UA API");
		}
	}
}