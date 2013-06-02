package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.enchantments.Enchantment;

public class CompositeEnchantment
{
	private final Enchantment enchantment;
	private final int level;
	
	public CompositeEnchantment(final Enchantment enchantment, final int level)
	{
		this.enchantment = enchantment;
		this.level = level;
	}
	
	public Enchantment getType()
	{
		return enchantment;
	}
	
	public int getLevel()
	{
		return level;
	}
}