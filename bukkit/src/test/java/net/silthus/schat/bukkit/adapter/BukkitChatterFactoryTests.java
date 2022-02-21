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
import java.util.UUID;
import net.silthus.schat.bukkit.BukkitTests;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.commands.SendMessageCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.ui.ViewProviderStub;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

class BukkitChatterFactoryTests extends BukkitTests {

    private BukkitChatterFactory factory;

    @BeforeEach
    void setUp() {
        factory = new BukkitChatterFactory(audiences, ViewProviderStub.viewProviderStub());
        SendMessageCommand.prototype(builder -> builder.eventBus(EventBus.empty()));
    }

    private Chatter create(UUID id) {
        return factory.createChatter(id);
    }

    @Nested class given_online_player {
        private PlayerMock player;

        @BeforeEach
        void setUp() {
            player = server.addPlayer();
        }

        private Chatter createChatter() {
            return create(player.getUniqueId());
        }

        @Test
        void then_chatter_name_is_player_name() {
            assertThat(createChatter().name()).isEqualTo(player.getName());
        }

        @Test
        void then_chatter_display_name_is_player_display_name() {
            player.setDisplayName("Bob");
            assertThat(createChatter().displayName()).isEqualTo(text("Bob"));
        }

        @Test
        void given_player_changes_display_name_then_chatter_name_changes() {
            final Chatter chatter = createChatter();
            player.setDisplayName("Bob");
            assertThat(chatter.displayName()).isEqualTo(text("Bob"));
        }

        @Nested class when_message_is_send {
            @BeforeEach
            void setUp() {
                message("Hey").to(createChatter()).send();
            }

            @Test
            void then_player_receives_message() {
                assertLastMessageContains(player, "Hey");
            }
        }
    }

    @Nested class given_offline_player {
        private OfflinePlayer player;

        @BeforeEach
        void setUp() {
            player = server.addPlayer();
        }

        private Chatter createChatter() {
            return create(player.getUniqueId());
        }

        @Test
        void then_player_name_is_chatter_name() {
            assertThat(createChatter().name()).isEqualTo(player.getName());
        }
    }
}
