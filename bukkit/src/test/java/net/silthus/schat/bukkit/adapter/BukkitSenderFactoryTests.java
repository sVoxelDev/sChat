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

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.silthus.schat.bukkit.BukkitTests;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.platform.sender.SenderFactory;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BukkitSenderFactoryTests extends BukkitTests {

    private BukkitSenderFactory factory;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        factory = new BukkitSenderFactory(audiences, mock(SchedulerAdapter.class));
        player = server.addPlayer();
    }

    private Sender chatter() {
        return factory.wrap(player);
    }

    private void assertDisplayName(Component name) {
        assertThat(chatter().displayName()).isEqualTo(name);
    }

    @Nested class wrap {

        @Test
        void is_not_null() {
            assertThat(chatter()).isNotNull();
        }

        @Test
        void has_same_id_as_player() {
            assertThat(chatter().uniqueId()).isEqualTo(player.getUniqueId());
        }

        @Test
        void has_same_name_as_player() {
            assertThat(chatter().name()).isEqualTo(player.getName());
        }

        @Test
        void has_same_display_name_as_player() {
            assertDisplayName(text(player.getDisplayName()));
        }

        @Nested class given_player_display_name_changes {
            @BeforeEach
            void setUp() {
                player.setDisplayName("My Name");
            }

            @Test
            void then_user_display_name_changes() {
                final Component name = text("My Name");
                assertDisplayName(name);
            }
        }

        @Nested class given_formatted_player_display_name {
            @BeforeEach
            void setUp() {
                player.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Mr." + ChatColor.GREEN + " Bob");
            }

            @Test
            void then_user_display_name_is_formatted() {
                assertDisplayName(text().append(text("Mr.", RED, BOLD)).append(text(" Bob", GREEN)).build());
            }
        }

        @Nested class given_player_with_no_permissions {
            @Test
            void then_hasPermission_returns_false() {
                assertThat(chatter().hasPermission("foobar")).isFalse();
            }
        }

        @Nested class given_player_with_permission {
            @BeforeEach
            void setUp() {
                player.addAttachment(mockPlugin, "foobar", true);
            }

            @Test
            void then_user_hasPermission_returns_true() {
                assertThat(chatter().hasPermission("foobar")).isTrue();
            }
        }

        @Nested class when_sendMessage_is_called {
            @BeforeEach
            void setUp() {
                chatter().sendMessage(text("Hi"));
            }

            @Test
            void then_player_receives_message() {
                assertThat(player.nextMessage()).isEqualTo("Hi");
            }
        }

        @Nested class given_console_sender {
            private ConsoleCommandSenderMock consoleSender;
            private Sender console;

            @BeforeEach
            void setUp() {
                consoleSender = (ConsoleCommandSenderMock) server.getConsoleSender();
                console = factory.wrap(consoleSender);
            }

            @Test
            void then_uses_console_properties() {
                assertThat(console.identity()).isEqualTo(SenderFactory.CONSOLE);
            }

            @Test
            void then_sendMessage_sends_message_to_console() {
                console.sendMessage(text("Hi"));
                assertLastMessageIs(consoleSender, "Hi");
            }
        }
    }
}
