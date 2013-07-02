package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.IEssentials;
import net.dmulloy2.ultimatearena.UltimateArena;

@Deprecated
public class OldArenaClass 
{
	public String name;
	public String permissionNode = "";
	
	public int armor1 = 0;
	public int armor2 = 0;
	public int armor3 = 0;
	
	public List<CompositeEnchantment> armorenchant1 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> armorenchant2 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> armorenchant3 = new ArrayList<CompositeEnchantment>();
	
	public int weapon1 = 0;
	public int weapon2 = 0;
	public int weapon3 = 0;
	public int weapon4 = 0;
	public int weapon5 = 0;
	public int weapon6 = 0;
	public int weapon7 = 0;
	public int weapon8 = 0;
	public int weapon9 = 0;
	
	public int amt1 = 1;
	public int amt2 = 1;
	public int amt3 = 1;
	public int amt4 = 1;
	public int amt5 = 1;
	public int amt6 = 1;
	public int amt7 = 1;
	public int amt8 = 1;
	public int amt9 = 1;
	
	public byte special1 = 0;
	public byte special2 = 0;
	public byte special3 = 0;
	public byte special4 = 0;
	public byte special5 = 0;
	public byte special6 = 0;
	public byte special7 = 0;
	public byte special8 = 0;
	public byte special9 = 0;
	
	public List<CompositeEnchantment> enchant1 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant2 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant3 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant4 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant5 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant6 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant7 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant8 = new ArrayList<CompositeEnchantment>();
	public List<CompositeEnchantment> enchant9 = new ArrayList<CompositeEnchantment>();
	
	public boolean loaded = true;
	public boolean helmet = true;
	
	public boolean useEssentials = false;
	public String essKitName = "";
	public Map<String, Object> essentialsKit;
	
	public UltimateArena plugin;
	public File file;
	
	public OldArenaClass(UltimateArena plugin, File file)
	{
		this.plugin = plugin;
		this.file = file;
		this.load();
	}
	
	public void load()
	{
		name = file.getName();
		ArrayList<String> file = new ArrayList<String>();
	    try
	    {
			FileInputStream fstream = new FileInputStream(this.file.getAbsolutePath());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) 
			{
				file.add(strLine);
			}
			br.close();
			in.close();
			fstream.close();
        }
	    catch (Exception e)
	    {
            plugin.getLogger().severe("Error: " + e.getMessage());
        }
	    
