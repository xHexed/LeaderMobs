package com.github.xhexed.leadermobs.utils;

import com.github.xhexed.leadermobs.data.MobDamageInfo;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.xhexed.leadermobs.LeaderMobs.*;

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
            string = PlaceholderAPI.setPlaceholders(player, string);
        if (mvdw)
            string = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
        return string;
    }

    public static void sendMessage(final String message) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (playerData.getBoolean(p.getName(), false)) return;
            p.sendMessage(replacePlaceholder(p, ChatColor.translateAlternateColorCodes('&', message)));
        });
    }

    public static void sendTitle(final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        if (playerData.getBoolean(player.getName(), false)) return;
        try {
            player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
        catch (final NoClassDefFoundError ignored) {
            Bukkit.getConsoleSender().sendMessage("Title message sent unsuccessful. Try using newer version.");
        }
    }

    public static void sendActionBar(final Player player, final String message) {
        if (playerData.getBoolean(player.getName(), false)) return;
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
        catch (final NoClassDefFoundError ignored) {
            Bukkit.getConsoleSender().sendMessage("Actionbar message sent unccessful. Try using spigot (or its forks) with newer version.");
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

    public static String replaceMobPlaceholder(String message, final MobDamageInfo info) {
        final Matcher m = PLACEHOLDER_PATTERN.matcher(message);
        while (m.find()) {
            final String params = m.group(2);
            message = message.replaceAll(Pattern.quote(m.group()), Matcher.quoteReplacement(onPlaceholderRequest(params, info)));
        }
        return message;
    }

    private static String onPlaceholderRequest(String params, final MobDamageInfo info) {
        if (params.startsWith("top_dealt_damage_")) {
            params = params.substring(15);

            final int pos;
            try { pos = Integer.parseInt(params); } catch (final NumberFormatException ignored) { return ""; }

            try { return DOUBLE_FORMAT.format(info.getTopDamageDealt().get(pos - 1).getKey()); }
            catch (final IndexOutOfBoundsException e) { return ""; }
        }
        if (params.startsWith("top_dealt_")) {
            params = params.substring(10);
			
            final int pos;
            try { pos = Integer.parseInt(params); } catch (final NumberFormatException ignored) { return ""; }

            try { return Bukkit.getOfflinePlayer(info.getTopDamageDealt().get(pos - 1).getValue()).getName(); }
            catch (final IndexOutOfBoundsException e) { return ""; }
        }
        if (params.startsWith("top_taken_damage_")) {
            params = params.substring(15);

            final int pos;
            try { pos = Integer.parseInt(params); } catch (final NumberFormatException ignored) { return ""; }

            try { return DOUBLE_FORMAT.format(info.getTopDamageTaken().get(pos - 1).getKey()); }
            catch (final IndexOutOfBoundsException e) { return ""; }
        }
        if (params.startsWith("top_taken_")) {
            params = params.substring(10);

            final int pos;
            try { pos = Integer.parseInt(params); } catch (final NumberFormatException ignored) { return ""; }

            try { return Bukkit.getOfflinePlayer(info.getTopDamageTaken().get(pos - 1).getValue()).getName(); }
            catch (final IndexOutOfBoundsException e) { return ""; }
        }
        return "";
    }

    public static String getMobDeathMessage(final MobDamageInfo info, final String mobname, String message) {
        message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
        message = replaceMobPlaceholder(message, info);
        message = replacePlaceholder(null, message);
        return message;
    }

    public static Entity getDamageSource(Entity entity) {
        if (entity instanceof Projectile) {
            final Entity shooter = (Entity) ((Projectile) entity).getShooter();
            if (shooter != null) entity = shooter;
        }
        if (entity instanceof TNTPrimed) {
            final Entity igniter = ((TNTPrimed) entity).getSource();
            if (igniter != null) entity = igniter;
        }
        return entity;
    }
}
