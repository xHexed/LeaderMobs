package com.github.xhexed.leadermobs.listeners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.Utils.debug;

public class MythicMobsListener implements Listener {
    private static final BukkitAPIHelper helper = MythicMobs.inst().getAPIHelper();
    private final FileConfiguration config = getInstance().getConfig();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final MythicMobSpawnEvent event) {
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.MythicMobs").contains(event.getMobType().getInternalName())) return;
        MobListener.onMobSpawn(event.getEntity(), event.getMobType().getDisplayName().get());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final Entity victim = event.getEntity();
        final Entity damager = event.getDamager();

        if (damager instanceof Player) {
            if (damager.hasMetadata("NPC") || !helper.isMythicMob(victim)) return;
            if (config.getBoolean("Blacklist.Whitelist", false)
                    != config.getStringList("Blacklist.MythicMobs").contains(helper.getMythicMobInstance(victim).getType().getInternalName())) return;
            MobListener.onPlayerDamage(damager.getUniqueId(), victim, event.getFinalDamage());
            debug("Damage for boss: " + ChatColor.stripColor(victim.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + damager.getName());
            debug("Data: " + MobListener.data);
        }

        if (victim instanceof Player) {
            if (victim.hasMetadata("NPC") || !helper.isMythicMob(damager)) return;
            if (config.getBoolean("Blacklist.Whitelist", false)
                    != config.getStringList("Blacklist.MythicMobs").contains(helper.getMythicMobInstance(damager).getType().getInternalName())) return;
            MobListener.onMobDamage(damager, victim.getUniqueId(), event.getFinalDamage());
            debug("Damage for boss: " + ChatColor.stripColor(damager.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + victim.getName());
            debug("Data: " + MobListener.data);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onDeath(final MythicMobDeathEvent event) {
        final ActiveMob mob = event.getMob();

        Object health;
        try {
            health = event.getMobType().getHealth().get();
        }
        catch (final NoClassDefFoundError e) {
            health = event.getMobType().getHealth();
        }

        MobListener.onMobDeath(event.getEntity(), mob.getDisplayName(), event.getMobType().getInternalName(), (Double) health);
    }
}
