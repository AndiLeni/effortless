package dev.huskuraft.effortless.session.config.serializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;

import dev.huskuraft.effortless.api.config.ConfigSerializer;
import dev.huskuraft.effortless.api.core.ResourceLocation;
import dev.huskuraft.effortless.session.config.GeneralConfig;
import dev.huskuraft.effortless.session.config.SessionConfig;

public class SessionConfigSerializer implements ConfigSerializer<SessionConfig> {

    private static final String KEY_GLOBAL = "global";
    private static final String KEY_PLAYER = "player";

    private static final String KEY_USE_COMMANDS = "useCommands";
    private static final String KEY_ALLOW_USE_MOD = "allowUseMod";
    private static final String KEY_ALLOW_BREAK_BLOCKS = "allowBreakBlocks";
    private static final String KEY_ALLOW_PLACE_BLOCKS = "allowPlaceBlocks";
    private static final String KEY_MAX_REACH_DISTANCE = "maxReachDistance";
    private static final String KEY_MAX_DISTANCE_PER_AXIS = "maxDistancePerAxis";
    private static final String KEY_MAX_BREAK_BOX_VOLUME = "maxBreakBoxVolume";
    private static final String KEY_MAX_PLACE_BOX_VOLUME = "maxPlaceBoxVolume";
    private static final String KEY_WHITELISTED_ITEMS = "whitelistedItems";
    private static final String KEY_BLACKLISTED_ITEMS = "blacklistedItems";

    @Override
    public ConfigSpec getSpec(Config config) {
        var spec = new ConfigSpec();
        spec.define(KEY_GLOBAL, GlobalGeneralConfigSerializer.INSTANCE.serialize(getDefault().globalConfig()), Config.class::isInstance);
        spec.define(KEY_PLAYER, Config.inMemory(), Config.class::isInstance);
        return ConfigSerializer.super.getSpec(config);
    }

    @Override
    public SessionConfig getDefault() {
        return SessionConfig.defaultConfig();
    }

