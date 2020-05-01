package com.github.xhexed.leadermobs.data;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobDamageInfo {
    private final Map<String, Double> damageDealt;
    private final Map<String, Double> damageTaken;
    private Pair<List<Pair<Double, String>>, Boolean> topDamageDealt = new Pair<>(new ArrayList<>(), false);
    private Pair<List<Pair<Double, String>>, Boolean> topDamageTaken = new Pair<>(new ArrayList<>(), false);

    public MobDamageInfo(final Map<String, Double> damageDealt, final Map<String, Double> damageTaken) {
        this.damageDealt = new HashMap<>(damageDealt);
        this.damageTaken = new HashMap<>(damageTaken);
    }

    public Map<String, Double> getDamageDealt() {
        return damageDealt;
    }

    public Map<String, Double> getDamageTaken() {
        return damageTaken;
    }

    public List<Pair<Double, String>> getTopDamageDealt() {
        final List<Pair<Double, String>> topDamageDealt = calculateTop(this.topDamageDealt.getKey(), damageDealt, this.topDamageDealt.getValue());
        this.topDamageDealt = new Pair<>(topDamageDealt, true);
        return topDamageDealt;
    }

    public List<Pair<Double, String>> getTopDamageTaken() {
        final List<Pair<Double, String>> topDamageTaken = calculateTop(this.topDamageTaken.getKey(), damageTaken, topDamageDealt.getValue());
        this.topDamageTaken = new Pair<>(topDamageTaken, true);
        return topDamageTaken;
    }

    private List<Pair<Double, String>> calculateTop(final List<Pair<Double, String>> topList, final Map<String, Double> list, final boolean isCalculated) {
        if (isCalculated) return topList;

        topList.clear();
        list.forEach((p, d) -> topList.add(new Pair<>(d, p)));
        topList.sort((f, s) -> s.getKey().compareTo(f.getKey()));
        return topList;
    }
}
