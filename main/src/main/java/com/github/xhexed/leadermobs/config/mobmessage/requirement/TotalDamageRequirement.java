package com.github.xhexed.leadermobs.config.mobmessage.requirement;

import org.bukkit.configuration.ConfigurationSection;

public class TotalDamageRequirement {
    public long damageDealtRequired;
    public long damageTakenRequired;

    public TotalDamageRequirement(ConfigurationSection config) {
        damageDealtRequired = config.getLong("dealt-damage-required");
        damageTakenRequired = config.getLong("taken-damage-required");
    }
}
