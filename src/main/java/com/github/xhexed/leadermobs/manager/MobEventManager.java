package com.github.xhexed.leadermobs.manager;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.AbstractMobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.ActionbarMessage;
import com.github.xhexed.leadermobs.config.mobmessage.TitleMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobDeathMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobEventMessage;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.github.xhexed.leadermobs.util.Util.*;
import static org.bukkit.Bukkit.getScheduler;

public class MobEventManager {
    private LeaderMobs plugin;

    public MobEventManager(LeaderMobs plugin) {
        this.plugin = plugin;
    }

    public void handleMobSpawn(String pluginName, Entity entity, String mobName, String mobDisplayName) {
        Map<String, AbstractMobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(pluginName);
        if (!mobMessages.containsKey(mobName)) return;
        AbstractMobMessage mobMessage = mobMessages.get(mobName);

        entity.setMetadata("leadermobs", new FixedMetadataValue(plugin, new MobDamageTracker(mobMessage)));

        Location location = entity.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        MobEventMessage mobSpawnMessage = mobMessage.mobSpawnMessage;
        getScheduler().runTaskLater(plugin, () -> {
            mobSpawnMessage.messages.stream()
                    .map(message -> plugin.getPluginUtil().getMobSpawnMessage(mobDisplayName, x, y, z, message))
                    .forEach(message -> plugin.getPluginUtil().sendMessage(message));

            TitleMessage titleMessage = mobSpawnMessage.titleMessage;
            if (titleMessage != null) {
                getScheduler().runTaskLater(plugin, () -> Bukkit.getOnlinePlayers().forEach((p) -> plugin.getPluginUtil().sendTitle(p, plugin.getPluginUtil().getMobSpawnMessage(mobDisplayName, x, y, z, titleMessage.title),
                        plugin.getPluginUtil().getMobSpawnMessage(mobDisplayName, x, y, z, titleMessage.subTitle),
                        titleMessage.fadeIn,
                        titleMessage.stay,
                        titleMessage.fadeOut)), titleMessage.delay);
            }

            ActionbarMessage actionbarMessage = mobSpawnMessage.actionbarMessage;
            if (actionbarMessage != null) {
                getScheduler().runTaskLater(plugin, () -> Bukkit.getOnlinePlayers().forEach((p) -> plugin.getPluginUtil().sendActionBar(p, plugin.getPluginUtil().getMobSpawnMessage(mobName, x, y, z, actionbarMessage.message))), actionbarMessage.delay);
            }
        }, mobSpawnMessage.delay);
    }

    public void handlePlayerDamage(UUID player, Entity entity, Double damage, AbstractMobMessage mobMessage) {
        MobDamageTracker info;
        if (entity.hasMetadata("leadermobs")) {
            List<MetadataValue> metadataValues = entity.getMetadata("leadermobs");
            info = metadataValues.stream().filter(metadata -> plugin.equals(metadata.getOwningPlugin())).findFirst().map(metadata -> (MobDamageTracker) metadata.value()).orElse(new MobDamageTracker(mobMessage));
        }
        else {
            info = new MobDamageTracker(mobMessage);
        }
        Map<UUID, Double> damageDealtList = Objects.requireNonNull(info).getDamageDealt();
        damageDealtList.put(player, damage + damageDealtList.getOrDefault(player, 0D));
        info.setDamageDealt(damageDealtList);
        entity.setMetadata("leadermobs", new FixedMetadataValue(plugin, info));
    }

    public void handleMobDamage(Entity entity, UUID player, Double damage, AbstractMobMessage mobMessage) {
        MobDamageTracker info;
        if (entity.hasMetadata("leadermobs")) {
            info = entity.getMetadata("leadermobs").stream().filter(metadata -> plugin.equals(metadata.getOwningPlugin())).findFirst().map(metadata -> (MobDamageTracker) metadata.value()).orElse(new MobDamageTracker(mobMessage));
        }
        else {
            info = new MobDamageTracker(mobMessage);
        }
        Map<UUID, Double> damageTakenList = info.getDamageTaken();
        damageTakenList.put(player, damage + damageTakenList.getOrDefault(player, 0D));
        info.setDamageTaken(damageTakenList);
        entity.setMetadata("leadermobs", new FixedMetadataValue(plugin, info));
    }

