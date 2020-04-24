package com.github.xhexed.leadermobs.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.mineacademy.boss.api.Boss;
import org.mineacademy.boss.api.BossAPI;

import java.util.List;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.LeaderMobs.broadcast;

public class BossListener implements Listener {
    private final FileConfiguration config = getInstance().getConfig();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final EntitySpawnEvent event) {
        if (!BossAPI.isBoss(event.getEntity())) return;
        final Boss boss = BossAPI.getBoss(event.getEntity());
        final List<String> blacklist = config.getStringList("Blacklist.Boss");
        final boolean contain = blacklist.contains(boss.getName());
        if (config.getBoolean("Blacklist.Whitelist") == contain && broadcast) {
            final Location loc = event.getEntity().getLocation();
            MobListener.onMobSpawn(boss.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (!BossAPI.isBoss(entity)) return;
        final Boss boss = BossAPI.getBoss(entity);
        if (config.getBoolean("Blacklist.Whitelist", false) == config.getStringList("Blacklist.Boss").contains(boss.getName())) {
            final Entity damager = event.getDamager();
            if (damager.hasMetadata("NPC")) { return; }
            if (damager instanceof Player) {
                MobListener.onMobDamage((Player) damager, entity, event.getFinalDamage());
                getInstance().debug("Final damage for boss:" + ChatColor.stripColor(entity.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + damager.getName());
                getInstance().debug("Data: " + MobListener.data);
            }
        }
    }

    @EventHandler
    public void onDeath(final EntityDeathEvent e) {
        if (!BossAPI.isBoss(e.getEntity())) return;
        final Boss boss = BossAPI.getBoss(e.getEntity());
        MobListener.onMobDeath(e.getEntity(), boss.getSettings().getCustomName(), 0, boss.getName(), boss.getSettings().getHealth());
    }
}