    @Override
    public SessionConfig deserialize(Config config) {
        return new SessionConfig(
                GlobalGeneralConfigSerializer.INSTANCE.deserialize(config.get(KEY_GLOBAL)),
                ((Config) config.get(KEY_PLAYER)).entrySet().stream().map(e -> {
                    try {
                        return Map.entry(UUID.fromString(e.getKey()), PerPlayerGeneralConfigSerializer.INSTANCE.deserialize(e.getValue()));
                    } catch (IllegalArgumentException | NullPointerException ignored) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
        );
    }

    @Override
    public Config serialize(SessionConfig sessionConfig) {
        var config = CommentedConfig.inMemory();
        config.add(KEY_GLOBAL, GlobalGeneralConfigSerializer.INSTANCE.serialize(sessionConfig.globalConfig()));
        for (var entry : sessionConfig.playerConfigs().entrySet()) {
            config.add(List.of(KEY_PLAYER, entry.getKey().toString()), PerPlayerGeneralConfigSerializer.INSTANCE.serialize(entry.getValue()));
        }
        return config;
    }

    public static class GlobalGeneralConfigSerializer implements ConfigSerializer<GeneralConfig> {

        private GlobalGeneralConfigSerializer() {
        }

        public static final GlobalGeneralConfigSerializer INSTANCE  = new GlobalGeneralConfigSerializer();

        @Override
        public ConfigSpec getSpec(Config config) {
            var spec = new ConfigSpec();
            spec.define(KEY_USE_COMMANDS, GeneralConfig.USE_COMMANDS_DEFAULT, Objects::nonNull);
            spec.define(KEY_ALLOW_USE_MOD, GeneralConfig.ALLOW_USE_MOD_DEFAULT, Objects::nonNull);
            spec.define(KEY_ALLOW_BREAK_BLOCKS, GeneralConfig.ALLOW_BREAK_BLOCKS_DEFAULT, Objects::nonNull);
            spec.define(KEY_ALLOW_PLACE_BLOCKS, GeneralConfig.ALLOW_PLACE_BLOCKS_DEFAULT, Objects::nonNull);
            spec.defineInRange(KEY_MAX_REACH_DISTANCE, GeneralConfig.MAX_REACH_DISTANCE_DEFAULT, GeneralConfig.MAX_REACH_DISTANCE_RANGE_START, GeneralConfig.MAX_REACH_DISTANCE_RANGE_END);
            spec.defineInRange(KEY_MAX_DISTANCE_PER_AXIS, GeneralConfig.MAX_DISTANCE_PER_AXIS_DEFAULT, GeneralConfig.MAX_DISTANCE_PER_AXIS_RANGE_START, GeneralConfig.MAX_DISTANCE_PER_AXIS_RANGE_END);
            spec.defineInRange(KEY_MAX_BREAK_BOX_VOLUME, GeneralConfig.MAX_BREAK_BOX_VOLUME_DEFAULT, GeneralConfig.MAX_BREAK_BOX_VOLUME_RANGE_START, GeneralConfig.MAX_BREAK_BOX_VOLUME_RANGE_END);
            spec.defineInRange(KEY_MAX_PLACE_BOX_VOLUME, GeneralConfig.MAX_PLACE_BOX_VOLUME_DEFAULT, GeneralConfig.MAX_PLACE_BOX_VOLUME_RANGE_START, GeneralConfig.MAX_PLACE_BOX_VOLUME_RANGE_END);
            spec.defineList(KEY_WHITELISTED_ITEMS, GeneralConfig.WHITELISTED_ITEMS_DEFAULT.stream().map(ResourceLocation::getString).toList(), Objects::nonNull);
            spec.defineList(KEY_BLACKLISTED_ITEMS, GeneralConfig.BLACKLISTED_ITEMS_DEFAULT.stream().map(ResourceLocation::getString).toList(), Objects::nonNull);
            return spec;
        }

        @Override
        public GeneralConfig getDefault() {
            return GeneralConfig.DEFAULT;
        }

        @Override
        public GeneralConfig deserialize(Config config) {
            validate(config);
            return new GeneralConfig(
                    config.get(KEY_USE_COMMANDS),
                    config.get(KEY_ALLOW_USE_MOD),
                    config.get(KEY_ALLOW_BREAK_BLOCKS),
                    config.get(KEY_ALLOW_PLACE_BLOCKS),
                    config.get(KEY_MAX_REACH_DISTANCE),
                    config.get(KEY_MAX_DISTANCE_PER_AXIS),
                    config.get(KEY_MAX_BREAK_BOX_VOLUME),
                    config.get(KEY_MAX_PLACE_BOX_VOLUME),
                    config.get(KEY_WHITELISTED_ITEMS) == null ? null : config.<List<String>>get(KEY_WHITELISTED_ITEMS).stream().map(ResourceLocation::decompose).toList(),
                    config.get(KEY_BLACKLISTED_ITEMS) == null ? null : config.<List<String>>get(KEY_BLACKLISTED_ITEMS).stream().map(ResourceLocation::decompose).toList()
            );
        }

        @Override
        public Config serialize(GeneralConfig generalConfig) {
            var config = CommentedConfig.inMemory();
            config.set(KEY_USE_COMMANDS, generalConfig.useCommands());
            config.set(KEY_ALLOW_USE_MOD, generalConfig.allowUseMod());
            config.set(KEY_ALLOW_BREAK_BLOCKS, generalConfig.allowBreakBlocks());
            config.set(KEY_ALLOW_PLACE_BLOCKS, generalConfig.allowPlaceBlocks());
            config.set(KEY_MAX_REACH_DISTANCE, generalConfig.maxReachDistance());
            config.set(KEY_MAX_DISTANCE_PER_AXIS, generalConfig.maxDistancePerAxis());
            config.set(KEY_MAX_BREAK_BOX_VOLUME, generalConfig.maxBreakBoxVolume());
            config.set(KEY_MAX_PLACE_BOX_VOLUME, generalConfig.maxPlaceBoxVolume());
            config.set(KEY_WHITELISTED_ITEMS, generalConfig.whitelistedItems() == null ? null : generalConfig.whitelistedItems().stream().map(ResourceLocation::getString).toList());
            config.set(KEY_BLACKLISTED_ITEMS, generalConfig.blacklistedItems() == null ? null : generalConfig.blacklistedItems().stream().map(ResourceLocation::getString).toList());

            config.setComment(KEY_USE_COMMANDS, "Should use commands to build using this mod? It's available for client side only.");
            config.setComment(KEY_ALLOW_USE_MOD, "Should allow players to use this mod?");
            config.setComment(KEY_ALLOW_BREAK_BLOCKS, "Should allow players to break blocks using this mod?");
            config.setComment(KEY_ALLOW_PLACE_BLOCKS, "Should allow players to place blocks using this mod?");
            config.setComment(KEY_MAX_REACH_DISTANCE, "The maximum distance a player can reach when building using this mod. \nRange: " + GeneralConfig.MAX_REACH_DISTANCE_RANGE_START + " ~ " + GeneralConfig.MAX_REACH_DISTANCE_RANGE_END);
            config.setComment(KEY_MAX_DISTANCE_PER_AXIS, "The maximum size a player can build per axis when building using this mod. \nRange: " + GeneralConfig.MAX_DISTANCE_PER_AXIS_RANGE_START + " ~ " + GeneralConfig.MAX_DISTANCE_PER_AXIS_RANGE_END);
            config.setComment(KEY_MAX_BREAK_BOX_VOLUME, "The maximum volume a player can break when building using this mod. \nRange: " + GeneralConfig.MAX_BREAK_BOX_VOLUME_RANGE_START + " ~ " + GeneralConfig.MAX_BREAK_BOX_VOLUME_RANGE_END);
            config.setComment(KEY_MAX_PLACE_BOX_VOLUME, "The maximum volume a player can place when building using this mod. \nRange: " + GeneralConfig.MAX_PLACE_BOX_VOLUME_RANGE_START + " ~ " + GeneralConfig.MAX_PLACE_BOX_VOLUME_RANGE_END);
            config.setComment(KEY_WHITELISTED_ITEMS, "The list of items that players are allowed to use when building using this mod. \nIf the whitelist is empty, all items are allowed. \nIf the whitelist is not empty, only the items in the whitelist are allowed. \nThe value must be a list of item resource locations like [\"minecraft:stone\", \"minecraft:dirt\"].");
            config.setComment(KEY_BLACKLISTED_ITEMS, "The list of items that players are not allowed to use when building using this mod. \nIf the blacklist is empty, no items are not allowed. \nIf an item exists both in the blacklist and the whitelist, it will not be allowed. \nThe value must be a list of item resource locations like [\"minecraft:stone\", \"minecraft:dirt\"].");
            validate(config);
            return config;
        }

    }

    public static class PerPlayerGeneralConfigSerializer implements ConfigSerializer<GeneralConfig> {

        private PerPlayerGeneralConfigSerializer() {
        }

        public static final PerPlayerGeneralConfigSerializer INSTANCE  = new PerPlayerGeneralConfigSerializer();

        private static <T> void addOrRemove(Config config, String path, T value) {
            if (value == null) {
                config.remove(path);
            } else {
                config.add(path, value);
            }
        }

        @Override
        public ConfigSpec getSpec(Config config) {
            var spec = new ConfigSpec();
            if (config.contains(KEY_USE_COMMANDS)) {
                spec.define(KEY_USE_COMMANDS, GeneralConfig.USE_COMMANDS_DEFAULT, Boolean.class::isInstance);
            }
            if (config.contains(KEY_ALLOW_USE_MOD)) {
                spec.define(KEY_ALLOW_USE_MOD, GeneralConfig.ALLOW_USE_MOD_DEFAULT, Boolean.class::isInstance);
            }
            if (config.contains(KEY_ALLOW_BREAK_BLOCKS)) {
                spec.define(KEY_ALLOW_BREAK_BLOCKS, GeneralConfig.ALLOW_BREAK_BLOCKS_DEFAULT, Boolean.class::isInstance);
            }
            if (config.contains(KEY_ALLOW_PLACE_BLOCKS)) {
                spec.define(KEY_ALLOW_PLACE_BLOCKS, GeneralConfig.ALLOW_PLACE_BLOCKS_DEFAULT, Boolean.class::isInstance);
            }
            if (config.contains(KEY_MAX_REACH_DISTANCE)) {
                spec.defineInRange(KEY_MAX_REACH_DISTANCE, GeneralConfig.MAX_REACH_DISTANCE_DEFAULT, GeneralConfig.MAX_REACH_DISTANCE_RANGE_START, GeneralConfig.MAX_REACH_DISTANCE_RANGE_END);
            }
            if (config.contains(KEY_MAX_DISTANCE_PER_AXIS)) {
                spec.defineInRange(KEY_MAX_DISTANCE_PER_AXIS, GeneralConfig.MAX_DISTANCE_PER_AXIS_DEFAULT, GeneralConfig.MAX_DISTANCE_PER_AXIS_RANGE_START, GeneralConfig.MAX_DISTANCE_PER_AXIS_RANGE_END);
            }
            if (config.contains(KEY_MAX_BREAK_BOX_VOLUME)) {
                spec.defineInRange(KEY_MAX_BREAK_BOX_VOLUME, GeneralConfig.MAX_BREAK_BOX_VOLUME_DEFAULT, GeneralConfig.MAX_BREAK_BOX_VOLUME_RANGE_START, GeneralConfig.MAX_BREAK_BOX_VOLUME_RANGE_END);
            }
            if (config.contains(KEY_MAX_PLACE_BOX_VOLUME)) {
                spec.defineInRange(KEY_MAX_PLACE_BOX_VOLUME, GeneralConfig.MAX_PLACE_BOX_VOLUME_DEFAULT, GeneralConfig.MAX_PLACE_BOX_VOLUME_RANGE_START, GeneralConfig.MAX_PLACE_BOX_VOLUME_RANGE_END);
            }
            if (config.contains(KEY_WHITELISTED_ITEMS)) {
                spec.defineList(KEY_WHITELISTED_ITEMS, GeneralConfig.WHITELISTED_ITEMS_DEFAULT, Objects::nonNull);
            }
            if (config.contains(KEY_BLACKLISTED_ITEMS)) {
                spec.defineList(KEY_BLACKLISTED_ITEMS, GeneralConfig.BLACKLISTED_ITEMS_DEFAULT, Objects::nonNull);
            }
            return spec;
        }

        @Override
        public GeneralConfig getDefault() {
            return GeneralConfig.NULL;
        }

        @Override
        public GeneralConfig deserialize(Config config) {
            validate(config);
            return new GeneralConfig(
                    config.get(KEY_USE_COMMANDS),
                    config.get(KEY_ALLOW_USE_MOD),
                    config.get(KEY_ALLOW_BREAK_BLOCKS),
                    config.get(KEY_ALLOW_PLACE_BLOCKS),
                    config.get(KEY_MAX_REACH_DISTANCE),
                    config.get(KEY_MAX_DISTANCE_PER_AXIS),
                    config.get(KEY_MAX_BREAK_BOX_VOLUME),
                    config.get(KEY_MAX_PLACE_BOX_VOLUME),
                    config.get(KEY_WHITELISTED_ITEMS) == null ? null : config.<List<String>>get(KEY_WHITELISTED_ITEMS).stream().map(ResourceLocation::decompose).toList(),
                    config.get(KEY_BLACKLISTED_ITEMS) == null ? null : config.<List<String>>get(KEY_BLACKLISTED_ITEMS).stream().map(ResourceLocation::decompose).toList()
            );
        }

        @Override
        public Config serialize(GeneralConfig generalConfig) {
            var config = CommentedConfig.inMemory();
            addOrRemove(config, KEY_USE_COMMANDS, generalConfig.useCommands());
            addOrRemove(config, KEY_ALLOW_USE_MOD, generalConfig.allowUseMod());
            addOrRemove(config, KEY_ALLOW_BREAK_BLOCKS, generalConfig.allowBreakBlocks());
            addOrRemove(config, KEY_ALLOW_PLACE_BLOCKS, generalConfig.allowPlaceBlocks());
            addOrRemove(config, KEY_MAX_REACH_DISTANCE, generalConfig.maxReachDistance());
            addOrRemove(config, KEY_MAX_DISTANCE_PER_AXIS, generalConfig.maxDistancePerAxis());
            addOrRemove(config, KEY_MAX_BREAK_BOX_VOLUME, generalConfig.maxBreakBoxVolume());
            addOrRemove(config, KEY_MAX_PLACE_BOX_VOLUME, generalConfig.maxPlaceBoxVolume());
            addOrRemove(config, KEY_WHITELISTED_ITEMS, generalConfig.whitelistedItems() == null ? null : generalConfig.whitelistedItems().stream().map(ResourceLocation::getString).toList());
            addOrRemove(config, KEY_BLACKLISTED_ITEMS, generalConfig.blacklistedItems() == null ? null : generalConfig.blacklistedItems().stream().map(ResourceLocation::getString).toList());
            validate(config);
            return config;
        }

    }


}
