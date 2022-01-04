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

package net.silthus.schat.channel.usecases;

import java.util.List;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.repository.ChannelRepository;
import net.silthus.schat.settings.Settings;
import net.silthus.schat.usecases.ChannelConfig;
import net.silthus.schat.usecases.LoadChannels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.channel.repository.ChannelRepository.createInMemoryChannelRepository;
import static org.assertj.core.api.Assertions.assertThat;

class LoadChannelsTest {

    private LoadChannels useCase;
    private ChannelRepository repository;

    @BeforeEach
    void setUp() {
        useCase = new LoadChannels() {};
        repository = createInMemoryChannelRepository();
    }

    @Test
    void loads_channels_from_config() {
        final ChannelConfig config = new ChannelConfig();
        config.setKey("test");
        config.setName(text("Test"));
        config.setSettings(Settings.settings().withStatic(REQUIRES_JOIN_PERMISSION, false).create());
        useCase.load(LoadChannels.Args.of(repository, List.of(
            config
        )));

        assertThat(repository.all()).singleElement()
            .extracting(
                Channel::getKey,
                Channel::getDisplayName
            ).contains(
                "test",
                text("Test")
            );
        assertThat(repository.get("test").get(REQUIRES_JOIN_PERMISSION)).isFalse();
    }
}