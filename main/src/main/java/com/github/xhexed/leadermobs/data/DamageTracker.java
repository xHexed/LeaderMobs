package com.github.xhexed.leadermobs.data;

import java.util.UUID;

public class DamageTracker {
    private double totalDamage;
    private UUID tracker;

    public DamageTracker(double totalDamage, UUID tracker) {
        this.totalDamage = totalDamage;
        this.tracker = tracker;
    }

    public double getTotalDamage() {
        return totalDamage;
    }

    public UUID getTracker() {
        return tracker;
    }
}
