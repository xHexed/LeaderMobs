package com.github.xhexed.leadermobs.listeners;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import java.text.DecimalFormat;
import java.util.*;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.Utils.*;

public class MobListener {
    public static final Map<Entity, HashMap<String, Double>> data = new HashMap<>();

    public static void onMobSpawn(final String mobname, final int x, final int y, final int z) {
        if (!LeaderMobs.broadcast) return;
        for (String message : getInstance().getConfig().getStringList("Messages.MobSpawn.messages")) {
            message = NAME.matcher(message).replaceAll(ChatColor.stripColor(mobname));
            message = POS_X.matcher(message).replaceAll(Integer.toString(x));
            message = POS_Y.matcher(message).replaceAll(Integer.toString(y));
            message = POS_Z.matcher(message).replaceAll(Integer.toString(z));
            message = replacePlaceholder(null, message);
            sendMessage(message);
        }
    }

    public static void onMobDamage(final AnimalTamer player, final Entity entity, final Double damageBase) {
        final HashMap<String, Double> mapPlayerDamage = data.containsKey(entity) ? data.get(entity) : new HashMap<>();
        final double damageFinal = Math.min(((Damageable) entity).getHealth(), damageBase);
        if (mapPlayerDamage.containsKey(player.getName())) {
            mapPlayerDamage.put(player.getName(), mapPlayerDamage.get(player.getName()) + damageFinal);
        }
        else {
            mapPlayerDamage.put(player.getName(), damageFinal);
        }
        data.put(entity, mapPlayerDamage);
    }

    public static void onMobDeath(final Entity entity, String mob_name, final double level, final String internal_name, final double health) {
        final FileConfiguration config = getInstance().getConfig();
        if (!data.containsKey(entity) || data.get(entity).keySet().size() < config.getInt("PlayersRequired")) return;
        final HashMap<Double, String> damageToValue = new HashMap<>();
        final List<Double> pos = new ArrayList<>();
        for (final String p : data.get(entity).keySet()) {
            final Double damage = data.get(entity).get(p);
            damageToValue.put(damage, p);
            pos.add(damage);
        }
        Collections.sort(pos);
        Collections.reverse(pos);
        int place = 1;
        String header = config.getString("Messages.MobDead.header", "");
        String footer = config.getString("Messages.MobDead.footer", "");
        mob_name = LEVEL.matcher(mob_name).replaceAll(Double.toString(level));
        header = NAME.matcher(header != null ? header : "").replaceAll(ChatColor.stripColor(mob_name));
        header = replacePlaceholder(null, header);
        final HashMap<Integer, String> rewards = new HashMap<>();
        sendMessage(header);
        final String mainMessage = config.getString("Messages.MobDead.message", "");
        for (final Double dam : pos) {
            String msgCopy = mainMessage;
            msgCopy = PLACE_PREFIX.matcher(msgCopy != null ? msgCopy : "").replaceAll(config.getString(config.contains("PlacePrefix." + place) ? "PlacePrefix." + place : "PlacePrefix.default", ""));
            msgCopy = DAMAGE_POS.matcher(msgCopy).replaceAll(Integer.toString(place));
            final String name = damageToValue.get(dam);
            msgCopy = PLAYER_NAME.matcher(msgCopy).replaceAll(name);
            final DecimalFormat format = new DecimalFormat("#.##");
            msgCopy = DAMAGE.matcher(msgCopy).replaceAll(format.format(dam));
            getInstance().debug(dam + "" + health);
            msgCopy = PERCENTAGE.matcher(msgCopy).replaceAll(format.format(getPercentage(dam, health)));
            msgCopy = replacePlaceholder(Bukkit.getPlayer(name), msgCopy);
            sendMessage(ChatColor.translateAlternateColorCodes('&', msgCopy));
            if (place == config.getInt("PlacesToBroadcast")) break;
            rewards.put(place, damageToValue.get(dam));
            ++place;
        }
        footer = NAME.matcher(footer != null ? footer : "").replaceAll(ChatColor.stripColor(mob_name));
        footer = replacePlaceholder(null, footer);
        sendMessage(footer);
        new Reward(internal_name, rewards);
        getInstance().debug("Data:" + data);
        data.remove(entity);
    }
}
