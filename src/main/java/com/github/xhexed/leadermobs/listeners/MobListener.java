package com.github.xhexed.leadermobs.listeners;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.Reward;
import com.github.xhexed.leadermobs.data.MobDamageInfo;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.Utils.*;

public class MobListener {
    public static final Map<Entity, MobDamageInfo> data = new HashMap<>();

    public static void onMobSpawn(final Entity entity, final String mobName) {
        final Location location = entity.getLocation();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();

        final FileConfiguration config = getInstance().getConfig();
        if (!LeaderMobs.broadcast) return;
        for (String message : config.getStringList("Messages.MobSpawn.messages")) {
            message = getMobSpawnMessage(entity, mobName, x, y, z, message);
            sendMessage(message);
        }

        Bukkit.getOnlinePlayers().forEach((p) -> {
            sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(entity, mobName, x, y, z, config.getString("Messages.MobSpawn.title.title", ""))),
                    ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(entity, mobName, x, y, z, config.getString("Messages.MobSpawn.title.subTitle", ""))),
                    config.getInt("Messages.MobSpawn.title.fadeIn", 0),
                    config.getInt("Messages.MobSpawn.title.stay", 0),
                    config.getInt("Messages.MobSpawn.title.fadeOut", 0));
            sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(entity, mobName, x, y, z, config.getString("Messages.MobSpawn.actionbar.message", ""))));
        });
    }

    public static void onPlayerDamage(final Player player, final Entity entity, final Double damage) {
        final Map<String, Double> damageDealtList = data.containsKey(entity) ? data.get(entity).getDamageDealt() : new HashMap<>();
        final double damageFinal = Math.min(((Damageable) entity).getHealth(), damage);
        if (damageDealtList.containsKey(player.getName())) {
            damageDealtList.put(player.getName(), damageDealtList.get(player.getName()) + damageFinal);
        }
        else {
            damageDealtList.put(player.getName(), damageFinal);
        }
        data.put(entity, new MobDamageInfo(damageDealtList, new HashMap<>()));
    }

    public static void onMobDamage(final Entity entity, final Player player, final Double damage) {
        final Map<String, Double> damageTakenList = data.containsKey(entity) ? data.get(entity).getDamageTaken() : new HashMap<>();
        final double damageFinal = Math.min(((Damageable) entity).getHealth(), damage);
        if (damageTakenList.containsKey(player.getName())) {
            damageTakenList.put(player.getName(), damageTakenList.get(player.getName()) + damageFinal);
        }
        else {
            damageTakenList.put(player.getName(), damageFinal);
        }
        data.put(entity, new MobDamageInfo(new HashMap<>(), damageTakenList));
    }

    public static void onMobDeath(final Entity entity, final String mobName, final String internalName, final double health) {
        final FileConfiguration config = getInstance().getConfig();
        if (!data.containsKey(entity)) return;
        final MobDamageInfo damageInfo = data.get(entity);
        if (damageInfo.getDamageDealt().keySet().size() < config.getInt("PlayersRequired")) return;

        String header = config.getString("Messages.MobDead.damageDealt.header", "");
        header = NAME.matcher(header != null ? header : "").replaceAll(ChatColor.stripColor(mobName));
        header = replacePlaceholder(null, header);
        sendMessage(header);

        final List<Pair<Double, String>> damageDealtList = new ArrayList<>();
        damageInfo.getDamageDealt().forEach((p, d) -> damageDealtList.add(new Pair<>(d, p)));
        damageDealtList.sort((f, s) -> s.getKey().compareTo(f.getKey()));

        final Map<Integer, String> damageDealtRewards = new HashMap<>();
        final String mainMessage = config.getString("Messages.MobDead.damageDealt.message", "");

        for (int place = 1; place <= damageDealtList.size(); place++) {
            if (place >= config.getInt("PlacesToBroadcast")) break;

            final Pair<Double, String> info = damageDealtList.get(place);

            final Double damage = info.getKey();
            final String name = info.getValue();

            damageDealtRewards.put(place, name);

            String message = mainMessage;
            message = PLACE_PREFIX.matcher(message != null ? message : "").replaceAll(config.getString(config.contains("PlacePrefix." + place) ? "PlacePrefix." + place : "PlacePrefix.default", ""));
            message = DAMAGE_POS.matcher(message).replaceAll(Integer.toString(place));
            message = PLAYER_NAME.matcher(message).replaceAll(name);
            final DecimalFormat format = new DecimalFormat("#.##");
            message = DAMAGE.matcher(message).replaceAll(format.format(damage));
            debug(damage + " " + health);
            message = PERCENTAGE.matcher(message).replaceAll(format.format(getPercentage(damage, health)));
            message = replacePlaceholder(Bukkit.getPlayer(name), message);

            sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        Bukkit.getOnlinePlayers().forEach((p) -> {
            sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(entity, mobName, config.getString("Messages.MobDead.damageDealt.title.title", ""))),
                    ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(entity, mobName, config.getString("Messages.MobDead.damageDealt.title.subTitle", ""))),
                    config.getInt("Messages.MobDead.damageDealt.title.fadeIn", 0),
                    config.getInt("Messages.MobDead.damageDealt.title.stay", 0),
                    config.getInt("Messages.MobDead.damageDealt.title.fadeOut", 0));
            sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(entity, mobName, config.getString("Messages.MobDead.damageDealt.actionbar.message", ""))));
        });

        final List<Pair<Double, String>> damageTakenList = new ArrayList<>();
        damageInfo.getDamageTaken().forEach((p, d) -> damageTakenList.add(new Pair<>(d, p)));
        damageTakenList.sort((f, s) -> s.getKey().compareTo(f.getKey()));

        String footer = config.getString("Messages.MobDead.damageDealt.footer", "");
        footer = NAME.matcher(footer != null ? footer : "").replaceAll(ChatColor.stripColor(mobName));
        footer = replacePlaceholder(null, footer);
        sendMessage(footer);

        String _header = config.getString("Messages.MobDead.damageTaken.header", "");
        _header = NAME.matcher(_header != null ? _header : "").replaceAll(ChatColor.stripColor(mobName));
        _header = replacePlaceholder(null, _header);
        sendMessage(_header);

        final Map<Integer, String> damageTakenRewards = new HashMap<>();

        for (int place = 1; place <= damageTakenList.size(); place++) {
            if (place >= config.getInt("PlacesToBroadcast")) break;

            final Pair<Double, String> info = damageTakenList.get(place);

            final Double damage = info.getKey();
            final String name = info.getValue();

            damageTakenRewards.put(place, name);

            String message = mainMessage;
            message = PLACE_PREFIX.matcher(message != null ? message : "").replaceAll(config.getString(config.contains("PlacePrefix." + place) ? "PlacePrefix." + place : "PlacePrefix.default", ""));
            message = DAMAGE_POS.matcher(message).replaceAll(Integer.toString(place));
            message = PLAYER_NAME.matcher(message).replaceAll(name);
            final DecimalFormat format = new DecimalFormat("#.##");
            message = DAMAGE.matcher(message).replaceAll(format.format(damage));
            debug(damage + " " + health);
            message = PERCENTAGE.matcher(message).replaceAll(format.format(getPercentage(damage, health)));
            message = replacePlaceholder(Bukkit.getPlayer(name), message);

            //sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        Bukkit.getOnlinePlayers().forEach((p) -> {
            sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(entity, mobName, config.getString("Messages.MobDead.damageTaken.title.title", ""))),
                    ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(entity, mobName, config.getString("Messages.MobDead.damageTaken.title.subTitle", ""))),
                    config.getInt("Messages.MobDead.damageTaken.title.fadeIn", 0),
                    config.getInt("Messages.MobDead.damageTaken.title.stay", 0),
                    config.getInt("Messages.MobDead.damageTaken.title.fadeOut", 0));
            sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(entity, mobName, config.getString("Messages.MobDead.damageTaken.actionbar.message", ""))));
        });

        String _footer = config.getString("Messages.MobDead.damageTaken.footer", "");
        _footer = NAME.matcher(_footer != null ? _footer : "").replaceAll(ChatColor.stripColor(mobName));
        _footer = replacePlaceholder(null, _footer);
        sendMessage(_footer);

        new Reward(internalName, damageDealtRewards);
        debug("Final data: " + data);
        data.remove(entity);
    }
}
