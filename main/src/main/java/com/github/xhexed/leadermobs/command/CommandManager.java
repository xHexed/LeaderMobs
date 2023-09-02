package com.github.xhexed.leadermobs.command;

import com.github.xhexed.leadermobs.LeaderMobs;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private LeaderMobs plugin;
    private List<String> commands = Collections.singletonList("reload");
    private List<String> playerCommands = new ArrayList<>(commands);

    public CommandManager(LeaderMobs plugin) {
        this.plugin = plugin;
        playerCommands.add("toggle");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 0) {
                sender.sendMessage("§7Usage: /lm reload");
            }
            else if (args.length == 1) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    sender.sendMessage("§aReloading plugin...");
                    plugin.reloadPlugin();
                    return true;
                }
            }
        }
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage("§7Usage: /lm <reload/toggle>");
            }
            else if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "reload": {
                        if (!sender.hasPermission("lm.reload")) {
                            sender.sendMessage("§cYou don't have permission!");
                            return true;
                        }
                        sender.sendMessage("§aReloading plugin...");
                        plugin.reloadPlugin();
                        return true;
                    }
                    case "toggle": {
                        if (!sender.hasPermission("lm.toggle")) {
                            sender.sendMessage(plugin.getConfigManager().getPluginMessage().getNoPermissionMessage());
                            return true;
                        }
                        FileConfiguration config = plugin.getPlayerDataManager().getPlayerData();
                        if (!config.contains(sender.getName()) || config.getBoolean(sender.getName())) {
                            sender.sendMessage(plugin.getConfigManager().getPluginMessage().getToggleBroadcastOffMessage());
                            config.set(sender.getName(), false);
                        }
                        else {
                            sender.sendMessage(plugin.getConfigManager().getPluginMessage().getToggleBroadcastOnMessage());
                            config.set(sender.getName(), true);
                        }

                        plugin.getPlayerDataManager().saveData();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length > 1) {
            return null;
        }

        Collection<String> commandList = commands;
        if (commandSender instanceof Player) {
            commandList = playerCommands;
        }

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(strings[0], commandList, completions);
        Collections.sort(completions);

        return completions;
    }
}
