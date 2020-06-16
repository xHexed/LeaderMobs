package com.github.xhexed.leadermobs.data;

import com.github.xhexed.leadermobs.utils.Pair;

import java.util.*;

public class MobDamageInfo {
    private final Map<UUID, Double> damageDealt;
    private final Map<UUID, Double> damageTaken;
    private final List<Pair<Double, UUID>> topDamageDealt = new ArrayList<>();
    private final List<Pair<Double, UUID>> topDamageTaken = new ArrayList<>();

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
        return calculateTop(topDamageDealt, damageDealt);
    }

    public List<Pair<Double, UUID>> getTopDamageTaken() {
        return calculateTop(topDamageTaken, damageTaken);
    }

    private List<Pair<Double, UUID>> calculateTop(final List<Pair<Double, UUID>> topList, final Map<UUID, Double> list) {
        if (!topList.isEmpty()) return topList;

        list.forEach((p, d) -> topList.add(new Pair<>(d, p)));
        topList.sort((f, s) -> s.getKey().compareTo(f.getKey()));
        return topList;
    }
}
