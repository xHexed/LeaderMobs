package com.github.xhexed.leadermobs.config.mobmessage.checker;

import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class MobChecker {
    private final Map<String, BiFunction<Entity, MobMessage, String>> checker;

    public MobChecker() {
        checker = new HashMap<>();
    }

    public void addChecker(String condition, BiFunction<Entity, MobMessage, String> checker) {
        this.checker.put(condition, checker);
    }

    public boolean check(MobMessage mobMessage, String pluginName, String mobName, Entity mobEntity) {
        for (MobCondition mobCondition : mobMessage.getMobConditions()) {
            Pattern pluginCondition = mobCondition.getPluginName();
            if (pluginCondition != null && !pluginCondition.matcher(pluginName).matches())
                continue;

            Pattern mobNameCondition = mobCondition.getMobName();
            if (mobNameCondition != null && !mobNameCondition.matcher(mobName).matches())
                continue;

            Map<String, Pattern> mobConditions = mobCondition.getConditions();
            if (mobConditions != null && !isConditionsMet(mobConditions, mobEntity, mobMessage)) continue;
            return true;
        }
        return false;
    }

    private boolean isConditionsMet(Map<String, Pattern> conditions, Entity entity, MobMessage mobMessage) {
        for (Map.Entry<String, Pattern> entry : conditions.entrySet()) {
            String condition = entry.getKey();
            Pattern value = entry.getValue();
            if (!checker.containsKey(condition))
                return false;
            if (!value.matcher(checker.get(condition).apply(entity, mobMessage)).matches())
                return false;
        }
        return true;
    }
}
