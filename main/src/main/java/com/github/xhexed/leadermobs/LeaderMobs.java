package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.commands.Commands;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class LeaderMobs extends JavaPlugin {
    public static File dataFile;
    public static FileConfiguration playerData;
    public static boolean broadcast;
    static boolean debug;
    static boolean papi;
    static boolean mvdw;
    private static LeaderMobs instance;
    static File debugfile;

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
            try {
                manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listeners.BossListener").newInstance(), this);
            } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (manager.isPluginEnabled("MythicMobs")) {
            logger.info("Found MythicMobs, hooking...");
            final String[] version = Objects.requireNonNull(manager.getPlugin("MythicMobs")).getDescription().getVersion().split("\\.");
            final int mainVersion = Integer.parseInt(version[0]);
            if (mainVersion < 4 || (mainVersion == 4 && Integer.parseInt(version[1]) < 9)) {
                logger.info("Found legacy version of MythicMobs (4.9.0-), hooking...");
                try {
                    manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listeners.LegacyMythicMobsListener").newInstance(), this);
                } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listeners.MythicMobsListener").newInstance(), this);
                } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
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

        try {
            ConfigUpdater.update(this, "config.yml", new File(getDataFolder(), "config.yml"), new ArrayList<>());
            ConfigUpdater.update(this, "rewards.yml", new File(getDataFolder(), "rewards.yml"), new ArrayList<>());
        }
        catch (final IOException e) { e.printStackTrace(); }

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

        final FileConfiguration config = getConfig();
        debug          = config.getBoolean("debug");
        broadcast = config.getBoolean("Messages.broadcast");

        if (debug) {
            final File Debugfolder = new File(getDataFolder() + "/debugs");
            if (!Debugfolder.exists())
                Debugfolder.mkdirs();

            final SimpleDateFormat df = new SimpleDateFormat("MM-dd-hh");
            final String file = "debug_" + df.format(new Date()) + ".txt";
            getLogger().info("Debug mode is on, out file: " + file);
            debugfile = new File(getDataFolder() + "/debugs", file);
            Utils.debugln("Server version: " + getServer().getVersion());
        }
    }

    @Override
    public void onDisable() {
        instance = null;
        HandlerList.unregisterAll(this);
    }
}
