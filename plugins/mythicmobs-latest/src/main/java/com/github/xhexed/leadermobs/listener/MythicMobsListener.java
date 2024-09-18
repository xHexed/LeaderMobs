package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MythicMobsListener extends CustomMobListener implements Listener {
    public MythicMobsListener(LeaderMobs plugin) {
        super(plugin, "MythicMobs");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean isMob(Entity entity) {
        return MythicBukkit.inst().getAPIHelper().isMythicMob(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(MythicMobSpawnEvent event) {
        handleMobSpawn(event.getEntity(), event.getMobType().getInternalName(), event.getMob().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        handleDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(MythicMobDeathEvent event) {
        handleMobDeath(event.getEntity(), event.getMobType().getInternalName(), event.getMob().getDisplayName());
    }
}
