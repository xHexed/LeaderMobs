package com.github.xhexed.leadermobs.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface MessageSender {
    void sendMessage(Player player, String message);

    void sendMessage(CommandSender sender, String message);

    void sendActionbar(Player player, String message);

    void sendTitle(Player player, String title, String subTitle, long fadeIn, long stay, long fadeOut);
}
