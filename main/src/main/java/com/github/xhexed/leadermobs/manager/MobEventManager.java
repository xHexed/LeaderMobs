package com.github.xhexed.leadermobs.manager;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobDeathMessage;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.data.MobData;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Map;
import java.util.UUID;

public class MobEventManager {
    private LeaderMobs plugin;

    public MobEventManager(LeaderMobs plugin) {
        this.plugin = plugin;
    }

    public void handleMobSpawn(String pluginName, Entity entity, String mobName, String mobDisplayName) {
        Map<String, MobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(pluginName);
        if (!mobMessages.containsKey(mobName)) return;
        MobMessage mobMessage = mobMessages.get(mobName);
        entity.setMetadata("leadermobs", new FixedMetadataValue(plugin, new MobDamageTracker()));
        mobMessage.getMobSpawnMessage().sendMessages(new MobData(entity, mobName, mobDisplayName));
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

    public void handleMobDeath(String pluginName, Entity entity, String mobName, String mobDisplayName) {
        if (!entity.hasMetadata("leadermobs")) return;
        MobDamageTracker damageInfo = null;
        for (MetadataValue metadataValue : entity.getMetadata("leadermobs")) {
            if (plugin.equals(metadataValue.getOwningPlugin())) {
                damageInfo = (MobDamageTracker) metadataValue.value();
                break;
            }
        }
        if (damageInfo == null) return;

        Map<String, MobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(pluginName);
        if (!mobMessages.containsKey(mobName)) return;
        MobMessage mobMessage = mobMessages.get(mobName);
        MobDeathMessage mobDeathMessage = mobMessage.getMobDeathMessage();

        if (damageInfo.getDealtDamageTracker().getDamageTracker().size() < mobMessage.getPlayersRequired()) return;
        damageInfo.calculateTop(mobMessage.getTotalDamageRequirement());
        mobDeathMessage.sendMessages(damageInfo, new MobData(entity, mobName, mobDisplayName));

        plugin.getRewardManager().giveReward(pluginName, mobName, damageInfo);
        entity.removeMetadata("leadermobs", plugin);
    }
}
