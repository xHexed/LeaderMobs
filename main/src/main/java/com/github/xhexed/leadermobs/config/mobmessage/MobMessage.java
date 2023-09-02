package com.github.xhexed.leadermobs.config.mobmessage;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobDeathMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.MobSpawnMessage;
import com.github.xhexed.leadermobs.config.mobmessage.requirement.ResetDamageRequirement;
import com.github.xhexed.leadermobs.config.mobmessage.requirement.TotalDamageRequirement;
import com.github.xhexed.leadermobs.config.mobmessage.requirement.WarmupDamageRequirement;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;

@Getter
public class MobMessage {
    private LeaderMobs plugin;
    private List<String> mobs;
    private int playersRequired;
    private int placesToBroadcast;
    private TotalDamageRequirement totalDamageRequirement;
    private WarmupDamageRequirement warmupDamageRequirement;
    private ResetDamageRequirement resetDamageRequirement;
    private MobSpawnMessage mobSpawnMessage;
    private MobDeathMessage mobDeathMessage;

    public MobMessage(LeaderMobs plugin, ConfigurationSection config) {
        this.plugin = plugin;
        if (config.contains("mobs"))
            mobs = config.getStringList("mobs");
        if (config.contains("players-required"))
            playersRequired = config.getInt("players-required", 0);
        if (config.contains("places-to-broadcast"))
            placesToBroadcast = config.getInt("places-to-broadcast", Integer.MAX_VALUE);
        if (config.contains("total-damage-requirement")) {
            totalDamageRequirement = new TotalDamageRequirement(Objects.requireNonNull(config.getConfigurationSection("total-damage-requirement")));
        } else {
            totalDamageRequirement = new TotalDamageRequirement();
        }
        if (config.contains("warmup-damage-requirement")) {
            warmupDamageRequirement = new WarmupDamageRequirement(config.getConfigurationSection("warmup-damage-requirement"));
        }
        if (config.contains("reset-damage-requirement")) {
            resetDamageRequirement = new ResetDamageRequirement(config.getConfigurationSection("reset-damage-requirement"));
        }
        if (config.contains("mob-spawn-message")) {
            mobSpawnMessage = new MobSpawnMessage(plugin, config.getConfigurationSection("mob-spawn-message"));
        }
        if (config.contains("mob-dead-message")) {
            mobDeathMessage = new MobDeathMessage(this, Objects.requireNonNull(config.getConfigurationSection("mob-dead-message")));
        }
    }
}
