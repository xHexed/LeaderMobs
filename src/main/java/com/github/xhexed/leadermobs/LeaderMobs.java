package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.commands.Commands;
import com.github.xhexed.leadermobs.listeners.BossListener;
import com.github.xhexed.leadermobs.listeners.MythicMobsListener;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class LeaderMobs extends JavaPlugin {
    public static File dataFile;
    public static FileConfiguration playerData;
    public static FileConfiguration rewards;
    public static boolean papi;
    public static boolean mvdw;
    private static LeaderMobs instance;

    public static LeaderMobs getInstance() { return instance; }
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reload();

        final PluginManager manager = getServer().getPluginManager();
        final Logger logger = getLogger();
        if (manager.isPluginEnabled("Boss")) {
            logger.info("Found Boss, hooking...");
            manager.registerEvents(new BossListener(), this);
        }
        if (manager.isPluginEnabled("MythicMobs")) {
            logger.info("Found MythicMobs, hooking...");
            manager.registerEvents(new MythicMobsListener(), this);
        }
        else {
            logger.severe("Didn't found any hookable mobs plugin, disabling..");
            manager.disablePlugin(this);
            return;
        }

        if (manager.isPluginEnabled("PlaceholderAPI")) {
            logger.info("Found PlaceholderAPI, hooking...");
            papi = true;
        }
        if (manager.isPluginEnabled("MVdWPlaceholderAPI")) {
            logger.info("Found MVdWPlaceholderAPI, hooking...");
            mvdw = true;
        }

        final PluginCommand command = Objects.requireNonNull(getCommand("lm"));
        command.setExecutor(new Commands());
        command.setTabCompleter(new Commands());
    }

    public void reload() {
        if (!new File(getDataFolder(), "rewards.yml").exists()) {
            saveResource("rewards.yml", true);
        }
        final File rewardFile = new File(getDataFolder(), "rewards.yml");
        rewards = YamlConfiguration.loadConfiguration(rewardFile);

        final FileConfiguration config = getConfig();
        if (config.getBoolean("auto-update", true)) {
            try {
                ConfigUpdater.update(this, "config.yml", new File(getDataFolder(), "config.yml"), new ArrayList<>());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        reloadConfig();

        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        playerData = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
