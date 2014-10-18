/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.mob;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.types.ArenaConfig;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.ultimatearena.types.KillStreak;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
public class MobConfig extends ArenaConfig
{
	private int maxWave;

	public MobConfig(UltimateArena plugin, String type, File file)
	{
		super(plugin, type, file);
	}

	public MobConfig(ArenaZone az)
	{
		super(az);
	}

	@Override
	protected void setCustomDefaults()
	{
		this.countMobKills = true;
		this.maxWave = 15;
	}

	@Override
	public void loadCustomOptions(YamlConfiguration fc, ArenaConfig def)
	{
		this.maxWave = fc.getInt("maxWave", ((MobConfig) def).getMaxWave());
	}

	@Override
	public Map<Integer, List<KillStreak>> getDefaultKillStreak()
	{
		Map<Integer, List<KillStreak>> ret = new LinkedHashMap<>();

		ret.put(8, Arrays.asList(new KillStreak[] {
				new KillStreak(8, "&e8 &3kills! Unlocked strength potion!", ItemUtil.readPotion("strength, 1, 1, false"))
		}));

		ret.put(12, Arrays.asList(new KillStreak[] {
				new KillStreak(12, "&e12 &3kills! Unlocked swiftness potion!", ItemUtil.readPotion("strength, 1, 1, false"))
		}));

		ret.put(16, Arrays.asList(new KillStreak[] {
				new KillStreak(16, "&e16 &3kills! Unlocked Anti-Fire!", ItemUtil.readPotion("fireres, 1, 1, false"))
		}));

		ret.put(24, Arrays.asList(new KillStreak[] {
				new KillStreak(24, "&e24 &3kills! Unlocked health potion!", ItemUtil.readPotion("heal, 1, 1, false")),
				new KillStreak(24, "&e24 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		ret.put(32, Arrays.asList(new KillStreak[] {
				new KillStreak(24, "&e32 &3kills! Unlocked attack dogs!", EntityType.WOLF, 3)
		}));

		ret.put(40, Arrays.asList(new KillStreak[] {
				new KillStreak(40, "&e40 &3kills! Unlocked regen potion!", ItemUtil.readPotion("regen, 1, 1, false")),
				new KillStreak(40, "&e40 &3kills! Unlocked food!", new ItemStack(Material.GRILLED_PORK, 2))
		}));

		ret.put(72, Arrays.asList(new KillStreak[] {
				new KillStreak(72, "&e72 &3kills! Unlocked Golden Apples!", new ItemStack(Material.GOLDEN_APPLE, 2))
		}));

		ret.put(112, Arrays.asList(new KillStreak[] {
				new KillStreak(112, "&e112 &3kills! Unlocked Golden Apples!", new ItemStack(Material.GOLDEN_APPLE, 2))
		}));

		return ret;
	}

	@Override
	public void serializeCustomOptions(Map<String, Object> data)
	{
		data.put("maxWave", maxWave);
	}
}