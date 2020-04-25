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

    public static void onMobSpawn(final String mobName, final int x, final int y, final int z) {
        final FileConfiguration config = getInstance().getConfig();
        if (!LeaderMobs.broadcast) return;
        for (String message : config.getStringList("Messages.MobSpawn.messages")) {
            message = getMobSpawnMessage(mobName, x, y, z, message);
            sendMessage(message);
        }

        Bukkit.getOnlinePlayers().forEach((p) -> {
            sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(mobName, x, y, z, config.getString("Messages.MobSpawn.title.title", ""))),
                    ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(mobName, x, y, z, config.getString("Messages.MobSpawn.title.subTitle", ""))),
                    config.getInt("Messages.MobSpawn.title.fadeIn", 0),
                    config.getInt("Messages.MobSpawn.title.stay", 0),
                    config.getInt("Messages.MobSpawn.title.fadeOut", 0));
            sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(mobName, x, y, z, config.getString("Messages.MobSpawn.actionbar.message", ""))));
        });
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

    public static void onMobDeath(final Entity entity, final String mobName, final String internalName, final double health) {
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
        header = NAME.matcher(header != null ? header : "").replaceAll(ChatColor.stripColor(mobName));
        header = replacePlaceholder(null, header);
        sendMessage(header);

        final HashMap<Integer, String> rewards = new HashMap<>();
        final String mainMessage = config.getString("Messages.MobDead.message", "");
        for (final Double dam : pos) {
            if (place >= config.getInt("PlacesToBroadcast")) break;
            rewards.put(place, damageToValue.get(dam));
            String message = mainMessage;
            message = PLACE_PREFIX.matcher(message != null ? message : "").replaceAll(config.getString(config.contains("PlacePrefix." + place) ? "PlacePrefix." + place : "PlacePrefix.default", ""));
            message = DAMAGE_POS.matcher(message).replaceAll(Integer.toString(place));
            final String name = damageToValue.get(dam);
            message = PLAYER_NAME.matcher(message).replaceAll(name);
            final DecimalFormat format = new DecimalFormat("#.##");
            message = DAMAGE.matcher(message).replaceAll(format.format(dam));
            getInstance().debug(dam + " " + health);
            message = PERCENTAGE.matcher(message).replaceAll(format.format(getPercentage(dam, health)));
            message = replacePlaceholder(Bukkit.getPlayer(name), message);
            sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            ++place;
        }

        String footer = config.getString("Messages.MobDead.footer", "");
        footer = NAME.matcher(footer != null ? footer : "").replaceAll(ChatColor.stripColor(mobName));
        footer = replacePlaceholder(null, footer);
        sendMessage(footer);

        Bukkit.getOnlinePlayers().forEach((p) -> {
            sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(mobName, config.getString("Messages.MobDead.title.title", ""))),
                    ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(mobName, config.getString("Messages.MobDead.title.subTitle", ""))),
                    config.getInt("Messages.MobDead.title.fadeIn", 0),
                    config.getInt("Messages.MobDead.title.stay", 0),
                    config.getInt("Messages.MobDead.title.fadeOut", 0));
            sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(mobName, config.getString("Messages.MobDead.actionbar.message", ""))));
        });

        new Reward(internalName, rewards);
        getInstance().debug("Final data: " + data);
        data.remove(entity);
    }
}
