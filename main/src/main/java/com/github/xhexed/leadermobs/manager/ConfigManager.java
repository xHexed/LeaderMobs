package com.github.xhexed.leadermobs.manager;

import com.bgsoftware.common.config.CommentedConfiguration;
import com.github.xhexed.leadermobs.config.PluginMessage;
import com.github.xhexed.leadermobs.config.mobmessage.AbstractMobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private Plugin plugin;
    private Map<String, Map<String, AbstractMobMessage>> pluginMobMessages = new HashMap<>();
    private PluginMessage pluginMessage;

    public ConfigManager(Plugin plugin) {
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
                Map<String, AbstractMobMessage> mobMessages = new HashMap<>();
                ConfigurationSection pluginSection = pluginHooks.getConfigurationSection(pl);
                for (String mobName : Objects.requireNonNull(pluginSection).getKeys(false)) {
                    AbstractMobMessage mobMessage = MobMessage.getMobMessage(Objects.requireNonNull(pluginSection.getConfigurationSection(mobName)));
                    if (mobMessage.mobs != null) {
                        for (String mob : mobMessage.mobs) {
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

    public Map<String, Map<String, AbstractMobMessage>> getPluginMobMessages() {
        return pluginMobMessages;
    }

    public PluginMessage getPluginMessage() {
        return pluginMessage;
    }
}
