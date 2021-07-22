package com.github.xhexed.leadermobs.config.mobmessage.message;

import org.bukkit.configuration.ConfigurationSection;

public class MobDeathMessage {
    public DamageMessage damageDealtMessage;
    public DamageMessage damageTakenMessage;

    public MobDeathMessage(ConfigurationSection config) {
        damageDealtMessage = new DamageMessage(config.getConfigurationSection("damage-dealt"));
        damageTakenMessage = new DamageMessage(config.getConfigurationSection("damage-taken"));
    }
}
