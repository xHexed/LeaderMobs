package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.data.MobDamageInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.LeaderMobs.*;
import static com.github.xhexed.leadermobs.listeners.MobListener.data;

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
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%](lm_)([^%]+)[%]");

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

    public static String getMobSpawnMessage(final Entity entity, final String mobname, final int x, final int y, final int z, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        message = POS_X.matcher(message).replaceAll(Integer.toString(x));
        message = POS_Y.matcher(message).replaceAll(Integer.toString(y));
        message = POS_Z.matcher(message).replaceAll(Integer.toString(z));
        message = replacePlaceholder(null, message);
        message = replaceMobPlaceholder(message, entity);
        return message;
    }

    public static String replaceMobPlaceholder(String message, final Entity entity) {
        final Matcher m = PLACEHOLDER_PATTERN.matcher(message);
        while (m.find()) {
            final String format = m.group(1);
            final int index = format.indexOf("_");
            if (index <= 0 || index >= format.length()) continue;
            final String params = format.substring(index + 1);
            debugConsole(params);
            message = message.replaceAll(Pattern.quote(m.group()), Matcher.quoteReplacement(onPlaceholderRequest(params, entity)));
        }
        return message;
    }

    public static String onPlaceholderRequest(String params, final Entity entity) {
        if (params.startsWith("top_dealt_")) {
            params = params.substring(params.indexOf("top_dealt_"));
            debugConsole(params);

            final int pos;
            try {
                pos = Integer.parseInt(params);
            }
            catch (final NumberFormatException ignored) {
                return "";
            }

            final MobDamageInfo info = data.get(entity);
            final Map<String, Double> list = info.getDamageDealt();
            //TODO: Get top players
        }
        return params;
    }

    public static String getMobDeathMessage(final Entity entity, final String mobname, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        message = replacePlaceholder(null, message);
        message = replaceMobPlaceholder(message, entity);
        return message;
    }

    public static void debug(final String text) {
        if (!debug) return;
        try (final BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(debugfile, true))) {
            writer.write(('\n' + text).getBytes());
            writer.flush();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void debugConsole(final String text) {
        System.out.println(text);
    }
}
