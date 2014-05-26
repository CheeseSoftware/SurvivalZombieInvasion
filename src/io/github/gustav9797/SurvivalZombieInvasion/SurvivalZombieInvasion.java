package io.github.gustav9797.SurvivalZombieInvasion;

import io.github.gustav9797.SurvivalZombieInvasion.Entity.EntityBlockBreakingSkeleton;
import io.github.gustav9797.SurvivalZombieInvasion.Entity.EntityBlockBreakingVillager;
import io.github.gustav9797.SurvivalZombieInvasion.Entity.EntityBlockBreakingZombie;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_7_R2.BiomeBase;
import net.minecraft.server.v1_7_R2.BiomeMeta;
import net.minecraft.server.v1_7_R2.EntityVillager;
import net.minecraft.server.v1_7_R2.EntityZombie;
import net.minecraft.server.v1_7_R2.EntitySkeleton;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ostkaka34.OstEconomyPlugin.IOstEconomy;
import ostkaka34.OstEconomyPlugin.OstEconomyPlugin;

public final class SurvivalZombieInvasion extends JavaPlugin implements Listener
{
	LinkedList<CustomEntityType> entityTypes;
	Random r = new Random();
	File configFile;

	public static IOstEconomy economyPlugin;

	@Override
	public void onEnable()
	{
		this.configFile = new File(this.getDataFolder() + File.separator + "config.yml");
		
		this.entityTypes = new LinkedList<CustomEntityType>();
		this.entityTypes.add(new CustomEntityType("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, EntityBlockBreakingZombie.class));
		this.entityTypes.add(new CustomEntityType("Skeleton", 51, EntityType.SKELETON, EntitySkeleton.class, EntityBlockBreakingSkeleton.class));
		this.entityTypes.add(new CustomEntityType("Villager ", 120, EntityType.VILLAGER, EntityVillager.class, EntityBlockBreakingVillager.class));

		this.registerEntities();
		
		SurvivalZombieInvasion.economyPlugin = (IOstEconomy) Bukkit.getPluginManager().getPlugin("OstEconomyPlugin");
		if(SurvivalZombieInvasion.economyPlugin == null)
			this.getServer().getLogger().severe("Could not load economy!");

		if (!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			this.SaveConfig();
		}
		this.Load();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable()
	{
		this.saveDefaultConfig();
		this.saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			//Player player = (Player) sender;

		}
		return false;
	}

	public void Save()
	{
		this.SaveConfig();
	}

	public void Load()
	{
		this.LoadConfig();
	}

	public void SaveConfig()
	{
		/*YamlConfiguration config = new YamlConfiguration();
		List<String> temp = new LinkedList<String>();
		try
		{
			config.save(configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/
	}

	public void LoadConfig()
	{
		/*YamlConfiguration config = new YamlConfiguration();
		try
		{
			config.load(configFile);
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}*/
	}

	public void registerEntities()
	{
		BiomeBase[] biomes;
		try
		{
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
		}
		catch (Exception exc)
		{
			return;
		}
		for (BiomeBase biomeBase : biomes)
		{
			if (biomeBase == null)
				break;
			for (String field : new String[]
			{ "as", "at", "au", "av" })
				try
				{
					Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					for (BiomeMeta meta : mobList)
						for (CustomEntityType entity : entityTypes)
							if (entity.getNMSClass().equals(meta.b))
								meta.b = entity.getCustomClass();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}
	}

	public void Reload()
	{
		this.Load();
	}

	public static SurvivalZombieInvasion getPlugin()
	{
		return (SurvivalZombieInvasion) Bukkit.getPluginManager().getPlugin("ZombieInvasion");
	}

	public static OstEconomyPlugin getEconomyPlugin()
	{
		return (OstEconomyPlugin) Bukkit.getPluginManager().getPlugin("OstEconomyPlugin");
	}

	public static JavaPlugin getWeaponsPlugin()
	{
		return (JavaPlugin) Bukkit.getPluginManager().getPlugin("WeaponsPlugin");
	}

	@SuppressWarnings("rawtypes")
	private static Object getPrivateStatic(Class clazz, String f) throws Exception
	{
		Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		return field.get(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onCreatureSpawn(CreatureSpawnEvent event)
	{
		/*if(event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		else if (event.getSpawnReason() == SpawnReason.BUILD_SNOWMAN)
			return;
		
		if (event.getSpawnReason() == SpawnReason.SPAWNER_EGG)
		{
			event.setCancelled(true);
			EntityCreature monster = null;
			net.minecraft.server.v1_7_R2.World mcWorld = ((CraftWorld) event.getLocation().getWorld()).getHandle();

			switch (event.getEntityType())
			{
				case SKELETON:
					monster = new EntityBlockBreakingSkeleton(mcWorld);
					break;
				case ZOMBIE:
					monster = new EntityBlockBreakingZombie(mcWorld);
					break;
				case VILLAGER:
					monster = new EntityBlockBreakingVillager(mcWorld);
					break;
				default:
					break;
			}

			if (monster != null)
			{
				monster.getBukkitEntity().teleport(event.getLocation());
				((ICustomMonster)monster).setArena(null);
				mcWorld.addEntity(monster, SpawnReason.CUSTOM);
			}
		}
		else
		{
			boolean monsterIsPartOfAnyArena = false;
			for (Arena a : this.arenas.values())
			{
				if (a instanceof ZombieArena)
				{
					ZombieArena arena = (ZombieArena) a;
					if (arena.monsters.containsKey(event.getEntity().getUniqueId()))
					{
						monsterIsPartOfAnyArena = true;
						if (!a.ContainsLocation(event.getLocation()))
							event.setCancelled(true);
						break;
					}
				}
			}
			if (!monsterIsPartOfAnyArena)
				event.setCancelled(true);
		}*/
	}

}
