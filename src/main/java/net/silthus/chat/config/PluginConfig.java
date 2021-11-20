/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat.config;

import com.google.common.base.Strings;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.silthus.chat.Constants;
import net.silthus.chat.Formats;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static java.util.Objects.requireNonNullElseGet;
import static java.util.stream.Collectors.toMap;
import static net.silthus.chat.config.BroadcastConfig.broadcastDefaults;
import static net.silthus.chat.config.ChannelConfig.channelConfig;
import static net.silthus.chat.config.ChannelConfig.channelDefaults;
import static net.silthus.chat.config.ConsoleConfig.consoleConfig;
import static net.silthus.chat.config.ConsoleConfig.consoleDefaults;
import static net.silthus.chat.config.FormatConfig.formatConfig;
import static net.silthus.chat.config.PlayerConfig.playerConfig;
import static net.silthus.chat.config.PrivateChatConfig.privateChatDefaults;

@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
@Log(topic = Constants.PLUGIN_NAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PluginConfig {

    public static PluginConfig.PluginConfigBuilder builder() {
        return new PluginConfigBuilder().formats(Formats.DEFAULT_FORMATS);
    }

    public static PluginConfig config(ConfigurationSection config) {
        return configDefaults().withConfig(config).build();
    }

    public static PluginConfig configDefaults() {
        return builder().build();
    }

    @Builder.Default
    String languageConfig = "languages/en.yaml";
    @Builder.Default
    Defaults defaults = new Defaults(channelDefaults());
    @Builder.Default
    ConsoleConfig console = consoleDefaults();
    @Builder.Default
    PrivateChatConfig privateChat = privateChatDefaults();
    @Builder.Default
    BroadcastConfig broadcast = broadcastDefaults();
    @Builder.Default
    PlayerConfig player = PlayerConfig.playerDefaults();
    @Singular
    Map<String, ChannelConfig> channels;
    @Singular
    Map<String, FormatConfig> formats;

    public PluginConfig registerFormatTemplates() {
        formats.forEach((key, value) -> value.registerAsTemplate(key));
        return this;
    }

    public PluginConfig.PluginConfigBuilder withConfig(ConfigurationSection config) {
        return toBuilder()
                .languageConfig(loadLanguage(config))
                .defaults(loadDefaults(getSection(config, "defaults")))
                .console(consoleConfig(getSection(config, "console")))
                .player(playerConfig(getSection(config, "players")))
                .privateChat(PrivateChatConfig.privateChat(getSection(config, "private_chats")))
                .broadcast(BroadcastConfig.broadcast(getSection(config, "broadcast")))
                .channelsFromConfig(getSection(config, "channels"))
                .formatsFromConfig(getSection(config, "formats"));
    }

    private String loadLanguage(ConfigurationSection config) {
        if (!config.isSet("language"))
            warnSectionNotDefined("language");
        return "languages/" + config.getString("language", "en") + ".yaml";
    }

    private Defaults loadDefaults(@NonNull ConfigurationSection config) {
        return new Defaults(channelConfig(getSection(config, "channel")));
    }

    @NotNull
    private ConfigurationSection getSection(@NonNull ConfigurationSection config, String section) {
        final String path = Strings.isNullOrEmpty(config.getCurrentPath()) ? "" : config.getCurrentPath() + ".";
        return requireNonNullElseGet(
                config.getConfigurationSection(section),
                () -> warnAndDefault(path + section, config.createSection(section))
        );
    }

    private <TConfig> TConfig warnAndDefault(String section, TConfig defaultValue) {
        warnSectionNotDefined(section);
        return defaultValue;
    }

    private void warnSectionNotDefined(String section) {
        log.warning("No '" + section + "' section found inside your config.yml! Make sure your config is up-to-date with the config.default.yml.");
    }

    public record Defaults(ChannelConfig channel) {

    }

    public static class PluginConfigBuilder {

        private PluginConfigBuilder channelsFromConfig(@NonNull ConfigurationSection config) {
            return channels(loadChannels(config));
        }

        private PluginConfigBuilder formatsFromConfig(@NonNull ConfigurationSection config) {
            return formats(loadFormats(config));
        }

        private Map<String, ChannelConfig> loadChannels(@NonNull ConfigurationSection config) {
            final Defaults defaults = defaults$set ? defaults$value : $default$defaults();
            return config.getKeys(false).stream()
                    .collect(toMap(
                            key -> key,
                            key -> defaults.channel.withConfig(config.getConfigurationSection(key)).build()
                    ));
        }

        private Map<String, FormatConfig> loadFormats(@NonNull ConfigurationSection config) {
            return config.getKeys(false).stream()
                    .collect(toMap(
                            key -> key,
                            key -> formatConfig(config.getConfigurationSection(key))
                    ));
        }
    }
}
