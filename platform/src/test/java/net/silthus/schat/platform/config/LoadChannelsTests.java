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

import net.silthus.schat.ChannelHelper;
import net.silthus.schat.channel.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.AssertionHelper.assertUnmodifiable;
import static org.assertj.core.api.Assertions.assertThat;

class LoadChannelsTests {
    private ChannelLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ChannelLoader();
    }

    @Test
    void when_created_loaded_channels_are_empty() {
        assertThat(loader.getLoadedChannels()).isEmpty();
    }

    @Test
    void getLoadedChannels_is_immutable() {
        assertUnmodifiable(loader.getLoadedChannels(), ChannelHelper::randomChannel);
    }

    @Nested class given_single_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = ChannelHelper.randomChannel();
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_channel_then_load_throws() {
            assertNPE(() -> loader.load(null));
        }

        @Test
        void then_load_adds_channel_to_list_of_channels() {
            loader.load(channel);
            assertThat(loader.getLoadedChannels()).contains(channel);
        }
    }
}
