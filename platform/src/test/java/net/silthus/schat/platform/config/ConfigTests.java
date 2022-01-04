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

package net.silthus.schat.platform.config;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.usecases.ChannelConfig;
import net.silthus.schat.platform.TestConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurateAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.AUTO_JOIN;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ConfigTests {

    private SChatConfig config;

    @BeforeEach
    void setUp(@TempDir File temp) {
        final ConfigurationAdapter adapter = new TestConfigurationAdapter(new File(temp, "test-config.yml"));
        config = new SChatConfig(adapter);
        config.load();
    }

    private ChannelConfig getTestChannelConfig() {
        assertThat(config.get(CHANNELS)).containsKey("test");
        return config.get(CHANNELS).get("test");
    }

    @Test
    void loads_parsed_channel_name() {
        assertThat(getTestChannelConfig().getName()).isEqualTo(text("Test"));
    }

    @Test
    void loads_defined_channel_settings() {
        assertThat(getTestChannelConfig().getSettings().get(REQUIRES_JOIN_PERMISSION)).isTrue();
        assertThat(getTestChannelConfig().getSettings().get(AUTO_JOIN)).isTrue();
    }

    @Test
    void set_values_writes_and_loads_when_reloaded() {
        final TextComponent name = text("My Name");
        final Map<String, ChannelConfig> channels = config.get(CHANNELS);
        channels.get("test").setName(name);
        config.set(CHANNELS, channels);
        config.reload();
        assertThat(getTestChannelConfig().getName()).isEqualTo(name);
    }

    @Test
    void invalid_channel_does_not_throw() {
        assertThat(config.get(CHANNELS)).doesNotContainKey("invalid");
    }

    @Test
    void throws_when_loading_fails() {
        assertThatExceptionOfType(ConfigurationAdapter.LoadFailed.class)
            .isThrownBy(() -> new ErrorTest(null).load());
    }

    private static class ErrorTest extends ConfigurateAdapter {

        protected ErrorTest(Path path) {
            super(path);
        }

        @Override
        protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path, Function<ConfigurationOptions, ConfigurationOptions> defaultOptions) {
            return new ConfigurationLoader<>() {
                @Override
                public ConfigurationNode load(ConfigurationOptions options) throws ConfigurateException {
                    throw new ConfigurateException("");
                }

                @Override
                public ConfigurationReference<ConfigurationNode> loadToReference() {
                    return null;
                }

                @Override
                public void save(ConfigurationNode node) {

                }

                @Override
                public ConfigurationNode createNode(ConfigurationOptions options) {
                    return null;
                }
            };
        }
    }

}
