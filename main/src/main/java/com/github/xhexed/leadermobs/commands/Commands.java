package com.github.xhexed.leadermobs.commands;

import com.github.xhexed.leadermobs.LeaderMobs;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    private String c(final String text) { return ChatColor.translateAlternateColorCodes('&', text); }
    private static final List<String> commands = Collections.singletonList("reload");
    private static final List<String> playerCommands = Collections.singletonList("toggle");

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 0) {
                sender.sendMessage("§7Usage: /lm reload");
            }
            else if (args.length == 1) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    sender.sendMessage("§aReloading plugin...");
                    LeaderMobs.getInstance().reload();
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
                        LeaderMobs.getInstance().reload();
                        return true;
                    }
                    case "toggle": {
                        if (!sender.hasPermission("lm.toggle")) {
                            sender.sendMessage("§cYou don't have permission!");
                            return true;
                        }
                        final FileConfiguration cnf = LeaderMobs.playerData;
                        if (!cnf.contains(sender.getName()) || cnf.getBoolean(sender.getName())) {
                            sender.sendMessage(c(LeaderMobs.getInstance().getConfig().getString("Messages.toggle.false")));
                            cnf.set(sender.getName(), false);
                        }
                        else {
                            sender.sendMessage(c(LeaderMobs.getInstance().getConfig().getString("Messages.toggle.true")));
                            cnf.set(sender.getName(), true);
                        }

                        try { cnf.save(LeaderMobs.dataFile); } catch (final IOException e) { e.printStackTrace(); }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final @NotNull CommandSender commandSender, final @NotNull Command command, final @NotNull String s, final String[] strings) {
        if (strings.length > 1) {
            return null;
        }

        final Collection<String> commandList = new ArrayList<>(commands);
        if (commandSender instanceof Player) {
            commandList.addAll(playerCommands);
        }

        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(strings[0], commandList, completions);
        Collections.sort(completions);

        return completions;
    }
}
