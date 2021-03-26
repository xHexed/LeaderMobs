package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MythicMobsListener extends CustomMobListener implements Listener {
    public MythicMobsListener(LeaderMobs plugin) {
        super(plugin, "MythicMobs");
    }

    @Override
    public String getMobName(Entity entity) {
        return MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getType().getInternalName();
    }

    @Override
    public boolean isMob(Entity entity) {
        return MythicMobs.inst().getAPIHelper().isMythicMob(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(MythicMobSpawnEvent event) {
        handleMobSpawn(event.getEntity(), event.getMobType().getInternalName(), event.getMob().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        handleMobDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(MythicMobDeathEvent event) {
        handleMobDeath(event.getEntity(), event.getMob().getDisplayName(), event.getMobType().getInternalName());
    }
}
