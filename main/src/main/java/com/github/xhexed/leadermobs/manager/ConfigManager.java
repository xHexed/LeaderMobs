package com.github.xhexed.leadermobs.manager;

import com.bgsoftware.common.config.CommentedConfiguration;
import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.PluginMessage;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ConfigManager {
    @Getter(value = AccessLevel.NONE)
    private LeaderMobs plugin;
    private Map<String, Map<String, MobMessage>> pluginMobMessages = new HashMap<>();
    private PluginMessage pluginMessage;

    public ConfigManager(LeaderMobs plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists())
            plugin.saveResource("config.yml", false);
        CommentedConfiguration config = CommentedConfiguration.loadConfiguration(file);

        if (config.getBoolean("auto-update", true)) {
            try {
                config.syncWithConfig(file, plugin.getResource("config.yml"), "mob-messages.plugin-hooks");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pluginMobMessages.clear();
        ConfigurationSection pluginHooks = config.getConfigurationSection("mob-messages.plugin-hooks");
        if (pluginHooks != null) {
            pluginHooks.getKeys(false).forEach((pl -> {
                Map<String, MobMessage> mobMessages = new HashMap<>();
                ConfigurationSection pluginSection = pluginHooks.getConfigurationSection(pl);
                for (String mobName : Objects.requireNonNull(pluginSection).getKeys(false)) {
                    MobMessage mobMessage = new MobMessage(plugin, Objects.requireNonNull(pluginSection.getConfigurationSection(mobName))) {
                    };
                    if (mobMessage.getMobs() != null) {
                        for (String mob : mobMessage.getMobs()) {
                            mobMessages.put(mob, mobMessage);
                        }
                    }
                    mobMessages.put(mobName, mobMessage);
                }
                pluginMobMessages.put(pl, mobMessages);
            }));
        }
        pluginMessage = new PluginMessage(Objects.requireNonNull(config.getConfigurationSection("plugin-messages")));
    }
}
