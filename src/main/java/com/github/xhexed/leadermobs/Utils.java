package com.github.xhexed.leadermobs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.LeaderMobs.*;
import static com.github.xhexed.leadermobs.listeners.MobListener.data;

public class Utils {
    public static final Pattern PLAYER_NAME = Pattern.compile("%player_name%");
    public static final Pattern NAME = Pattern.compile("%mob_name%");
    public static final Pattern PLACE_PREFIX = Pattern.compile("%place_prefix%");
    private static final Pattern POS_X = Pattern.compile("%x%");
    private static final Pattern POS_Y = Pattern.compile("%y%");
    private static final Pattern POS_Z = Pattern.compile("%z%");
    public static final Pattern DAMAGE_POS = Pattern.compile("%place%");
    public static final Pattern DAMAGE = Pattern.compile("%damage%");
    public static final Pattern PERCENTAGE = Pattern.compile("%percentage%");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%](mob_)([^%]+)[%]");
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##");

    public static float getPercentage(final Double damage, final Double health) { return (float) (damage / health * 100.0f); }

    public static String replacePlaceholder(final OfflinePlayer player, String string) {
        if (papi)
            string = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
        if (mvdw)
            string = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
        return string;
    }

    public static void sendMessage(final String message) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!playerData.getBoolean(p.getName(), true)) return;
            p.sendMessage(replacePlaceholder(p, ChatColor.translateAlternateColorCodes('&', message)));
        });
    }

    public static void sendTitle(final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        if (playerData.getBoolean(player.getName())) return;
        try {
            player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
        catch (final NoClassDefFoundError ignored) {
            Bukkit.getConsoleSender().sendMessage("Title message sent unsuccessful. Try using newer version.");
        }
    }

    public static void sendActionBar(final Player player, final String message) {
        if (playerData.getBoolean(player.getName())) return;
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
        catch (final NoClassDefFoundError ignored) {
            Bukkit.getConsoleSender().sendMessage("Actionbar message sent unccessful. Try using spigot (or its forks) with newer version.");
        }
    }

    public static String getMobSpawnMessage(final Entity entity, final String mobname, final int x, final int y, final int z, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        message = POS_X.matcher(message).replaceAll(Integer.toString(x));
        message = POS_Y.matcher(message).replaceAll(Integer.toString(y));
        message = POS_Z.matcher(message).replaceAll(Integer.toString(z));
        message = replaceMobPlaceholder(message, entity);
        message = replacePlaceholder(null, message);
        return message;
    }

    public static String replaceMobPlaceholder(String message, final Entity entity) {
        final Matcher m = PLACEHOLDER_PATTERN.matcher(message);
        while (m.find()) {
            final String params = m.group(2);
            debugConsole("Placeholder requested: " + params);
            message = message.replaceAll(Pattern.quote(m.group()), Matcher.quoteReplacement(onPlaceholderRequest(params, entity)));
        }
        return message;
    }

    private static String onPlaceholderRequest(String params, final Entity entity) {
        if (params.startsWith("top_dealt_")) {
            params = params.substring(params.indexOf("top_dealt_"));
            debugConsole(params);

            final int pos;
            try { pos = Integer.parseInt(params); } catch (final NumberFormatException ignored) { return ""; }

            try { return Bukkit.getOfflinePlayer(data.get(entity).getTopDamageDealt().get(pos).getValue()).getName(); }
            catch (final IndexOutOfBoundsException ignored) { return ""; }
        }
        if (params.startsWith("top_taken_")) {
            params = params.substring(params.indexOf("top_taken_"));
            debugConsole(params);

            final int pos;
            try { pos = Integer.parseInt(params); } catch (final NumberFormatException ignored) { return ""; }

            try { return Bukkit.getOfflinePlayer(data.get(entity).getTopDamageTaken().get(pos).getValue()).getName(); }
            catch (final IndexOutOfBoundsException ignored) { return ""; }
        }
        return "";
    }

    public static String getMobDeathMessage(final Entity entity, final String mobname, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        message = replaceMobPlaceholder(message, entity);
        message = replacePlaceholder(null, message);
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

    private static void debugConsole(final String text) {
        System.out.println(text);
    }
}
