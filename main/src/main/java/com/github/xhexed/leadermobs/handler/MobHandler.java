package com.github.xhexed.leadermobs.handler;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.Reward;
import com.github.xhexed.leadermobs.Utils;
import com.github.xhexed.leadermobs.data.MobDamageInfo;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.Utils.*;

public class MobHandler {
    public static final Map<Entity, MobDamageInfo> data = new HashMap<>();

    public static void onMobSpawn(final Entity entity, final String mobName) {
        data.put(entity, new MobDamageInfo(new HashMap<>(), new HashMap<>()));

        final Location location = entity.getLocation();
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();

        final FileConfiguration config = getInstance().getConfig();
        if (!LeaderMobs.broadcast) return;

        final BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskLater(getInstance(), () -> {
            config.getStringList("Messages.MobSpawn.messages").stream()
                    .map(message -> getMobSpawnMessage(mobName, x, y, z, message))
                    .forEach(Utils::sendMessage);

            scheduler.runTaskLater(getInstance(), () -> {
                if (config.getBoolean("Messages.MobSpawn.title.enabled", false)) {
                    Bukkit.getOnlinePlayers().forEach((p) -> sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(mobName, x, y, z, config.getString("Messages.MobSpawn.title.title", ""))),
                            ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(mobName, x, y, z, config.getString("Messages.MobSpawn.title.subTitle", ""))),
                            config.getInt("Messages.MobSpawn.title.fadeIn", 0),
                            config.getInt("Messages.MobSpawn.title.stay", 0),
                            config.getInt("Messages.MobSpawn.title.fadeOut", 0)));
                }
            }, config.getLong("Messages.MobSpawn.title.delay", 0));