	    for (int i= 0; i < file.size(); i++)
	    {
	    	computeData(file.get(i));
	    }
	}
	
	public int readWep(String str) 
	{
		int ret = 0;
		if (str.contains(","))
		{
			str = str.substring(0, str.indexOf(","));
		}
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":"))
		{
			str = str.substring(0, str.indexOf(":"));
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int readSpec(String str)
	{
		int ret = 0;
		if (str.contains(",")) 
		{
			str = str.substring(0, str.indexOf(","));
		}
		try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		if (str.contains(":")) 
		{
			str = str.substring(str.indexOf(":") + 1);
			ret = Integer.parseInt(str);
		}
		return ret;
	}
	
	public int readAmt(String str) 
	{
		int ret = 1;
		if (str.contains(",")) 
		{
			str = str.substring(str.indexOf(",") + 1);
			try{ ret = Integer.parseInt(str); }catch(Exception e) { }
		}
		return ret;
	}
	
	public List<CompositeEnchantment> readArmorEnchantments(String string)
	{
		List<CompositeEnchantment> enchants = new ArrayList<CompositeEnchantment>();
		if (string.contains(":"))
		{
			String[] split2 = string.split(":");
					
			Enchantment enchantment = null;
			try { enchantment = EnchantmentType.toEnchantment(split2[0]); }
			catch (Exception e) { enchantment = Enchantment.getByName(split2[0].toUpperCase()); }
					
			int level = 0;
			try { level = Integer.parseInt(split2[1]); }
			catch (Exception e) {}
					
			if (enchantment != null && level > 0)
			{
				enchants.add(new CompositeEnchantment(enchantment, level));
			}
		}
		return enchants;
	}
	
	public List<CompositeEnchantment> readToolEnchantments(String str)
	{
		List<CompositeEnchantment> enchants = new ArrayList<CompositeEnchantment>();
		if (str.contains(","))
		{
			String[] split = str.split(",");
			if (split.length > 2)
			{
				for (int i=2; i<split.length; i++)
				{
					String s = split[i];
					if (s.contains(":"))
					{
						String[] split2 = s.split(":");
						Enchantment enchantment = null;
						try { enchantment = EnchantmentType.toEnchantment(split2[0]); }
						catch (Exception e) { enchantment = Enchantment.getByName(split2[0].toUpperCase()); }
						
						int level = 0;
						try { level = Integer.parseInt(split2[1]); }
						catch (Exception e) {}
						
						if (enchantment != null && level > 0)
						{
							enchants.add(new CompositeEnchantment(enchantment, level));
						}
					}
				}
			}
		}
		return enchants;
	}
	
	public void computeData(String str) 
	{
		try
		{
			if (str.indexOf("=") >=1 ) 
			{
				String str2 = str.substring(0, str.indexOf("="));
				if (str2.equalsIgnoreCase("chestplate")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					String[] split = line.split(",");
					
					if (split.length > 1)
					{
						armor1 = Integer.parseInt(split[0]);
						armorenchant1 = readArmorEnchantments(split[1]);
					}
					else
					{
						armor1 = Integer.parseInt(line);
					}
				}
				if (str2.equalsIgnoreCase("leggings")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					String[] split = line.split(",");
					
					if (split.length > 1)
					{
						armor2 = Integer.parseInt(split[0]);
						armorenchant2 = readArmorEnchantments(split[1]);
					}
					else
					{
						armor2 = Integer.parseInt(line);
					}
				}
				if (str2.equalsIgnoreCase("boots"))
				{
					String line = str.substring(str.indexOf("=")+1);
					String[] split = line.split(",");
					
					if (split.length > 1)
					{
						armor3 = Integer.parseInt(split[0]);
						armorenchant3 = readArmorEnchantments(split[1]);
					}
					else
					{
						armor3 = Integer.parseInt(line);
					}
				}
				if (str2.equalsIgnoreCase("node"))
				{
					String line = str.substring(str.indexOf("=")+1);
					permissionNode = line;
				}
				if (str2.equalsIgnoreCase("tool1"))
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon1 = value;
					special1 = (byte) value2;
					amt1 = value3;
					enchant1 = value4;
				}
				if (str2.equalsIgnoreCase("tool2"))
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon2 = value;
					special2 = (byte) value2;
					amt2 = value3;
					enchant2 = value4;
				}
				if (str2.equalsIgnoreCase("tool3")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon3 = value;
					special3 = (byte) value2;
					amt3 = value3;
					enchant3 = value4;
				}
				if (str2.equalsIgnoreCase("tool4")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon4 = value;
					special4 = (byte) value2;
					amt4 = value3;
					enchant4 = value4;
				}
				if (str2.equalsIgnoreCase("tool5")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon5 = value;
					special5 = (byte) value2;
					amt5 = value3;
					enchant5 = value4;
				}
				if (str2.equalsIgnoreCase("tool6")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon6 = value;
					special6 = (byte) value2;
					amt6 = value3;
					enchant6 = value4;
				}
				if (str2.equalsIgnoreCase("tool7"))
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon7 = value;
					special7 = (byte) value2;
					amt7 = value3;
					enchant7 = value4;
				}
				if (str2.equalsIgnoreCase("tool8"))
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon8 = value;
					special8 = (byte) value2;
					amt8 = value3;
					enchant8 = value4;
				}
				if (str2.equalsIgnoreCase("tool9")) 
				{
					String line = str.substring(str.indexOf("=")+1);
					int value = readWep(line);
					int value2 = readSpec(line);
					int value3 = readAmt(line);
					List<CompositeEnchantment> value4 = readToolEnchantments(line);
					weapon9 = value;
					special9 = (byte) value2;
					amt9 = value3;
					enchant9 = value4;
				}
				if (str2.equalsIgnoreCase("helmet")) 
				{
					String line = str.substring(str.indexOf("=") + 1);
					if (line.equalsIgnoreCase("false"))
					{
						helmet = false;
					}
				}
				if (str2.equalsIgnoreCase("useEssentials"))
				{
					String line = str.substring(str.indexOf("=") + 1);
					if (line.equalsIgnoreCase("true"))
					{
						useEssentials = true;
					}
				}
				if (str2.equalsIgnoreCase("essentialsKit"))
				{
					String line = str.substring(str.indexOf("=") + 1);
					// Initialize Essentials Hook
					PluginManager pm = plugin.getServer().getPluginManager();
					if (pm.isPluginEnabled("Essentials"))
					{
						Plugin essPlugin = pm.getPlugin("Essentials");
						IEssentials ess = (IEssentials) essPlugin;
						Map<String, Object> kit = ess.getSettings().getKit(line);
						if (kit != null)
						{
							essentialsKit = kit;
						}
					}
					essKitName = line;
				}
			}
		}
		catch(Exception e)
		{
			//problem loading the class
			loaded = false;
		}
	}

	public boolean checkPermission(Player player)
	{
		if (permissionNode.equals(""))
			return true;
			
		return plugin.getPermissionHandler().hasPermission(player, permissionNode);
	}	
}