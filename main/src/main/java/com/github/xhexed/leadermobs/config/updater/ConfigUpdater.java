package com.github.xhexed.leadermobs.config.updater;

import com.github.xhexed.leadermobs.config.updater.impl.NewConfigUpdater;
import com.github.xhexed.leadermobs.config.updater.impl.OldConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigUpdater {
    static void updateConfig(FileConfiguration config, FileConfiguration defaultConfig) {
        if (config.contains("Messages")) {
            OldConfigUpdater.INSTANCE.update(config, defaultConfig);
        }
        if (config.getInt("config-version", 0) < 2) {
            NewConfigUpdater.INSTANCE.update(config, defaultConfig);
        }
    }

    void update(FileConfiguration config, FileConfiguration defaultConfig);
}
