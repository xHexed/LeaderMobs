package com.github.xhexed.leadermobs.config.mobmessage.requirement;

import org.bukkit.configuration.ConfigurationSection;

public abstract class DamageRequirement {
    public long damageRequired;
    public long resetInterval;

    public DamageRequirement(ConfigurationSection config) {
        damageRequired = config.getInt("damage");
        resetInterval = config.getInt("reset-interval");
    }
}
