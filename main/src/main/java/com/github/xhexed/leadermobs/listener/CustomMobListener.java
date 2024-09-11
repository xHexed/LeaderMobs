package com.github.xhexed.leadermobs.listener;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobDeathMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobSpawnMessage;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.data.MobData;
import com.github.xhexed.leadermobs.data.MobTracker;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;
import java.util.function.BiFunction;

public abstract class CustomMobListener {
    protected LeaderMobs plugin;
    protected String name;

    public CustomMobListener(LeaderMobs plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract String getMobName(Entity entity);

    public abstract boolean isMob(Entity entity);

    public void registerMobChecker(String condition, BiFunction<Entity, MobMessage, String> checker) {
        plugin.getConfigManager().getMobChecker().addChecker(condition, checker);
    }

    public void handleMobSpawn(Entity entity, String mobName, String mobDisplayName) {
        MobMessage mobMessage = plugin.getConfigManager().getMobMessage(name, mobName, entity);
        if (mobMessage == null) return;
        entity.setMetadata("leadermobs", new FixedMetadataValue(plugin, new MobTracker(mobMessage, new MobDamageTracker())));
        MobSpawnMessage spawnMessage = mobMessage.getMobSpawnMessage();
        if (spawnMessage == null) return;
        spawnMessage.sendMessages(new MobData(entity, mobName, mobDisplayName));
    }

    public void handleMobDamage(Entity attacker, Entity victim, double damage) {
        attacker = getDamageSource(attacker);
        victim = getDamageSource(victim);

        if (attacker instanceof Player && !attacker.hasMetadata("NPC") && isMob(victim)) {
            handlePlayerDamage(attacker.getUniqueId(), victim, damage);
        }
        if (victim instanceof Player && !victim.hasMetadata("NPC") && isMob(attacker)) {
            handleMobDamage(attacker, victim.getUniqueId(), damage);
        }
    }

    public void handlePlayerDamage(UUID player, Entity entity, Double damage) {
        if (!entity.hasMetadata("leadermobs")) return;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (!plugin.equals(metadataValue.getOwningPlugin())) continue;
            MobTracker tracker = (MobTracker) metadataValue.value();
            if (tracker == null) continue;
            tracker.getDamageTracker().getDealtDamageTracker().addDamage(player, damage);
            return;
        }
    }

    public void handleMobDamage(Entity entity, UUID player, Double damage) {
        if (!entity.hasMetadata("leadermobs")) return;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (!plugin.equals(metadataValue.getOwningPlugin())) continue;
            MobTracker tracker = (MobTracker) metadataValue.value();
            if (tracker == null) continue;
            tracker.getDamageTracker().getTakenDamageTracker().addDamage(player, damage);
            return;
        }
    }

    public void handleMobDeath(Entity entity, String mobName, String mobDisplayName) {
        if (!entity.hasMetadata("leadermobs")) return;
        MobTracker tracker = null;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (!plugin.equals(metadataValue.getOwningPlugin())) continue;
            tracker = (MobTracker) metadataValue.value();
            break;
        }
        if (tracker == null) return;

        MobDamageTracker damageInfo = tracker.getDamageTracker();
        MobMessage mobMessage = tracker.getMobMessage();

        if (damageInfo.getDealtDamageTracker().getDamageTracker().size() < mobMessage.getPlayersRequired()) return;
        damageInfo.calculateTop(mobMessage.getTotalDamageRequirement());

        MobDeathMessage mobDeathMessage = mobMessage.getMobDeathMessage();
        if (mobDeathMessage != null)
            mobDeathMessage.sendMessages(damageInfo, new MobData(entity, mobName, mobDisplayName));

        plugin.getRewardManager().giveReward(name, mobName, damageInfo);
        entity.removeMetadata("leadermobs", plugin); //Make sure data is removed
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
