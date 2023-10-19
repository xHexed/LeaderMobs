package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.command.CommandManager;
import com.github.xhexed.leadermobs.config.ConfigManager;
import com.github.xhexed.leadermobs.data.PlayerDataManager;
import com.github.xhexed.leadermobs.reward.RewardManager;
import com.github.xhexed.leadermobs.util.MessageManager;
import com.github.xhexed.leadermobs.util.TextMessageParser;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
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
        boolean found = false;
        if (manager.isPluginEnabled("MythicMobs")) {
            final String[] version = Objects.requireNonNull(manager.getPlugin("MythicMobs")).getDescription().getVersion().split("\\.");
            final int mainVersion = Integer.parseInt(version[0]);
            if (mainVersion < 4 || (mainVersion == 4 && Integer.parseInt(version[1]) < 9)) {
                getLogger().info("Found legacy version of MythicMobs (4.9.0-), hooking...");
                try {
                    manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listener.LegacyMythicMobsListener").getDeclaredConstructor(getClass()).newInstance(this), this);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                getLogger().info("Found MythicMobs, hooking...");
                try {
                    manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listener.MythicMobsListener").getDeclaredConstructor(getClass()).newInstance(this), this);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            found = true;
        }
        if (!found) {
            getLogger().warning("Couldn't find any custom mobs plugin...");
        }

        if (manager.isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("Found PlaceholderAPI, hooking...");
            papi = true;
        }

        reloadPlugin();
    }

    public void reloadPlugin() {
        configManager.reloadConfig();
        playerDataManager.reloadData();
        rewardManager.reloadData();
    }
}
