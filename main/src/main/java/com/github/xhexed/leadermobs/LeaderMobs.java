package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.command.CommandManager;
import com.github.xhexed.leadermobs.config.ConfigManager;
import com.github.xhexed.leadermobs.data.PlayerDataManager;
import com.github.xhexed.leadermobs.reward.RewardManager;
import com.github.xhexed.leadermobs.util.MessageManager;
import com.github.xhexed.leadermobs.util.TextMessageParser;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public class LeaderMobs extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private TextMessageParser messageParser;
    private MessageManager messageManager;
    private RewardManager rewardManager;
    public boolean papi;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        messageParser = new TextMessageParser(this);
        messageManager = new MessageManager(this);
        rewardManager = new RewardManager(this);

        PluginCommand command = Objects.requireNonNull(getCommand("lm"));
        CommandManager commandManager = new CommandManager(this);
        command.setExecutor(commandManager);
        command.setTabCompleter(commandManager);

        PluginManager manager = getServer().getPluginManager();
        registerHooks();
        if (manager.isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("Found PlaceholderAPI");
            papi = true;
        }

        reloadPlugin();
    }

    private void registerHooks() {
        PluginManager manager = getServer().getPluginManager();
        boolean found = false;
        if (manager.isPluginEnabled("MythicMobs")) {
            final String[] version = Objects.requireNonNull(manager.getPlugin("MythicMobs")).getDescription().getVersion().split("\\.");
            final int mainVersion = Integer.parseInt(version[0]);
            if (mainVersion < 4 || (mainVersion == 4 && Integer.parseInt(version[1]) < 9)) {
                getLogger().info("Found legacy version of MythicMobs (4.9.0-)");
                registerPluginHook("com.github.xhexed.leadermobs.listener.LegacyMythicMobsListener");
            }
            else {
                getLogger().info("Found MythicMobs");
                registerPluginHook("com.github.xhexed.leadermobs.listener.MythicMobsListener");
            }
            found = true;
        }
        if (manager.isPluginEnabled("EliteMobs")) {
            getLogger().info("Found EliteMobs");
            registerPluginHook("com.github.xhexed.leadermobs.listener.EliteMobsListener");
            found = true;
        }
        if (!found) {
            getLogger().warning("Couldn't find any custom mobs plugin...");
        }
    }

    private void registerPluginHook(String className) {
        try {
            Class.forName(className).getDeclaredConstructor(getClass()).newInstance(this);
        } catch (final Exception e) {
            getLogger().fine("Error while registering hook: " + e);
        }
    }


    public void reloadPlugin() {
        configManager.reloadConfig();
        playerDataManager.reloadData();
        rewardManager.reloadData();
    }
}
