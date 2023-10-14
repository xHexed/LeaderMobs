package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobDeathMessage;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.data.MobData;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.UUID;

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
        Map<String, MobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(name);
        if (!mobMessages.containsKey(mobName)) return;
        MobMessage mobMessage = mobMessages.get(mobName);
        entity.setMetadata("leadermobs", new FixedMetadataValue(plugin, new MobDamageTracker()));
        mobMessage.getMobSpawnMessage().sendMessages(new MobData(entity, mobName, mobDisplayName));
    }

    public void handleMobDamage(Entity attacker, Entity victim, double damage) {
        attacker = getDamageSource(attacker);
        victim = getDamageSource(victim);

        Map<String, MobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(name);
        if (attacker instanceof Player && !attacker.hasMetadata("NPC") && isMob(victim)) {
            String mobName = getMobName(victim);
            if (mobMessages.containsKey(mobName)) {
                handlePlayerDamage(attacker.getUniqueId(), victim, damage);
            }
        }
        if (victim instanceof Player && !victim.hasMetadata("NPC") && isMob(attacker)) {
            String mobName = getMobName(attacker);
            if (mobMessages.containsKey(mobName)) {
                handleMobDamage(attacker, victim.getUniqueId(), damage);
            }
        }
    }

    public void handlePlayerDamage(UUID player, Entity entity, Double damage) {
        if (!entity.hasMetadata("leadermobs")) return;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (plugin.equals(metadataValue.getOwningPlugin())) {
                MobDamageTracker tracker = (MobDamageTracker) metadataValue.value();
                if (tracker != null) tracker.getDealtDamageTracker().addDamage(player, damage);
                return;
            }
        }
    }

    public void handleMobDamage(Entity entity, UUID player, Double damage) {
        if (!entity.hasMetadata("leadermobs")) return;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (plugin.equals(metadataValue.getOwningPlugin())) {
                MobDamageTracker tracker = (MobDamageTracker) metadataValue.value();
                if (tracker != null) tracker.getTakenDamageTracker().addDamage(player, damage);
                return;
            }
        }
    }

    public void handleMobDeath(Entity entity, String mobName, String mobDisplayName) {
        if (!entity.hasMetadata("leadermobs")) return;
        MobDamageTracker damageInfo = null;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (plugin.equals(metadataValue.getOwningPlugin())) {
                damageInfo = (MobDamageTracker) metadataValue.value();
                break;
            }
        }
        if (damageInfo == null) return;

        Map<String, MobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(name);
        if (!mobMessages.containsKey(mobName)) return;
        MobMessage mobMessage = mobMessages.get(mobName);
        MobDeathMessage mobDeathMessage = mobMessage.getMobDeathMessage();

        if (damageInfo.getDealtDamageTracker().getDamageTracker().size() < mobMessage.getPlayersRequired()) return;
        damageInfo.calculateTop(mobMessage.getTotalDamageRequirement());
        if (mobDeathMessage != null)
            mobDeathMessage.sendMessages(damageInfo, new MobData(entity, mobName, mobDisplayName));

        plugin.getRewardManager().giveReward(name, mobName, damageInfo);
        entity.removeMetadata("leadermobs", plugin);
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
        if (entity instanceof Tameable) {
            AnimalTamer source = ((Tameable) entity).getOwner();
            if (source instanceof Entity) entity = (Entity) source;
        }
        return entity;
    }
}
