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

package net.silthus.schat.platform.plugin;

import java.io.File;
import net.silthus.schat.platform.TestConfigurationAdapter;
import net.silthus.schat.platform.config.SChatConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelManagerTests {

    private ChannelManager channelManager;

    @BeforeEach
    void setUp(@TempDir File temp) {
        final SChatConfig config = new SChatConfig(new TestConfigurationAdapter(new File(temp, "config.yml")));
        channelManager = new ChannelManager(config, createInMemoryChannelRepository());
    }

    @Test
    void loads_channels_from_config() {
        channelManager.load();
        assertThat(channelManager.all()).isNotEmpty();
    }
}
