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

package net.silthus.schat.bukkit;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.silthus.schat.bukkit.adapter.BukkitUserFactory;
import net.silthus.schat.message.Message;
import net.silthus.schat.user.User;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static org.assertj.core.api.Assertions.assertThat;

class BukkitUserFactoryTests extends BukkitTests {

    private BukkitUserFactory factory;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        factory = new BukkitUserFactory(audiences);
        player = server.addPlayer();
    }

    private User user() {
        return factory.getUser(player);
    }

    private void assertDisplayName(Component name) {
        assertThat(user().getDisplayName()).isEqualTo(name);
    }

    @Nested class getUser {

        @Test
        void is_not_null() {
            assertThat(user()).isNotNull();
        }

        @Test
        void has_same_id_as_player() {
            assertThat(user().getUniqueId()).isEqualTo(player.getUniqueId());
        }

        @Test
        void has_same_name_as_player() {
            assertThat(user().getName()).isEqualTo(player.getName());
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
                assertThat(user().hasPermission("foobar")).isFalse();
            }
        }

        @Nested class given_player_with_permission {
            @BeforeEach
            void setUp() {
                player.addAttachment(mockPlugin, "foobar", true);
            }

            @Test
            void then_user_hasPermission_returns_true() {
                assertThat(user().hasPermission("foobar")).isTrue();
            }
        }

        @Nested class when_sendMessage_is_called {
            @BeforeEach
            void setUp() {
                user().sendMessage(Message.message("Hi").create());
            }

            @Test
            void then_player_receives_message() {
                assertThat(player.nextMessage()).isEqualTo("Hi");
            }
        }
    }
}
