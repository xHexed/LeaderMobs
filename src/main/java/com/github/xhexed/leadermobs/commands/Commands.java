package com.github.xhexed.leadermobs.commands;

import com.github.xhexed.leadermobs.LeaderMobs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Commands implements CommandExecutor {
    private String c(final String text) { return ChatColor.translateAlternateColorCodes('&', text); }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 0) {
                sender.sendMessage(c("&7Usage: /lm reload"));
            }
            else if (args.length == 1) {
                if ("reload".equalsIgnoreCase(args[0])) {
                    sender.sendMessage(c("&aReloading plugin..."));
                    LeaderMobs.getInstance().reload();
                    return true;
                }
            }
        }
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(c("&7Usage: /lm <reload/toggle>"));
            }
            else if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "reload": {
                        if (!sender.hasPermission("lm.reload")) {
                            sender.sendMessage(c("&cYou don't have permission!"));
                            return true;
                        }
                        sender.sendMessage(c("&aReloading plugin..."));
                        LeaderMobs.getInstance().reload();
                        return true;
                    }
                    case "toggle": {
                        if (!sender.hasPermission("lm.toggle")) {
                            sender.sendMessage(c("&cYou don't have permission!"));
                            return true;
                        }
                        final YamlConfiguration cnf = YamlConfiguration.loadConfiguration(LeaderMobs.playerdata);
                        if (cnf.getString(sender.getName()) == null) {
                            sender.sendMessage(c(LeaderMobs.getInstance().getConfig().getString("Messages.ToggleOFF")));
                            cnf.set(sender.getName(), false);
                            try {
                                cnf.save(LeaderMobs.playerdata);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        if (cnf.getBoolean(sender.getName())) {
                            sender.sendMessage(c(LeaderMobs.getInstance().getConfig().getString("Messages.ToggleOFF")));
                            cnf.set(sender.getName(), false);
                        }
                        else {
                            sender.sendMessage(c(LeaderMobs.getInstance().getConfig().getString("Messages.ToggleON")));
                            cnf.set(sender.getName(), true);
                        }
                        try {
                            cnf.save(LeaderMobs.playerdata);
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return true;
    }
}
