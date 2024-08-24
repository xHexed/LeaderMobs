package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.magmaguy.elitemobs.api.EliteMobSpawnEvent;
import com.magmaguy.elitemobs.entitytracker.EntityTracker;
import com.magmaguy.elitemobs.mobconstructor.EliteEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EliteMobsListener extends CustomMobListener implements Listener {
    public EliteMobsListener(LeaderMobs plugin) {
        super(plugin, "EliteMobs");
    }

    @Override
    public String getMobName(Entity entity) {
        EliteEntity eliteEntity = EntityTracker.getEliteMobEntity(entity);
        if (eliteEntity == null) return "";
        return eliteEntity.getName();
    }

    @Override
    public boolean isMob(Entity entity) {
        return EntityTracker.isEliteMob(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EliteMobSpawnEvent event) {
        handleMobSpawn(event.getEntity(), event.getEliteMobEntity().getName(), event.getEntity().getCustomName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        handleMobDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EliteMobDeathEvent event) {
        handleMobDeath(event.getEntity(), event.getEliteEntity().getName(), event.getEntity().getCustomName());
    }
}
