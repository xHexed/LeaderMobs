package com.github.xhexed.leadermobs.data;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PlayerDataManager {
    private Plugin plugin;
    private File dataFile;
    @Getter
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

    public void saveData() {
        try {
            playerData.save(dataFile);
            reloadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
