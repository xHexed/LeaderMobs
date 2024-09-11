package com.github.xhexed.leadermobs.config;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.checker.MobChecker;
import com.github.xhexed.leadermobs.config.updater.ConfigUpdater;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class ConfigManager {
    @Getter(value = AccessLevel.NONE)
    private LeaderMobs plugin;

    @Getter(value = AccessLevel.PACKAGE)
    private final Set<MobMessage> mobMessages = new HashSet<>();

    private final MobChecker mobChecker = new MobChecker();
    private PluginMessage pluginMessage;
    private final File file;

    public ConfigManager(LeaderMobs plugin) {
        this(plugin, new File(plugin.getDataFolder(), "config.yml"));
    }

    ConfigManager(LeaderMobs plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    public void reloadConfig() {
        if (!file.exists())
            plugin.saveResource("config.yml", false);
        YamlConfiguration config = checkConfigUpdates();
        if (config.getBoolean("auto-update", true)) {
            try {
                com.tchristofferson.configupdater.ConfigUpdater.update(plugin, "config.yml", file, "mob-messages");
            } catch (IOException e) {
                plugin.getLogger().fine("Error: " + e);
            }
        }
        loadConfig(config);
    }

    public MobMessage getMobMessage(String plugin, String mobName, Entity entity) {
        for (MobMessage mobMessage : mobMessages) {
            if (!mobChecker.check(mobMessage, plugin, mobName, entity))
                continue;
            return mobMessage;
        }
        return null;
    }

    void loadConfig(ConfigurationSection config) {
        mobMessages.clear();
        ConfigurationSection mobs = config.getConfigurationSection("mob-messages");
        if (mobs == null) return;
        mobs.getKeys(false).forEach((pl -> {
            MobMessage mobMessage = new MobMessage(plugin, mobs.getConfigurationSection(pl));
            if (mobMessage.getMobConditions() != null) {
                mobMessages.add(mobMessage);
            }
        }));
        pluginMessage = new PluginMessage(Objects.requireNonNull(config.getConfigurationSection("plugin-messages")));
    }

    YamlConfiguration checkConfigUpdates() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.getBoolean("auto-update", true) ||
            config.getInt("config-version", 0) == 2 //TODO: Remove this magic value
        ) return config;
        File backupFolder = new File(file.getParent(), "config-backup");
        if (backupFolder.exists() || backupFolder.mkdir()) {
            try {
                File tempFile = File.createTempFile("config", ".yml", backupFolder);
                FileUtil.copy(file, tempFile);
                ConfigUpdater.updateConfig(config, plugin.getConfig());
                config.save(file);
            } catch (Exception e) {
                plugin.getLogger().fine("Failed to create backup folder... aborting auto update:" + e);
            }
        }
        return config;
    }
}
