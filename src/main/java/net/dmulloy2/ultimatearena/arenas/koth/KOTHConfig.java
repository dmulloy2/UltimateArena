/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.koth;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
public class KOTHConfig extends ArenaConfig
{
	private int maxPoints;

	public KOTHConfig(@NonNull UltimateArena plugin, @NonNull String type, @NonNull File file)
	{
		super(plugin, type, file);
	}

	public KOTHConfig(@NonNull ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void initializeVariables()
	{
		super.initializeVariables();
		this.maxPoints = 60;
	}

	@Override
	public void loadCustomOptions(YamlConfiguration fc, ArenaConfig def)
	{
		this.maxPoints = fc.getInt("maxPoints", ((KOTHConfig) def).getMaxPoints());
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		Map<Integer, List<KillStreak>> ret = new LinkedHashMap<>();

		ret.put(2, Arrays.asList(new KillStreak[] {
				new KillStreak(2, "&e2 &3kills! Unlocked strength potion!", ItemUtil.readPotion("strength, 1, 1, false"))
		}));

		ret.put(4, Arrays.asList(new KillStreak[] {
				new KillStreak(4, "&e4 &3kills! Unlocked health potion!", ItemUtil.readPotion("heal, 1, 1, false")),
				new KillStreak(4, "&e4 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		ret.put(12, Arrays.asList(new KillStreak[] {
				new KillStreak(12, "&e12 &3kills! Unlocked regen potion!", ItemUtil.readPotion("regen, 1, 1, false")),
				new KillStreak(12, "&e12 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		return ret;
	}
}