    public void handleMobDeath(String pluginName, Entity entity, String mobName, String mobDisplayName) {
        if (!entity.hasMetadata("leadermobs")) return;
        MobDamageTracker damageInfo = entity.getMetadata("leadermobs").stream().filter(metadata -> plugin.equals(metadata.getOwningPlugin())).findFirst().map(metadata -> (MobDamageTracker) metadata.value()).orElse(null);
        if (damageInfo == null) return;
        damageInfo.calculateTop();

        Map<String, AbstractMobMessage> mobMessages = plugin.getConfigManager().getPluginMobMessages().get(pluginName);
        if (!mobMessages.containsKey(mobName)) return;
        AbstractMobMessage mobMessage = mobMessages.get(mobName);
        MobDeathMessage mobDeathMessage = mobMessage.mobDeathMessage;
        if (damageInfo.getDamageDealt().size() < mobMessage.playersRequired) return;
        MobDeathMessage.DamageMessage damageDealtMessage = mobDeathMessage.damageDealtMessage;
        if (damageDealtMessage != null) {
            getScheduler().runTaskLater(plugin, () -> {
                if (!(damageInfo.getTopDamageDealt().isEmpty() && damageDealtMessage.hideEmptyHeader)) {
                    List<String> damageDealtHeaders = damageDealtMessage.headerMessages;
                    for (String damageDealtHeader : damageDealtHeaders) {
                        damageDealtHeader = NAME.matcher(damageDealtHeader != null ? damageDealtHeader : "").replaceAll(ChatColor.stripColor(mobDisplayName));
                        damageDealtHeader = replaceMobPlaceholder(damageDealtHeader, damageInfo);
                        plugin.getPluginUtil().sendMessage(damageDealtHeader);
                    }
                }

                plugin.getPluginUtil().sendPlaceMessage(damageInfo.getTotalDamageDealt(), mobMessage, damageInfo.getTopDamageDealt(), damageDealtMessage.messages);

                TitleMessage titleMessage = damageDealtMessage.titleMessage;
                if (titleMessage != null) {
                    getScheduler().runTaskLater(plugin, () -> Bukkit.getOnlinePlayers().forEach((p) -> plugin.getPluginUtil().sendTitle(p, plugin.getPluginUtil().getMobDeathMessage(damageInfo, mobDisplayName, titleMessage.title),
                            plugin.getPluginUtil().getMobDeathMessage(damageInfo, mobDisplayName, titleMessage.subTitle),
                            titleMessage.fadeIn,
                            titleMessage.stay,
                            titleMessage.fadeOut)), titleMessage.delay);
                }

                ActionbarMessage actionbarMessage = damageDealtMessage.actionbarMessage;
                if (actionbarMessage != null) {
                    getScheduler().runTaskLater(plugin, () -> Bukkit.getOnlinePlayers().forEach((p) -> plugin.getPluginUtil().sendActionBar(p, plugin.getPluginUtil().getMobDeathMessage(damageInfo, mobDisplayName, actionbarMessage.message))), actionbarMessage.delay);
                }

                if (!(damageInfo.getTopDamageDealt().isEmpty() && damageDealtMessage.hideEmptyFooter)) {
                    List<String> damageDealtFooters = damageDealtMessage.footerMessages;
                    for (String damageDealtFooter : damageDealtFooters) {
                        damageDealtFooter = NAME.matcher(damageDealtFooter).replaceAll(ChatColor.stripColor(mobDisplayName));
                        damageDealtFooter = replaceMobPlaceholder(damageDealtFooter, damageInfo);
                        plugin.getPluginUtil().sendMessage(damageDealtFooter);
                    }
                }
            }, damageDealtMessage.delay);
        }

        MobDeathMessage.DamageMessage damageTakenMessage = mobDeathMessage.damageTakenMessage;
        if (damageTakenMessage != null) {
            getScheduler().runTaskLater(plugin, () -> {
                if (!(damageInfo.getTopDamageTaken().isEmpty() && damageTakenMessage.hideEmptyHeader)) {
                    List<String> damageTakenHeaders = damageTakenMessage.headerMessages;
                    for (String damageTakenHeader : damageTakenHeaders) {
                        damageTakenHeader = NAME.matcher(damageTakenHeader != null ? damageTakenHeader : "").replaceAll(ChatColor.stripColor(mobDisplayName));
                        damageTakenHeader = replaceMobPlaceholder(damageTakenHeader, damageInfo);
                        plugin.getPluginUtil().sendMessage(damageTakenHeader);
                    }
                }

                plugin.getPluginUtil().sendPlaceMessage(damageInfo.getTotalDamageDealt(), mobMessage, damageInfo.getTopDamageDealt(), damageTakenMessage.messages);

                TitleMessage titleMessage = damageTakenMessage.titleMessage;
                if (titleMessage != null) {
                    getScheduler().runTaskLater(plugin, () -> Bukkit.getOnlinePlayers().forEach((p) -> plugin.getPluginUtil().sendTitle(p, plugin.getPluginUtil().getMobDeathMessage(damageInfo, mobDisplayName, titleMessage.title),
                            plugin.getPluginUtil().getMobDeathMessage(damageInfo, mobDisplayName, titleMessage.subTitle),
                            titleMessage.fadeIn,
                            titleMessage.stay,
                            titleMessage.fadeOut)), titleMessage.delay);
                }

                ActionbarMessage actionbarMessage = damageTakenMessage.actionbarMessage;
                if (actionbarMessage != null) {
                    getScheduler().runTaskLater(plugin, () -> Bukkit.getOnlinePlayers().forEach((p) -> plugin.getPluginUtil().sendActionBar(p, plugin.getPluginUtil().getMobDeathMessage(damageInfo, mobDisplayName, actionbarMessage.message))), actionbarMessage.delay);
                }

                if (!(damageInfo.getTopDamageTaken().isEmpty() && damageTakenMessage.hideEmptyFooter)) {
                    List<String> damageTakenFooters = damageTakenMessage.footerMessages;
                    for (String damageTakenFooter : damageTakenFooters) {
                        damageTakenFooter = NAME.matcher(damageTakenFooter).replaceAll(ChatColor.stripColor(mobDisplayName));
                        damageTakenFooter = replaceMobPlaceholder(damageTakenFooter, damageInfo);
                        plugin.getPluginUtil().sendMessage(damageTakenFooter);
                    }
                }
            }, damageTakenMessage.delay);
        }

        plugin.getRewardManager().giveReward(pluginName, mobName, damageInfo);
        entity.removeMetadata("leadermobs", plugin);
    }
}
