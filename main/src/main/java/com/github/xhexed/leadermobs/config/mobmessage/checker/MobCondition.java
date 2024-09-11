package com.github.xhexed.leadermobs.config.mobmessage.checker;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
public class MobCondition {
    private static final String NAME_SEPARATOR = ";";
    private static final String PLUGIN_SEPARATOR = "@";
    private static final String CONDITION_SEPARATOR = "@";
    private static final String VALUE_SEPARATOR = "=";

    private Pattern pluginName;
    private Map<String, Pattern> conditions;
    private final Pattern mobName;

    /**
     * Stores conditions from the following syntax:
     * %plugin% @ %condition 1% = %value 1% @ %condition 2% = %value 2% @ ... ; %mob%
     */
    public MobCondition(String input) {
        if (!input.contains(NAME_SEPARATOR)) {
            mobName = Pattern.compile(input);
            return;
        }
        mobName = Pattern.compile(StringUtils.substringAfter(input, NAME_SEPARATOR));
        input = StringUtils.substringBefore(input, NAME_SEPARATOR);

        if (!input.contains(PLUGIN_SEPARATOR)) {
            pluginName = Pattern.compile(input);
            return;
        }
        pluginName = Pattern.compile(StringUtils.substringBefore(input, PLUGIN_SEPARATOR));
        input = StringUtils.substringAfter(input, PLUGIN_SEPARATOR);

        String[] conditionStr = input.split(CONDITION_SEPARATOR);
        conditions = new HashMap<>();
        for (String subCondition : conditionStr) {
            String condition = StringUtils.substringBefore(subCondition, VALUE_SEPARATOR);
            String value = StringUtils.substringAfter(subCondition, VALUE_SEPARATOR);
            conditions.put(condition, Pattern.compile(value));
        }
    }
}
