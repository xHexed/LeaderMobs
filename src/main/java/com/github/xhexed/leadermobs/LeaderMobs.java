package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.commands.Commands;
import com.github.xhexed.leadermobs.listeners.BossListener;
import com.github.xhexed.leadermobs.listeners.LegacyMythicMobsListener;
import com.github.xhexed.leadermobs.listeners.MythicMobsListener;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class LeaderMobs extends JavaPlugin {
    public static FileConfiguration playerData;
    public static boolean broadcast;
    static boolean debug;
    static boolean papi;
    static boolean mvdw;
    private static LeaderMobs instance;
    static File debugfile;

    public static LeaderMobs getInstance() { return instance; }

    private void updateConfig() {
        final FileConfiguration config = getConfig();
        switch (config.getInt("version", 0)) {
            case 1:
                config.addDefault("Messages.MobSpawn.title.title", "%mob_name% spawned");
                config.addDefault("Messages.MobSpawn.title.subTitle", "x: %x%, y: %y%, z: %z%");
                config.addDefault("Messages.MobSpawn.title.fadeIn", 1);
                config.addDefault("Messages.MobSpawn.title.stay", 1);
                config.addDefault("Messages.MobSpawn.title.fadeOut", 1);
                config.addDefault("Messages.MobSpawn.actionbar.message", "%mob_name% spawned");
                config.addDefault("Messages.MobDead.title.title", "%mob_name% spawned");
                config.addDefault("Messages.MobDead.title.subTitle", "x: %x%, y: %y%, z: %z%");
                config.addDefault("Messages.MobDead.title.fadeIn", 1);
                config.addDefault("Messages.MobDead.title.stay", 1);
                config.addDefault("Messages.MobDead.title.fadeOut", 1);
                config.addDefault("Messages.MobDead.actionbar.message", "%mob_name% spawned");
            case 2:
                //TODO: Update config :(
                config.set("version", 3);
                saveConfig();
        }
    }
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reload();
        updateConfig();
        
        final PluginManager manager = getServer().getPluginManager();
        final Logger logger = getLogger();
        if (manager.isPluginEnabled("Boss")) {
            logger.info("Found Boss, hooking...");
            manager.registerEvents(new BossListener(), this);
        }
        if (manager.isPluginEnabled("MythicMobs")) {
            logger.info("Found MythicMobs, hooking...");
            final String version = MythicMobs.inst().getDescription().getVersion();
            if (version.charAt(0) <= '4' && version.charAt(2) <= 9) {
                manager.registerEvents(new LegacyMythicMobsListener(), this);
            }
            else {
                manager.registerEvents(new MythicMobsListener(), this);
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
        reloadConfig();
        if (!new File(getDataFolder(), "rewards.yml").exists()) {
            saveResource("rewards.yml", true);
        }

        final File datafile = new File(getDataFolder(), "data.yml");
        if (!datafile.exists()) {
            try {
                datafile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        playerData = YamlConfiguration.loadConfiguration(datafile);

        final FileConfiguration config = getConfig();

        debug          = config.getBoolean("debug");
        broadcast = config.getBoolean("Messages.broadcast");

        if (debug) {
            final File Debugfolder = new File(getDataFolder() + "/debugs");
            if (!Debugfolder.exists())
                Debugfolder.mkdirs();

            try {
                final SimpleDateFormat df = new SimpleDateFormat("MM-dd-hh");
                final String file = "debug_" + df.format(new Date()) + ".txt";
                getLogger().info("Debug mode is on, out file: " + file);
                debugfile = new File(getDataFolder() + "/debugs", file);
                final BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(debugfile, true));
                writer.write(("Server version: " + getServer().getVersion() + '\n').getBytes());
                writer.flush();
                writer.close();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        instance = null;
        HandlerList.unregisterAll(this);
    }

}
