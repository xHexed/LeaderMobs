package com.github.xhexed.leadermobs.listeners;

import com.github.xhexed.leadermobs.handler.MobHandler;
import com.github.xhexed.leadermobs.utils.Utils;
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

public class BossListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final BossSpawnEvent event) {
        final FileConfiguration config = getInstance().getConfig();
        final String bossName = event.getBoss().getName();
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.Boss").contains(bossName)) return;
        MobHandler.onMobSpawn(event.getEntity(), bossName);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final FileConfiguration config = getInstance().getConfig();
        final Entity victim = Utils.getDamageSource(event.getEntity());
        final Entity damager = Utils.getDamageSource(event.getDamager());

        if (damager instanceof Player) {
            if (damager.hasMetadata("NPC") || !BossAPI.isBoss(victim) ||
                    (config.getBoolean("Blacklist.Whitelist", false)
                            != config.getStringList("Blacklist.Boss").contains(BossAPI.getBoss(victim).getName()))
            ) return;
            MobHandler.onPlayerDamage(damager.getUniqueId(), victim, event.getFinalDamage());
        }

        if (victim instanceof Player) {
            if (victim.hasMetadata("NPC") || !BossAPI.isBoss(damager) ||
                    (config.getBoolean("Blacklist.Whitelist", false)
                            != config.getStringList("Blacklist.Boss").contains(BossAPI.getBoss(damager).getName()))
            ) return;
            MobHandler.onMobDamage(damager, victim.getUniqueId(), event.getFinalDamage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(final BossDeathEvent e) {
        final Boss boss = e.getBoss();
        MobHandler.onMobDeath(e.getEntity(), boss.getSettings().getCustomName(), boss.getName());
    }
}
