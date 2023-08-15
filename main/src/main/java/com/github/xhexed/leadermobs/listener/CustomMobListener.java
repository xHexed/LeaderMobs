package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;

public abstract class CustomMobListener {
    protected LeaderMobs plugin;
    protected String name;

    public CustomMobListener(LeaderMobs plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract String getMobName(Entity entity);

    public abstract boolean isMob(Entity entity);

    public void handleMobSpawn(Entity entity, String mobName, String mobDisplayName) {
        plugin.getMobEventManager().handleMobSpawn(name, entity, mobName, mobDisplayName);
    }

    public void handleMobDamage(Entity attacker, Entity victim, double damage) {
        attacker = getDamageSource(attacker);
        victim = getDamageSource(victim);

        Map<String, MobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(name);
        if (attacker instanceof Player && !attacker.hasMetadata("NPC") && isMob(victim)) {
            String mobName = getMobName(victim);
            if (mobMessages.containsKey(mobName)) {
                plugin.getMobEventManager().handlePlayerDamage(attacker.getUniqueId(), victim, damage);
            }
        }
        if (victim instanceof Player && !victim.hasMetadata("NPC") && isMob(attacker)) {
            String mobName = getMobName(attacker);
            if (mobMessages.containsKey(mobName)) {
                plugin.getMobEventManager().handleMobDamage(attacker, victim.getUniqueId(), damage);
            }
        }
    }

    public void handleMobDeath(Entity entity, String mobName, String mobDisplayName) {
        plugin.getMobEventManager().handleMobDeath(name, entity, mobName, mobDisplayName);
    }

    private Entity getDamageSource(Entity entity) {
        if (entity instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Entity) entity = (Entity) shooter;
        }
        if (entity instanceof TNTPrimed) {
            Entity source = ((TNTPrimed) entity).getSource();
            if (source != null) entity = source;
        }
        return entity;
    }
}
