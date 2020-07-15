package com.github.xhexed.leadermobs.data;

import com.github.xhexed.leadermobs.utils.Pair;

import java.util.*;

public class MobDamageInfo {
    private final Map<UUID, Double> damageDealt;
    private final Map<UUID, Double> damageTaken;
    private List<Pair<Double, UUID>> topDamageDealt = new ArrayList<>();
    private List<Pair<Double, UUID>> topDamageTaken = new ArrayList<>();
    private double totalDamageDealt;
    private double totalDamageTaken;

    public MobDamageInfo(final Map<UUID, Double> damageDealt, final Map<UUID, Double> damageTaken) {
        this.damageDealt = new HashMap<>(damageDealt);
        this.damageTaken = new HashMap<>(damageTaken);
    }

    public void calculateTop() {
        Pair<List<Pair<Double, UUID>>, Double> result;

        result = calculateTop(topDamageDealt, damageDealt);
        topDamageDealt = result.getKey();
        totalDamageDealt = result.getValue();

        result = calculateTop(topDamageTaken, damageTaken);
        topDamageTaken = result.getKey();
        totalDamageTaken = result.getValue();
    }

    public Map<UUID, Double> getDamageDealt() {
        return damageDealt;
    }

    public Map<UUID, Double> getDamageTaken() {
        return damageTaken;
    }

    public double getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public double getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public List<Pair<Double, UUID>> getTopDamageDealt() {
        return topDamageDealt;
    }

    public List<Pair<Double, UUID>> getTopDamageTaken() {
        return topDamageTaken;
    }

    private Pair<List<Pair<Double, UUID>>, Double> calculateTop(final List<Pair<Double, UUID>> topList, final Map<UUID, Double> list) {
        if (!topList.isEmpty()) return new Pair<>(topList, 0.0);
        double totalDamage = 0.0;

        for (final Map.Entry<UUID, Double> entry : list.entrySet()) {
            final Double damage = entry.getValue();
            topList.add(new Pair<>(damage, entry.getKey()));
            totalDamage += damage;
        }
        topList.sort((f, s) -> s.getKey().compareTo(f.getKey()));
        return new Pair<>(topList, totalDamage);
    }
}
