package com.github.xhexed.leadermobs.listeners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;

public class MythicMobsListener implements Listener {
    private static final BukkitAPIHelper helper = MythicMobs.inst().getAPIHelper();
    private final FileConfiguration config = getInstance().getConfig();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final MythicMobSpawnEvent event) {
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.MythicMobs").contains(event.getMobType().getInternalName())) return;
        final Location loc = event.getLocation();
        MobListener.onMobSpawn(event.getMob().getDisplayName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (!helper.isMythicMob(entity)) return;
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.MythicMobs").contains(helper.getMythicMobInstance(entity).getType().getInternalName())) return;
        final Entity damager = event.getDamager();
        if (damager.hasMetadata("NPC") || !(damager instanceof Player)) return;
        MobListener.onMobDamage((Player) damager, entity, event.getFinalDamage());
        getInstance().debug("Damage for boss: " + ChatColor.stripColor(entity.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + damager.getName());
        getInstance().debug("Data: " + MobListener.data);
    }
    
    @EventHandler
    public void onDeath(final MythicMobDeathEvent e) {
        final ActiveMob mob = e.getMob();
        MobListener.onMobDeath(e.getEntity(), mob.getDisplayName(), e.getMobType().getInternalName(), e.getMobType().getHealth().get());
    }
}
