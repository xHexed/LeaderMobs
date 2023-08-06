package com.github.xhexed.leadermobs.config.mobmessage;

import org.bukkit.configuration.ConfigurationSection;

public class MobMessage extends AbstractMobMessage {
    public MobMessage(ConfigurationSection config) {
        super(config);
    }

    public static AbstractMobMessage getMobMessage(ConfigurationSection config) {
        return new MobMessage(config);
    }
}
