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

package net.silthus.schat.commands;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.messenger.Messenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.GLOBAL;
import static net.silthus.schat.channel.ChannelSettings.HIDDEN;
import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CreatePrivateChannelCommandTest {

    private final Messenger messenger = mock(Messenger.class);

    @BeforeEach
    void setUp() {
        CreatePrivateChannelCommand.prototype(builder -> builder.messenger(messenger).channelRepository(createInMemoryChannelRepository()));
    }

    @Test
    void private_channel_key_is_same_for_same_targets() {
        final ChatterMock chatter1 = randomChatter();
        final ChatterMock chatter2 = randomChatter();
        final Channel channel1 = createPrivateChannel(chatter1, chatter2).channel();
        final Channel channel2 = createPrivateChannel(chatter2, chatter1).channel();
        assertThat(channel1).isEqualTo(channel2);
    }

    @Test
    void private_channel_key_is_different_for_different_targets() {
        final ChatterMock chatter1 = randomChatter();
        final ChatterMock chatter2 = randomChatter();
        final ChatterMock chatter3 = randomChatter();
        final Channel channel1 = createPrivateChannel(chatter1, chatter2).channel();
        final Channel channel2 = createPrivateChannel(chatter2, chatter3).channel();
        assertThat(channel1).isNotEqualTo(channel2);
    }

    @Test
    void channel_settings() {
        Channel channel = createPrivateChannel(randomChatter(), randomChatter()).channel();
        assertThat(channel.is(PRIVATE)).isTrue();
        assertThat(channel.is(HIDDEN)).isTrue();
        assertThat(channel.is(PROTECTED)).isTrue();
        assertThat(channel.is(GLOBAL)).isTrue();
    }

    @Test
    void channel_name_is_combined_tagets_name() {
        ChatterMock source = randomChatter();
        ChatterMock target = randomChatter();
        Channel channel = createPrivateChannel(source, target).channel();
        assertThat(channel.displayName()).isEqualTo(source.displayName()
            .append(text("<->"))
            .append(target.displayName()));
    }
}
