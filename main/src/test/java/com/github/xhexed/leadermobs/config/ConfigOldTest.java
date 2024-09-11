package com.github.xhexed.leadermobs.config;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.config.updater.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

@Test
public class ConfigOldTest {
    private final ConfigManager manager;
    private final YamlConfiguration defaultConfig;

    public ConfigOldTest() {
        manager = new ConfigManager(null, null);
        defaultConfig = YamlConfiguration.loadConfiguration(new File("src/main/resources", "config.yml"));
    }

    public void testVersionOne() {
        loadUpdatedConfig(new File("src/test/resources", "config-1.yml"));
        MobMessage message = manager.getMobMessage("MythicMobs", "test1", null);
        assertTrue(message.getMobConditions().get(0).getMobName().matcher("test2").matches());
        assertEquals(message.getPlayersRequired(), 1);
        assertEquals(message.getPlacesToBroadcast(), 10);
        assertEquals(message.getMobDeathMessage().getDamageDealtMessage().getDefaultPlacePrefix(), "§6");
        assertEquals(message.getMobDeathMessage().getDamageDealtMessage().getPlacePrefixes().get(1), "§a");
    }

    public void testVersionTwo() {
        loadUpdatedConfig(new File("src/test/resources", "config-2.yml"));
        MobMessage message = manager.getMobMessage("MythicMobs", "test", null);
        assertEquals(message.getTotalDamageRequirement().getDamageDealtRequired(), 0);
    }

    public void testVersionThree() {
        loadUpdatedConfig(new File("src/test/resources", "config-3.yml"));
        MobMessage message = manager.getMobMessage("MythicMobs", "test", null);
        assertEquals(message
                .getMobDeathMessage().getDamageTakenMessage().getTitleMessage().getStay(), 1);
    }

    public void testVersionFour() {
        loadUpdatedConfig(new File("src/test/resources", "config-4.yml"));
        assertEquals(manager.getPluginMessage().getToggleBroadcastOnMessage(), "test1");
        assertEquals(manager.getPluginMessage().getToggleBroadcastOffMessage(), "test2");
    }

    public void testVersionFive() {
        loadUpdatedConfig(new File("src/test/resources", "config-5.yml"));
        assertEquals(manager.getPluginMessage().getToggleBroadcastOnMessage(), "test3");
        assertEquals(manager.getPluginMessage().getToggleBroadcastOffMessage(), "test4");
    }


    public void testVersionSix() {
        loadUpdatedConfig(new File("src/test/resources", "config-6.yml"));
        assertEquals(manager.getPluginMessage().getToggleBroadcastOnMessage(), "test1");
        assertEquals(manager.getPluginMessage().getToggleBroadcastOffMessage(), "test2");
    }

    public void testVersionEight() {
        loadUpdatedConfig(new File("src/test/resources", "config-8.yml"));
        assertEquals(manager.getPluginMessage().getToggleBroadcastOnMessage(), "test1");
        assertEquals(manager.getPluginMessage().getToggleBroadcastOffMessage(), "test2");
    }

    private void loadUpdatedConfig(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigUpdater.updateConfig(config, defaultConfig);
        manager.loadConfig(config);
    }
}
