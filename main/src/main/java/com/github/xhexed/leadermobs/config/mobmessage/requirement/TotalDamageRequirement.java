package com.github.xhexed.leadermobs.config.mobmessage.requirement;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class TotalDamageRequirement {
    private long damageDealtRequired;
    private long damageTakenRequired;

    public TotalDamageRequirement() { }

    public TotalDamageRequirement(ConfigurationSection config) {
        damageDealtRequired = config.getLong("dealt-damage-required");
        damageTakenRequired = config.getLong("taken-damage-required");
    }
}
