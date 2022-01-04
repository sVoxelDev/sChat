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

package net.silthus.schat;

import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChannelTests {

    private Channel channel;
    private Message message;

    @BeforeEach
    void setUp() {
        channel = Channel.createChannel("test");
        message = Message.message("Hi!");
    }

    private Chatter joinChatter() {
        final Chatter chatter = mock(Chatter.class);
        channel.addTarget(chatter);
        return chatter;
    }

    private void sendMessage() {
        channel.sendMessage(message);
    }

    @Test
    void sendMessage_addsMessageToChannel() {
        sendMessage();
        assertThat(channel.getMessages()).contains(message);
    }

    @Test
    void givenChatter_joinedChannel_sendsMessageToChatter() {
        final Chatter chatter = joinChatter();
        sendMessage();
        verify(chatter).sendMessage(message);
    }

    @Test
    void givenTwoChatters_joinedChannel_sendsMessageToAll() {
        final Chatter chatter1 = joinChatter();
        final Chatter chatter2 = joinChatter();
        sendMessage();
        verify(chatter1).sendMessage(message);
        verify(chatter2).sendMessage(message);
    }

    @Test
    void givenJoinedTwice_onlySendsOnce() {
        final Chatter chatter = joinChatter();
        channel.addTarget(chatter);
        sendMessage();
        verify(chatter).sendMessage(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "   ",
        "AAAA",
        "a b",
        "*!"
    })
    void givenInvalidKey_throws(String key) {
        assertThatExceptionOfType(Channel.InvalidKey.class)
            .isThrownBy(() -> Channel.createChannel(key));
    }

    @Test
    void givenNoDisplayName_usesKey() {
        assertThat(channel.getDisplayName()).isEqualTo(text("test"));
    }

    @Test
    void givenEmptyDisplayName_usesKey() {
        channel = Channel.channel("test").displayName(empty()).create();
        assertThat(channel.getDisplayName()).isEqualTo(text("test"));
    }

    @Test
    void givenDisplayName_formatsUsingDisplayName() {
        final TextComponent displayName = text("My Channel");
        channel = Channel.channel("test").displayName(displayName).create();
        assertThat(channel.getDisplayName()).isEqualTo(displayName);
    }

}
