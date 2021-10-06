package com.github.xhexed.leadermobs.config.mobmessage;

import com.github.xhexed.leadermobs.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public class MobMessage extends AbstractMobMessage {
    public MobMessage(ConfigurationSection config) {
        super(config);
    }

    public static AbstractMobMessage getMobMessage(ConfigurationSection config, ConfigManager configManager) {
        return new MobMessage(config);
    }
}
