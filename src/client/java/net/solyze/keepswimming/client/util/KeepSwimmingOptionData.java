package net.solyze.keepswimming.client.util;

import net.solyze.keepswimming.config.KeepSwimmingConfig;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record KeepSwimmingOptionData(
        String key,
        String name,
        String description,
        Function<KeepSwimmingConfig, Boolean> getter,
        BiConsumer<KeepSwimmingConfig, Boolean> setter
) {}