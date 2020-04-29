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
import org.mineacademy.boss.api.Boss;
import org.mineacademy.boss.api.BossAPI;
import org.mineacademy.boss.api.event.BossDeathEvent;
import org.mineacademy.boss.api.event.BossSpawnEvent;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.Utils.debug;

public class BossListener implements Listener {
    private final FileConfiguration config = getInstance().getConfig();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final BossSpawnEvent event) {
        final String bossName = event.getBoss().getName();
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.Boss").contains(bossName)) return;
        final Location loc = event.getEntity().getLocation();
        MobListener.onMobSpawn(event.getEntity(), bossName);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (!BossAPI.isBoss(entity)) return;
        final Boss boss = BossAPI.getBoss(entity);

        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.Boss").contains(boss.getName())) return;

        final Entity damager = event.getDamager();
        if (damager.hasMetadata("NPC") || !(damager instanceof Player)) return;

        MobListener.onPlayerDamage((Player) damager, entity, event.getFinalDamage());
        debug("Final damage for boss:" + ChatColor.stripColor(entity.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + damager.getName());
        debug("Data: " + MobListener.data);
    }

    @EventHandler
    public void onDeath(final BossDeathEvent e) {
        final Boss boss = e.getBoss();
        MobListener.onMobDeath(e.getEntity(), boss.getSettings().getCustomName(), boss.getName(), boss.getSettings().getHealth());
    }
}
