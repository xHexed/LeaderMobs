package com.github.xhexed.leadermobs.config.mobmessage;

import com.github.xhexed.leadermobs.config.mobmessage.message.MobDeathMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobEventMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobSpawnMessage;
import com.github.xhexed.leadermobs.config.mobmessage.requirement.ResetDamageRequirement;
import com.github.xhexed.leadermobs.config.mobmessage.requirement.TotalDamageRequirement;
import com.github.xhexed.leadermobs.config.mobmessage.requirement.WarmupDamageRequirement;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;

public abstract class AbstractMobMessage {
    public List<String> mobs;
    public int playersRequired;
    public int placesToBroadcast;
    public TotalDamageRequirement totalDamageRequirement;
    public WarmupDamageRequirement warmupDamageRequirement;
    public ResetDamageRequirement resetDamageRequirement;
    public MobEventMessage mobSpawnMessage;
    public MobDeathMessage mobDeathMessage;

    public AbstractMobMessage(ConfigurationSection config) {
        setMobMessage(config);
    }

    public void setMobMessage(ConfigurationSection config) {
        if (config.contains("mobs"))
            mobs = config.getStringList("mobs");
        if (config.contains("players-required"))
            playersRequired = config.getInt("players-required");
        if (config.contains("places-to-broadcast"))
            placesToBroadcast = config.getInt("places-to-broadcast");
        if (config.contains("total-damage-requirement")) {
            totalDamageRequirement = new TotalDamageRequirement(Objects.requireNonNull(config.getConfigurationSection("total-damage-requirement")));
        }
        if (config.contains("warmup-damage-requirement")) {
            warmupDamageRequirement = new WarmupDamageRequirement(config.getConfigurationSection("warmup-damage-requirement"));
        }
        if (config.contains("reset-damage-requirement")) {
            resetDamageRequirement = new ResetDamageRequirement(config.getConfigurationSection("reset-damage-requirement"));
        }
        if (config.contains("mob-spawn-message")) {
            mobSpawnMessage = new MobSpawnMessage(config.getConfigurationSection("mob-spawn-message"));
        }
        if (config.contains("mob-dead-message")) {
            mobDeathMessage = new MobDeathMessage(Objects.requireNonNull(config.getConfigurationSection("mob-dead-message")));
        }
    }
}
