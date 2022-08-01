package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.magmaguy.elitemobs.api.EliteMobSpawnEvent;
import com.magmaguy.elitemobs.entitytracker.EntityTracker;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EliteMobsListener extends CustomMobListener implements Listener {
    public EliteMobsListener(LeaderMobs plugin) {
        super(plugin, "EliteMobs");
    }

    @Override
    public String getMobName(Entity entity) {
        return entity.getName();
    }

    @Override
    public boolean isMob(Entity entity) {
        return EntityTracker.getEliteMobEntity(entity) != null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EliteMobSpawnEvent event) {
        handleMobSpawn(event.getEntity(), event.getEntity().getName(), event.getEntity().getCustomName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        handleMobDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EliteMobDeathEvent event) {
        handleMobSpawn(event.getEntity(), event.getEntity().getName(), event.getEntity().getCustomName());
    }
}
