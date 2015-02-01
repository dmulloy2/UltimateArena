/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.arenas.spleef;

import java.io.File;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.api.ArenaType;
import net.dmulloy2.ultimatearena.types.ArenaZone;
import net.dmulloy2.util.MaterialUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author dmulloy2
 */

@Getter @Setter
public class SpleefZone extends ArenaZone
{
	private String specialTypeString;
	private Material specialType;

	public SpleefZone(ArenaType type)
	{
		super(type);
	}

	public SpleefZone(ArenaType type, File file)
	{
		super(type, file);
	}

	@Deprecated
	public SpleefZone(UltimateArena plugin, File file)
	{
		super(plugin, file);
	}

	public final void setSpecialType(Material material)
	{
		this.specialType = material;
		this.specialTypeString = material.name();
	}

	@Override
	public void loadCustomOptions(FileConfiguration fc)
	{
		this.specialTypeString = fc.getString("specialTypeString");
		this.specialType = MaterialUtil.getMaterial(specialTypeString);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = super.serialize();
		ret.put("specialTypeString", specialTypeString);
		return ret;
	}
}