package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.magmaguy.elitemobs.api.EliteMobSpawnEvent;
import com.magmaguy.elitemobs.entitytracker.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EliteMobsListener extends CustomMobListener implements Listener {
    public EliteMobsListener(LeaderMobs plugin) {
        super(plugin, "EliteMobs");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean isMob(Entity entity) {
        return EntityTracker.isEliteMob(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EliteMobSpawnEvent event) {
        if (event.getEntity() == null) return;
        handleMobSpawn(event.getEntity(), event.getEliteMobEntity().getName(), event.getEliteMobEntity().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        handleDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EliteMobDeathEvent event) {
        handleMobDeath(event.getEntity(), event.getEliteEntity().getName(), event.getEliteEntity().getName());
    }
}
