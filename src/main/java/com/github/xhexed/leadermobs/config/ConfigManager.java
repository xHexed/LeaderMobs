package com.github.xhexed.leadermobs.config;

import com.github.xhexed.leadermobs.config.mobmessage.AbstractMobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.TemplateMobMessage;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {
    private Plugin plugin;
    private Map<String, TemplateMobMessage> templateMobMessages = new HashMap<>();
    private Map<String, Map<String, AbstractMobMessage>> pluginMobMessages = new HashMap<>();
    private PluginMessage pluginMessage;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        FileConfiguration config = plugin.getConfig();

        if (config.getBoolean("auto-update", true)) {
            try {
                ConfigUpdater.update(plugin, "config.yml", new File(plugin.getDataFolder(), "config.yml"), Arrays.asList("templates", "mob-messages"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        templateMobMessages.clear();
        pluginMobMessages.clear();
        ConfigurationSection templates = config.getConfigurationSection("templates");
        if (templates != null) {
            for (String template : templates.getKeys(false)) {
                templateMobMessages.put(template, new TemplateMobMessage(templates.getConfigurationSection(template)));
            }
        }
        ConfigurationSection pluginHooks = config.getConfigurationSection("mob-messages.plugin-hooks");
        if (pluginHooks != null) {
            pluginHooks.getKeys(false).forEach((pl -> {
                Map<String, AbstractMobMessage> mobMessages = new HashMap<>();
                ConfigurationSection pluginSection = pluginHooks.getConfigurationSection(pl);
                for (String mobName : Objects.requireNonNull(pluginSection).getKeys(false)) {
                    AbstractMobMessage mobMessage = MobMessage.getMobMessage(Objects.requireNonNull(pluginSection.getConfigurationSection(mobName)), this);
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

    public Map<String, TemplateMobMessage> getTemplateMobMessages() {
        return templateMobMessages;
    }

    public PluginMessage getPluginMessage() {
        return pluginMessage;
    }
}
