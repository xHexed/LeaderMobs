package com.github.xhexed.leadermobs.data;

import lombok.Getter;
import org.bukkit.entity.Entity;

@Getter
public class MobData {
    private Entity entity;
    private String name;
    private String displayName;

    public MobData(Entity entity, String name, String displayName) {
        this.entity = entity;
        this.name = name;
        this.displayName = displayName;
    }
}
