/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.platform.config;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelHelper;
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
