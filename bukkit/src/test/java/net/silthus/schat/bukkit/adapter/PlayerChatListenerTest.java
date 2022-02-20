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
package net.silthus.schat.bukkit.adapter;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.Optional;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.silthus.schat.bukkit.BukkitTests;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.locale.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.bukkit.adapter.BukkitIdentityAdapter.identity;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static org.assertj.core.api.Assertions.assertThat;

class PlayerChatListenerTest extends BukkitTests {

    private Chatter chatter;
    private PlayerMock player;
    private Component lastMessage;

    @BeforeEach
    void setUp() {
        player = server.addPlayer();
        chatter = Chatter.chatterBuilder(identity(player))
            .viewConnector(c -> () -> handleMessage(c))
            .create();
        PlayerChatListener listener = new PlayerChatListener(createInMemoryChatterRepository());
        server.getPluginManager().registerEvents(listener, mockPlugin);
    }

    private void handleMessage(Chatter chatter) {
        lastMessage = Optional.ofNullable(chatter.messages().last())
            .map(Message::text)
            .orElse(null);
    }

    private void chat() {
        player.chat("Hi");
    }

    @Nested class given_no_active_channel {
        @BeforeEach
        void setUp() {
            chatter.activeChannel(null);
        }

        @SneakyThrows
        @Test
        @Disabled("Async Chat Event not yet supported in MockBukkit: https://github.com/MockBukkit/MockBukkit/pull/297")
        void when_player_chats_sends_error_to_player() {
            chat();
            assertThat(lastMessage).isEqualTo(Messages.CANNOT_CHAT_NO_ACTIVE_CHANNEL.build());
        }
    }

    @Nested class given_active_channel {
        @BeforeEach
        void setUp() {
            chatter.activeChannel(randomChannel());
        }
    }
}
