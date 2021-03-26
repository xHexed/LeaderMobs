package com.github.xhexed.leadermobs.util;

import com.github.xhexed.leadermobs.data.MobDamageTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static Pattern PLAYER_NAME = Pattern.compile("%player_name%");
    public static Pattern NAME = Pattern.compile("%mob_name%");
    public static Pattern PLACE_PREFIX = Pattern.compile("%place_prefix%");
    public static Pattern POS_X = Pattern.compile("%x%");
    public static Pattern POS_Y = Pattern.compile("%y%");
    public static Pattern POS_Z = Pattern.compile("%z%");
    public static Pattern DAMAGE_POS = Pattern.compile("%place%");
    public static Pattern DAMAGE = Pattern.compile("%damage%");
    public static Pattern DAMAGE_DEALT = Pattern.compile("%damage_dealt%");
    public static Pattern DAMAGE_TAKEN = Pattern.compile("%damage_taken%");
    public static Pattern DAMAGE_DEALT_PERCENTAGE = Pattern.compile("%damage_dealt_percentage%");
    public static Pattern DAMAGE_TAKEN_PERCENTAGE = Pattern.compile("%damage_taken_percentage%");
    public static Pattern PERCENTAGE = Pattern.compile("%percentage%");
    private static Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%](mob_)([^%]+)[%]");
    public static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##");

    public static float getPercentage(Double damage, Double health) { return (float) (damage / health * 100.0f); }

    public static String replaceMobPlaceholder(String message, MobDamageTracker info) {
        Matcher m = PLACEHOLDER_PATTERN.matcher(message);
        while (m.find()) {
            String params = m.group(2);
            message = message.replaceAll(Pattern.quote(m.group()), Matcher.quoteReplacement(onPlaceholderRequest(params, info)));
        }
        return message;
    }

    private static String onPlaceholderRequest(String params, MobDamageTracker info) {
        if (params.startsWith("top_dealt_damage_")) {
            params = params.substring(15);

            int pos;
            try { pos = Integer.parseInt(params); } catch (NumberFormatException ignored) { return ""; }

            try { return DOUBLE_FORMAT.format(info.getTopDamageDealt().get(pos - 1).getTotalDamage()); }
            catch (IndexOutOfBoundsException e) { return ""; }
        }
        if (params.startsWith("top_dealt_")) {
            params = params.substring(10);
			
            int pos;
            try { pos = Integer.parseInt(params); } catch (NumberFormatException ignored) { return ""; }

            try { return Bukkit.getOfflinePlayer(info.getTopDamageDealt().get(pos - 1).getTracker()).getName(); }
            catch (IndexOutOfBoundsException e) { return ""; }
        }
        if (params.startsWith("top_taken_damage_")) {
            params = params.substring(15);

            int pos;
            try { pos = Integer.parseInt(params); } catch (NumberFormatException ignored) { return ""; }

            try { return DOUBLE_FORMAT.format(info.getTopDamageTaken().get(pos - 1).getTotalDamage()); }
            catch (IndexOutOfBoundsException e) { return ""; }
        }
        if (params.startsWith("top_taken_")) {
            params = params.substring(10);

            int pos;
            try { pos = Integer.parseInt(params); } catch (NumberFormatException ignored) { return ""; }

            try { return Bukkit.getOfflinePlayer(info.getTopDamageTaken().get(pos - 1).getTracker()).getName(); }
            catch (IndexOutOfBoundsException e) { return ""; }
        }
        return "";
    }

    public static Entity getDamageSource(Entity entity) {
        if (entity instanceof Projectile) {
            Entity shooter = (Entity) ((Projectile) entity).getShooter();
            if (shooter != null) entity = shooter;
        }
        if (entity instanceof TNTPrimed) {
            Entity igniter = ((TNTPrimed) entity).getSource();
            if (igniter != null) entity = igniter;
        }
        return entity;
    }
}
