package com.github.xhexed.leadermobs.config.mobmessage;

import com.github.xhexed.leadermobs.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public class MobMessage extends AbstractMobMessage {
    public MobMessage(ConfigurationSection config, ConfigManager configManager) {
        super(config);
        if (mobs != null) {
            for (String mob: mobs) {
                configManager.getBlacklistMobMessages().put(mob, this);
            }
        }
    }

    public static AbstractMobMessage getMobMessage(ConfigurationSection config, ConfigManager configManager) {
        if (config.contains("template")) {
            AbstractMobMessage mobMessage = configManager.getTemplateMobMessages().get(config.getString("template"));
            mobMessage.setMobMessage(config);
            if (mobMessage.mobs != null) {
                for (String mob: mobMessage.mobs) {
                    configManager.getBlacklistMobMessages().put(mob, mobMessage);
                }
            }
            return mobMessage;
        }
        else {
            return new MobMessage(config, configManager);
        }
    }
}
