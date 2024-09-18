package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LegacyMythicMobsListener extends CustomMobListener implements Listener {
    public LegacyMythicMobsListener(LeaderMobs plugin) {
        super(plugin, "MythicMobs");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean isMob(Entity entity) {
        return MythicMobs.inst().getAPIHelper().isMythicMob(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final MythicMobSpawnEvent event) {
        handleMobSpawn(event.getEntity(), event.getMobType().getInternalName(),event.getMobType().getDisplayName().get());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        handleDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(final MythicMobDeathEvent event) {
        handleMobDeath(event.getEntity(), event.getMobType().getInternalName(),event.getMobType().getDisplayName().get());
    }
}
