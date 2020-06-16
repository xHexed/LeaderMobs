package com.github.xhexed.leadermobs.listeners;

import com.github.xhexed.leadermobs.handler.MobHandler;
import org.bukkit.ChatColor;
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
import static com.github.xhexed.leadermobs.utils.Utils.debugln;

public class BossListener implements Listener {
    private final FileConfiguration config = getInstance().getConfig();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final BossSpawnEvent event) {
        final String bossName = event.getBoss().getName();
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.Boss").contains(bossName)) return;
        MobHandler.onMobSpawn(event.getEntity(), bossName);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity victim = event.getEntity();
        final Entity damager = event.getDamager();

        if (damager instanceof Player) {
            if (damager.hasMetadata("NPC") || !BossAPI.isBoss(victim) ||
                    (config.getBoolean("Blacklist.Whitelist", false)
                            != config.getStringList("Blacklist.Boss").contains(BossAPI.getBoss(entity).getName()))
            ) return;
            MobHandler.onPlayerDamage(damager.getUniqueId(), victim, event.getFinalDamage());
            debugln("Damage for boss: " + ChatColor.stripColor(victim.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + damager.getName());
        }

        if (victim instanceof Player) {
            if (victim.hasMetadata("NPC") || !BossAPI.isBoss(damager) ||
                    (config.getBoolean("Blacklist.Whitelist", false)
                            != config.getStringList("Blacklist.Boss").contains(BossAPI.getBoss(entity).getName()))
            ) return;
            MobHandler.onMobDamage(damager, victim.getUniqueId(), event.getFinalDamage());
            debugln("Damage for boss: " + ChatColor.stripColor(damager.getName()) + ", damage: " + event.getFinalDamage() + ", player: " + victim.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(final BossDeathEvent e) {
        final Boss boss = e.getBoss();
        MobHandler.onMobDeath(e.getEntity(), boss.getSettings().getCustomName(), boss.getName(), boss.getSettings().getHealth());
    }
}