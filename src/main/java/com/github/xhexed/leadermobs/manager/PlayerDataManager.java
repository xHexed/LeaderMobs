package com.github.xhexed.leadermobs.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PlayerDataManager {
    private Plugin plugin;
    private File dataFile;
    private FileConfiguration playerData;

    public PlayerDataManager(Plugin plugin) {
        this.plugin = plugin;
        reloadData();
    }

    public void reloadData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playerData = YamlConfiguration.loadConfiguration(dataFile);
    }

    public File getDataFile() {
        return dataFile;
    }

    public FileConfiguration getPlayerData() {
        return playerData;
    }
}
