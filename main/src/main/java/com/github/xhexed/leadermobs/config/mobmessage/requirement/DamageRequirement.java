package com.github.xhexed.leadermobs.config.mobmessage.requirement;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public abstract class DamageRequirement {
    private long damageRequired;
    private long resetInterval;

    public DamageRequirement(ConfigurationSection config) {
        damageRequired = config.getInt("damage");
        resetInterval = config.getInt("reset-interval");
    }
}
