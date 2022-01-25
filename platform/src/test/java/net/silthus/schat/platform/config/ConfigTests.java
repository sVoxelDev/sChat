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
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.PROTECTED;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigTests {

    private SChatConfig config;

    @BeforeEach
    void setUp(@TempDir File temp) {
        final ConfigurationAdapter adapter = testConfigAdapter(new File(temp, "test-config.yml"));
        config = new SChatConfig(adapter);
        config.load();
    }

    private Channel getTestChannelConfig() {
        return config.get(CHANNELS).stream()
            .filter(channel -> channel.getKey().equals("test"))
            .findFirst().orElseThrow();
    }

    @Test
    void loads_parsed_channel_name() {
        assertThat(getTestChannelConfig().getDisplayName()).isEqualTo(text("Test"));
    }

    @Test
    void loads_defined_channel_settings() {
        assertThat(getTestChannelConfig().getSettings().get(PROTECTED)).isTrue();
    }

    @Test
    void set_values_writes_and_loads_when_reloaded() {
        final TextComponent name = text("Test Name");
        final Channel channel = channelWith("test", builder -> builder.name(name));
        final List<Channel> channels = config.get(CHANNELS);
        channels.add(channel);

        config.set(CHANNELS, channels);
        config.reload();

        assertThat(getTestChannelConfig().getDisplayName()).isEqualTo(name);
    }
}
