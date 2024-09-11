package com.github.xhexed.leadermobs.config.updater.impl;

import com.github.xhexed.leadermobs.config.updater.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

public class NewConfigUpdater implements ConfigUpdater {
    public static NewConfigUpdater INSTANCE = new NewConfigUpdater();

    @Override
    public void update(FileConfiguration config, FileConfiguration defaultConfig) {
        ConfigurationSection pluginHooks = config.getConfigurationSection("mob-messages.plugin-hooks");
        if (pluginHooks == null) return;
        for (String plugin : pluginHooks.getKeys(false)) {
            ConfigurationSection pluginMobMessages = pluginHooks.getConfigurationSection(plugin);
            if (pluginMobMessages == null) continue;
            for (String mobMessageID : pluginMobMessages.getKeys(false)) {
                ConfigurationSection mobMessages = pluginMobMessages.getConfigurationSection(mobMessageID);
                if (mobMessages == null) continue;
                mobMessages.set("mobs",
                        mobMessages.getStringList("mobs").stream()
                                .map(mob -> plugin + ";" + mob)
                                .collect(Collectors.toList()));
                config.set("mob-messages." + mobMessageID, mobMessages);
            }
        }
        config.set("mob-messages.plugin-hooks", null);
    }

}
