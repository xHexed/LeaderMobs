package com.github.xhexed.leadermobs.config;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.updater.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

@Test
public class ConfigNewTest {
    private final ConfigManager manager;
    private final YamlConfiguration defaultConfig;

    public ConfigNewTest() {
        manager = new ConfigManager(null, null);
        defaultConfig = YamlConfiguration.loadConfiguration(new File("src/main/resources", "config.yml"));
    }

    public void testNewConfig() {
        File configFile = new File("src/test/resources", "config-new.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigUpdater.updateConfig(config, defaultConfig);
        manager.loadConfig(config);
        MobMessage message = manager.getMobMessage("MythicMobs", "test", null);
        assertTrue(message
                .getMobConditions().stream().anyMatch(condition -> condition.getMobName().matcher("test").matches()));
        //TODO: Add more tests :(
    }
}
