package com.github.xhexed.leadermobs.config.mobmessage.message;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class MobDeathMessage {
    public DamageMessage damageDealtMessage;
    public DamageMessage damageTakenMessage;

    public MobDeathMessage(ConfigurationSection config) {
        damageDealtMessage = new DamageMessage(config.getConfigurationSection("damage-dealt"));
        damageTakenMessage = new DamageMessage(config.getConfigurationSection("damage-taken"));
    }

    public static class DamageMessage extends MobEventMessage {
        public boolean hideEmptyHeader;
        public boolean hideEmptyFooter;
        public List<String> headerMessages;
        public List<String> footerMessages;

        public DamageMessage(ConfigurationSection config) {
            super(config);
            hideEmptyHeader = config.getBoolean("hide-empty-header");
            hideEmptyFooter = config.getBoolean("hide-empty-footer");
            headerMessages = config.getStringList("header-messages");
            footerMessages = config.getStringList("footer-messages");
        }
    }
}
