package com.github.xhexed.leadermobs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.LeaderMobs.*;

public class Utils {
    public static final Pattern LEVEL = Pattern.compile("%mob_level%");
    public static final Pattern PLAYER_NAME = Pattern.compile("%player_name%");
    public static final Pattern NAME = Pattern.compile("%mob_name%");
    public static final Pattern PLACE_PREFIX = Pattern.compile("%place_prefix%");
    public static final Pattern POS_X = Pattern.compile("%x%");
    public static final Pattern POS_Y = Pattern.compile("%y%");
    public static final Pattern POS_Z = Pattern.compile("%z%");
    public static final Pattern DAMAGE_POS = Pattern.compile("%place%");
    public static final Pattern DAMAGE = Pattern.compile("%damage%");
    public static final Pattern PERCENTAGE = Pattern.compile("%percentage%");

    public static float getPercentage(final Double damage, final Double health) { return (float) (damage / health * 100.0f); }

    public static String replacePlaceholder(final OfflinePlayer player, String string) {
        if (papi)
            string = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
        if (mvdw)
            string = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
        return string;
    }

    public static void sendMessage(final String message) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final YamlConfiguration datacf = YamlConfiguration.loadConfiguration(playerdata);
            if (datacf.getString(p.getName()) != null && !datacf.getBoolean(p.getName())) continue;
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
