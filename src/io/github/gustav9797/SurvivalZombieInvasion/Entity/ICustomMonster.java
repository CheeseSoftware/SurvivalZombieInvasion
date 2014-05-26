package io.github.gustav9797.SurvivalZombieInvasion.Entity;

import net.minecraft.server.v1_7_R2.EntityHuman;

public interface ICustomMonster
{
	public EntityHuman findNearbyVulnerablePlayer(double d0, double d1, double d2);
}
