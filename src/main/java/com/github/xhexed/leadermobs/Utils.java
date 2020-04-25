package com.github.xhexed.leadermobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.LeaderMobs.*;

public class Utils {
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

    public static void sendTitle(final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        try {
            player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
        catch (final NoClassDefFoundError ignored) {
            Bukkit.getConsoleSender().sendMessage("Tried to send title message but not successful. Try using newer version.");
        }
    }

    public static void sendActionBar(final Player player, final String message) {
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
        catch (final NoClassDefFoundError ignored) {
            Bukkit.getConsoleSender().sendMessage("Tried to send actionbar message but not successful. Try using spigot with newer version.");
        }
    }

    public static String getMobSpawnMessage(final String mobname, final int x, final int y, final int z, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        message = POS_X.matcher(message).replaceAll(Integer.toString(x));
        message = POS_Y.matcher(message).replaceAll(Integer.toString(y));
        message = POS_Z.matcher(message).replaceAll(Integer.toString(z));
        message = replacePlaceholder(null, message);
        return message;
    }

    public static String getMobDeathMessage(final String mobname, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        return message;
    }
}
