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

package net.silthus.schat.core;

import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.Channels;
import net.silthus.schat.core.channel.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelsApiTests {

    private static final String TEST_CHANNEL_ALIAS = "test";

    private Channels channels;

    @BeforeEach
    void setUp() {
        channels = new ChannelRepository().getApiProxy();
    }

    private net.silthus.schat.Channel createChannel() {
        return channels.create(TEST_CHANNEL_ALIAS);
    }

    @Test
    void all_isEmpty_byDefault() {
        assertThat(channels.all()).isEmpty();
    }

    @Test
    void create_createsNewChannel() {
        assertThat(createChannel()).isNotNull();
    }

    @Test
    void create_addsChannelToRegistry() {
        final net.silthus.schat.Channel channel = createChannel();
        assertThat(channels.all()).contains(channel);
        assertThat(channels.contains(TEST_CHANNEL_ALIAS)).isTrue();
    }

    @Test
    void create_sameAlias_throws() {
        createChannel();
        assertThatExceptionOfType(Channels.DuplicateAlias.class)
            .isThrownBy(this::createChannel);
    }

    @Test
    void create_ignoresCase() {
        createChannel();
        assertThatExceptionOfType(Channels.DuplicateAlias.class)
            .isThrownBy(() -> channels.create(TEST_CHANNEL_ALIAS.toUpperCase()));
    }

    @Test
    void get_getsChannelByAlias() {
        final net.silthus.schat.Channel channel = createChannel();
        assertThat(channels.get(TEST_CHANNEL_ALIAS))
            .isPresent().get()
            .isEqualTo(channel);
    }

    @Test
    void get_ignoreCase_getsChannel() {
        final net.silthus.schat.Channel channel = createChannel();
        assertThat(channels.get(TEST_CHANNEL_ALIAS.toUpperCase()))
            .isPresent().get()
            .isEqualTo(channel);
    }

    @Test
    void createChannel_withDisplayName() {
        final TextComponent displayName = text("Foobar");
        final net.silthus.schat.Channel channel = channels.create(TEST_CHANNEL_ALIAS, displayName);
        assertThat(channel.getDisplayName()).isEqualTo(displayName);
    }
}
