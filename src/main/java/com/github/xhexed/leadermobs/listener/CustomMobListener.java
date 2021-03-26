package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.AbstractMobMessage;
import com.github.xhexed.leadermobs.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
        plugin.getMobEventHandler().handleMobSpawn(name, entity, mobName, mobDisplayName);
    }

    public void handleMobDamage(Entity attacker, Entity victim, double damage) {
        attacker = Util.getDamageSource(attacker);
        victim = Util.getDamageSource(victim);

        Map<String, AbstractMobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(name);
        if (attacker instanceof Player && !attacker.hasMetadata("NPC") && isMob(victim)) {
            String mobName = getMobName(victim);
            if (mobMessages.containsKey(mobName)) {
                plugin.getMobEventHandler().handlePlayerDamage(attacker.getUniqueId(), victim, damage, mobMessages.get(mobName));
            }
        }
        if (victim instanceof Player && !victim.hasMetadata("NPC") && isMob(attacker)) {
            String mobName = getMobName(attacker);
            if (mobMessages.containsKey(mobName)) {
                plugin.getMobEventHandler().handleMobDamage(attacker, victim.getUniqueId(), damage, mobMessages.get(mobName));
            }
        }
    }

    public void handleMobDeath(Entity entity, String mobName, String mobDisplayName) {
        plugin.getMobEventHandler().handleMobDeath(name, entity, mobName, mobDisplayName);
    }
}
