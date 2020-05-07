package com.github.xhexed.leadermobs.listeners;

import com.github.xhexed.leadermobs.handler.MobHandler;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;

public class LegacyMythicMobsListener implements Listener {
    private final FileConfiguration config = getInstance().getConfig();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final MythicMobSpawnEvent event) {
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.MythicMobs").contains(event.getMobType().getInternalName())) return;
        MobHandler.onMobSpawn(event.getEntity(), event.getMobType().getDisplayName().get());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        MythicMobsListener.handleDamageEvent(event, config);
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(ignoreCancelled = true)
    public void onDeath(final MythicMobDeathEvent event) {
        final MythicMob mobs = event.getMobType();
        MobHandler.onMobDeath(event.getEntity(), event.getMob().getDisplayName(), mobs.getInternalName(), (Double) (Object) mobs.getHealth());
    }
}
