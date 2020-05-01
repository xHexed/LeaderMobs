package com.github.xhexed.leadermobs.data;

import javafx.util.Pair;

import java.util.*;

public class MobDamageInfo {
    private final Map<UUID, Double> damageDealt;
    private final Map<UUID, Double> damageTaken;
    private Pair<List<Pair<Double, UUID>>, Boolean> topDamageDealt = new Pair<>(new ArrayList<>(), false);
    private Pair<List<Pair<Double, UUID>>, Boolean> topDamageTaken = new Pair<>(new ArrayList<>(), false);

    public MobDamageInfo(final Map<UUID, Double> damageDealt, final Map<UUID, Double> damageTaken) {
        this.damageDealt = new HashMap<>(damageDealt);
        this.damageTaken = new HashMap<>(damageTaken);
    }

    public Map<UUID, Double> getDamageDealt() {
        return damageDealt;
    }

    public Map<UUID, Double> getDamageTaken() {
        return damageTaken;
    }

    public List<Pair<Double, UUID>> getTopDamageDealt() {
        final List<Pair<Double, UUID>> topDamageDealt = calculateTop(this.topDamageDealt.getKey(), damageDealt, this.topDamageDealt.getValue());
        this.topDamageDealt = new Pair<>(topDamageDealt, true);
        return topDamageDealt;
    }

    public List<Pair<Double, UUID>> getTopDamageTaken() {
        final List<Pair<Double, UUID>> topDamageTaken = calculateTop(this.topDamageTaken.getKey(), damageTaken, topDamageDealt.getValue());
        this.topDamageTaken = new Pair<>(topDamageTaken, true);
        return topDamageTaken;
    }

    private List<Pair<Double, UUID>> calculateTop(final List<Pair<Double, UUID>> topList, final Map<UUID, Double> list, final boolean isCalculated) {
        if (isCalculated) return topList;

        topList.clear();
        list.forEach((p, d) -> topList.add(new Pair<>(d, p)));
        topList.sort((f, s) -> s.getKey().compareTo(f.getKey()));
        return topList;
    }
}
