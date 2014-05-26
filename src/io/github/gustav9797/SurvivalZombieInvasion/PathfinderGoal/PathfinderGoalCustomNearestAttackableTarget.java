package io.github.gustav9797.SurvivalZombieInvasion.PathfinderGoal;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R2.EntityCreature;
import net.minecraft.server.v1_7_R2.EntityLiving;

public class PathfinderGoalCustomNearestAttackableTarget extends PathfinderGoalCustomTarget
{

	private final int attackCheckFrequency;
	private EntityLiving target;
	private Random r = new Random();

	public PathfinderGoalCustomNearestAttackableTarget(EntityCreature entitycreature, int attackCheckFrequency)
	{
		super(entitycreature);
		this.attackCheckFrequency = attackCheckFrequency;
		this.a(1);
	}

	public boolean a() // canExecute
	{
		if (this.attackCheckFrequency > 0 && r.nextInt(this.attackCheckFrequency) != 0)
		{
			return false;
		}
		else if (this.entity.getGoalTarget() == null)
		//else if(this.entity.getGoalTarget() == null)
		{
			// this.target = (EntityLiving) list.get(0);
			//Player[] players = Bukkit.getServer().getOnlinePlayers();
			Player closestPlayer = null;
			double closestPlayerDistance = Double.MAX_VALUE;
			
			for(Player player : Bukkit.getOnlinePlayers())
			{
				double distance = player.getLocation().distance(this.entity.getBukkitEntity().getLocation());
				if(distance < closestPlayerDistance && player.getGameMode() == GameMode.SURVIVAL)
				{
					closestPlayerDistance = distance;
					closestPlayer = player;
				}
			}
			
			if(closestPlayer != null)
			{
				this.target= ((CraftPlayer)closestPlayer).getHandle();
				this.entity.setGoalTarget(target);
				//this.entity.target = this.target;
				return true;
			}
		}
		return false;
	}

	public void c() // setup
	{
		this.entity.setGoalTarget(this.target);
		super.c();
	}
}