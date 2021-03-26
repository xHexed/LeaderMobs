package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mineacademy.boss.api.Boss;
import org.mineacademy.boss.api.BossAPI;
import org.mineacademy.boss.api.event.BossDeathEvent;
import org.mineacademy.boss.api.event.BossSpawnEvent;

public class BossListener extends CustomMobListener implements Listener {
    public BossListener(LeaderMobs plugin) {
        super(plugin, "Boss");
    }

    @Override
    public String getMobName(Entity entity) {
        return BossAPI.getBoss(entity).getName();
    }

    @Override
    public boolean isMob(Entity entity) {
        return BossAPI.isBoss(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(BossSpawnEvent event) {
        Boss boss = event.getBoss();
        handleMobSpawn(event.getEntity(), boss.getName(), boss.getSettings().getCustomName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        handleMobDamage(event.getDamager(), event.getEntity(), event.getFinalDamage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(BossDeathEvent e) {
        Boss boss = e.getBoss();
        handleMobDeath(e.getEntity(), boss.getSettings().getCustomName(), boss.getName());
    }
}
