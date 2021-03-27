package com.github.xhexed.leadermobs.config.mobmessage;

import com.github.xhexed.leadermobs.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public class MobMessage extends AbstractMobMessage {
    public MobMessage(ConfigurationSection config) {
        super(config);
    }

    public static AbstractMobMessage getMobMessage(ConfigurationSection config, ConfigManager configManager) {
        if (config.contains("template")) {
            AbstractMobMessage mobMessage = configManager.getTemplateMobMessages().get(config.getString("template"));
            if (mobMessage != null) {
                mobMessage.setMobMessage(config);
                return mobMessage;
            }
            else {
                return new MobMessage(config);
            }
        }
        else {
            return new MobMessage(config);
        }
    }
}