            scheduler.runTaskLater(getInstance(), () -> {
                if (config.getBoolean("Messages.MobSpawn.actionbar.enabled", false)) {
                    Bukkit.getOnlinePlayers().forEach((p) -> sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobSpawnMessage(mobName, x, y, z, config.getString("Messages.MobSpawn.actionbar.message", "")))));
                }
            }, config.getLong("Messages.MobSpawn.actionbar.delay", 0));
        }, config.getLong("Messages.MobSpawn.delay", 0));
    }

    public static void onPlayerDamage(final UUID uuid, final Entity entity, final Double damage) {
        final MobDamageInfo mobInfo = data.get(entity);
        final Map<UUID, Double> damageDealtList = mobInfo.getDamageDealt();
        final double damageFinal = Math.min(((Damageable) entity).getHealth(), damage);
        damageDealtList.put(uuid, damageDealtList.containsKey(uuid) ?
                damageDealtList.get(uuid) + damageFinal : damageFinal);
        data.put(entity, new MobDamageInfo(damageDealtList, mobInfo.getDamageTaken()));
    }

    public static void onMobDamage(final Entity entity, final UUID uuid, final Double damage) {
        final Map<UUID, Double> damageTakenList = data.containsKey(entity) ? data.get(entity).getDamageTaken() : new HashMap<>();
        final double damageFinal = Math.min(((Damageable) entity).getHealth(), damage);
        damageTakenList.put(uuid, damageTakenList.containsKey(uuid) ?
                damageTakenList.get(uuid) + damageFinal : damageFinal);
        data.put(entity, new MobDamageInfo(data.get(entity).getDamageDealt(), damageTakenList));
    }

    public static void onMobDeath(final Entity entity, final String mobName, final String internalName, final double health) {
        if (!data.containsKey(entity)) return;
        final MobDamageInfo damageInfo = data.get(entity);
        final FileConfiguration config = getInstance().getConfig();
        if (damageInfo.getDamageDealt().keySet().size() < config.getInt("PlayersRequired")) return;

        final BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskLater(getInstance(), () -> {
            scheduler.runTaskLater(getInstance(), () -> {
                String damageDealtHeader = config.getString("Messages.MobDead.damageDealt.header", "");
                damageDealtHeader = NAME.matcher(damageDealtHeader != null ? damageDealtHeader : "").replaceAll(ChatColor.stripColor(mobName));
                damageDealtHeader = replaceMobPlaceholder(damageDealtHeader, damageInfo);
                sendMessage(damageDealtHeader);

                sendPlaceMessage(health, config, damageInfo.getTopDamageDealt(), config.getString("Messages.MobDead.damageDealt.message", ""));

                scheduler.runTaskLater(getInstance(), () -> {
                    if (config.getBoolean("Messages.MobDead.damageDealt.title.enabled", false)) {
                        Bukkit.getOnlinePlayers().forEach((p) -> sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(damageInfo, mobName, config.getString("Messages.MobDead.damageDealt.title.title", ""))),
                                ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(damageInfo, mobName, config.getString("Messages.MobDead.damageDealt.title.subTitle", ""))),
                                config.getInt("Messages.MobDead.damageDealt.title.fadeIn", 0),
                                config.getInt("Messages.MobDead.damageDealt.title.stay", 0),
                                config.getInt("Messages.MobDead.damageDealt.title.fadeOut", 0)));
                    }
                }, config.getLong("Messages.MobDead.damageDealt.title.delay", 0));

                scheduler.runTaskLater(getInstance(), () -> {
                    if (config.getBoolean("Messages.MobDead.damageDealt.actionbar.enabled", false)) {
                        Bukkit.getOnlinePlayers().forEach((p) -> sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(damageInfo, mobName, config.getString("Messages.MobDead.damageDealt.actionbar.message", "")))));
                    }
                }, config.getLong("Messages.MobDead.damageDealt.actionbar.delay", 0));

                String damageDealtFooter = config.getString("Messages.MobDead.damageDealt.footer", "");
                damageDealtFooter = NAME.matcher(damageDealtFooter != null ? damageDealtFooter : "").replaceAll(ChatColor.stripColor(mobName));
                damageDealtFooter = replaceMobPlaceholder(damageDealtFooter, damageInfo);
                sendMessage(damageDealtFooter);
            }, config.getLong("Messages.MobDead.damageDealt.delay", 0));

            scheduler.runTaskLater(getInstance(), () -> {
                String damageTakenheader = config.getString("Messages.MobDead.damageTaken.header", "");
                damageTakenheader = NAME.matcher(damageTakenheader != null ? damageTakenheader : "").replaceAll(ChatColor.stripColor(mobName));
                damageTakenheader = replaceMobPlaceholder(damageTakenheader, damageInfo);
                sendMessage(damageTakenheader);

                sendPlaceMessage(health, config, damageInfo.getTopDamageTaken(), config.getString("Messages.MobDead.damageTaken.message", ""));

                scheduler.runTaskLater(getInstance(), () -> {
                    if (config.getBoolean("Messages.MobDead.damageTaken.title.enabled", false)) {
                        Bukkit.getOnlinePlayers().forEach((p) -> sendTitle(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(damageInfo, mobName, config.getString("Messages.MobDead.damageTaken.title.title", ""))),
                                ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(damageInfo, mobName, config.getString("Messages.MobDead.damageTaken.title.subTitle", ""))),
                                config.getInt("Messages.MobDead.damageTaken.title.fadeIn", 0),
                                config.getInt("Messages.MobDead.damageTaken.title.stay", 0),
                                config.getInt("Messages.MobDead.damageTaken.title.fadeOut", 0)));
                    }
                }, config.getLong("Messages.MobDead.damageTaken.title.delay", 0));

                scheduler.runTaskLater(getInstance(), () -> {
                    if (config.getBoolean("Messages.MobDead.damageTaken.actionbar.enabled", false)) {
                        Bukkit.getOnlinePlayers().forEach((p) -> sendActionBar(p, ChatColor.translateAlternateColorCodes('&', getMobDeathMessage(damageInfo, mobName, config.getString("Messages.MobDead.damageTaken.actionbar.message", "")))));
                    }
                }, config.getLong("Messages.MobDead.damageTaken.actionbar.delay", 0));

                String damageTakenFooter = config.getString("Messages.MobDead.damageTaken.footer", "");
                damageTakenFooter = NAME.matcher(damageTakenFooter != null ? damageTakenFooter : "").replaceAll(ChatColor.stripColor(mobName));
                damageTakenFooter = replaceMobPlaceholder(damageTakenFooter, damageInfo);
                sendMessage(damageTakenFooter);
            }, config.getLong("Messages.MobDead.damageTaken.delay", 0));
        }, config.getLong("Messages.MobDead.delay", 0));

        new Reward(internalName,
                damageInfo.getTopDamageDealt().stream().map(Pair::getValue).collect(Collectors.toList()),
                damageInfo.getTopDamageTaken().stream().map(Pair::getValue).collect(Collectors.toList()));
        data.remove(entity);
    }

    private static void sendPlaceMessage(final double health, final ConfigurationSection config, final List<? extends Pair<Double, UUID>> damageList, final String damageMessage) {
        for (int place = 1; place <= damageList.size(); place++) {
            if (place >= config.getInt("PlacesToBroadcast")) break;

            final Pair<Double, UUID> info = damageList.get(place - 1);

            final Double damage = info.getKey();
            final UUID uuid = info.getValue();
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            String message = damageMessage;
            message = PLACE_PREFIX.matcher(message != null ? message : "").replaceAll(config.getString(config.contains("PlacePrefix." + place) ? "PlacePrefix." + place : "PlacePrefix.default", ""));
            message = DAMAGE_POS.matcher(message).replaceAll(Integer.toString(place));
            message = PLAYER_NAME.matcher(message).replaceAll(player.getName());
            message = DAMAGE.matcher(message).replaceAll(DOUBLE_FORMAT.format(damage));
            message = PERCENTAGE.matcher(message).replaceAll(DOUBLE_FORMAT.format(getPercentage(damage, health)));
            message = replacePlaceholder(player, message);
            sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
