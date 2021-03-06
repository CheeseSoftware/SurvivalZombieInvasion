package io.github.gustav9797.SurvivalZombieInvasion.Entity;

import io.github.gustav9797.SurvivalZombieInvasion.PathfinderGoal.PathfinderGoalBreakBlock;
import io.github.gustav9797.SurvivalZombieInvasion.PathfinderGoal.PathfinderGoalCustomMeleeAttack;
import io.github.gustav9797.SurvivalZombieInvasion.PathfinderGoal.PathfinderGoalCustomNearestAttackableTarget;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.server.v1_7_R3.AttributeInstance;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EntityZombie;
import net.minecraft.server.v1_7_R3.Navigation;
import net.minecraft.server.v1_7_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_7_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_7_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_7_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_7_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_7_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.World;

import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;

public class EntityBlockBreakingZombie extends EntityZombie implements ICustomMonster
{
	private Random r = new Random();

	public EntityBlockBreakingZombie(World world)
	{
		super(world);

		try
		{
			Field field = Navigation.class.getDeclaredField("e");
			field.setAccessible(true);
			AttributeInstance e = (AttributeInstance) field.get(this.getNavigation());
			e.setValue(128); // Navigation distance in block lengths goes here
		}
		catch (Exception ex)
		{
		}

		try
		{
			Field gsa = PathfinderGoalSelector.class.getDeclaredField("b");
			gsa.setAccessible(true);
			gsa.set(this.goalSelector, new UnsafeList<Object>());
			gsa.set(this.targetSelector, new UnsafeList<Object>());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		this.getNavigation().b(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		//this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
		//this.targetSelector.a(1, new PathfinderGoalBreakBlock(this, false));
		this.targetSelector.a(1, new PathfinderGoalCustomNearestAttackableTarget(this, 0));
		this.a(0.6F, 1.8F);

	}

	@Override
	protected Entity findTarget()
	{
		EntityHuman entityhuman = this.findNearbyVulnerablePlayer(128, 128, 128);

		return entityhuman;
	}

	public EntityHuman findNearbyVulnerablePlayer(double d0, double d1, double d2)
	{
		if (world.players.size() > 0)
		{
			int i = r.nextInt(world.players.size());
			EntityHuman entityhuman1 = (EntityHuman) this.world.players.get(i);

			if (!entityhuman1.abilities.isInvulnerable && entityhuman1.isAlive())
			{
				return entityhuman1;
			}
		}
		return null;
	}
}
