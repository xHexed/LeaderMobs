package com.github.xhexed.leadermobs.util;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.MobData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TextMessageParser {
    private LeaderMobs plugin;

    public TextMessageParser(LeaderMobs plugin) {
        this.plugin = plugin;
    }

    public String parseMobEventMessage(String message, MobData data, Player player) {
        message = PlaceholderParser.NAME.matcher(message).replaceAll(ChatColor.stripColor(data.getName()));
        message = PlaceholderParser.POS_X.matcher(message).replaceAll(Integer.toString(data.getEntity().getLocation().getBlockX()));
        message = PlaceholderParser.POS_Y.matcher(message).replaceAll(Integer.toString(data.getEntity().getLocation().getBlockY()));
        message = PlaceholderParser.POS_Z.matcher(message).replaceAll(Integer.toString(data.getEntity().getLocation().getBlockZ()));
        message = replacePlaceholder(player, message);
        return message;
    }

    public String replacePlaceholder(OfflinePlayer player, String string) {
        if (plugin.papi)
            string = PlaceholderAPI.setPlaceholders(player, string);
        return string;
    }
}
