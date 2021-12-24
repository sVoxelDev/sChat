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

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRegistry;
import net.silthus.schat.handler.types.JoinGameHandler;
import net.silthus.schat.handler.types.PermissionHandler;
import net.silthus.schat.handler.types.UserJoinChannelHandler;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.chatter.ChatterRegistry.createInMemoryChatterRegistry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserChatterTests {

    private static final String NAME = "Player";
    private static final @NotNull Component DISPLAY_NAME = text("Player 1");

    private ChatterRegistry registry;
    private User user;
    private PermissionHandler permissionHandler;

    @BeforeEach
    void setUp() {
        registry = createInMemoryChatterRegistry();
        permissionHandler = mock(PermissionHandler.class);
        user = new User(Identity.identity(UUID.randomUUID(), NAME, DISPLAY_NAME), permissionHandler);

        joinGame();
    }

    @NotNull
    private Chatter chatter() {
        return registry.get(user.getUniqueId()).orElseThrow();
    }

    private void joinGame() {
        new JoinGameHandler.Default(registry, UserJoinChannelHandler::new).joinGame(user);
    }

    @NotNull
    private Channel joinChannel() {
        return joinChannel(createChannel("test"));
    }

    @NotNull
    private Channel joinChannel(Channel channel) {
        chatter().join(channel);
        return channel;
    }

    private void mockPermission(String permission, boolean result) {
        when(permissionHandler.hasPermission(permission)).thenReturn(result);
    }

    private void mockNoPermission() {
        mockPermission(anyString(), false);
    }

    private void mockPermission() {
        mockPermission(anyString(), true);
    }

    private void joinChannelAndAssertSuccess() {
        final Channel channel = joinChannel();
        assertThat(chatter().getChannels()).contains(channel);
    }

    private void joinChannelAndAssertException(Class<? extends RuntimeException> exception) {
        assertThatExceptionOfType(exception)
            .isThrownBy(UserChatterTests.this::joinChannel);
    }

    @Nested
    class JoinGame {

        @Test
        void adds_chatter_to_registry() {
            assertThat(registry.contains(user.getUniqueId())).isTrue();
        }

        @Test
        void sets_player_name() {
            assertThat(chatter().getName()).isEqualTo(NAME);
        }

        @Test
        void sets_player_display_name() {
            assertThat(chatter().getDisplayName()).isEqualTo(DISPLAY_NAME);
        }

    }

    @Nested
    class JoinChannel {

        @Test
        void givenNoJoinPermission_joinChannel_throws() {
            mockNoPermission();
            joinChannelAndAssertException(Channel.AccessDenied.class);
        }

        @Test
        void givenPermission_joinChannel_isAllowed() {
            mockPermission();
            joinChannelAndAssertSuccess();
        }

        @Test
        void given_userWithoutPermission_joins_public_channel_isAllowed() {
            mockNoPermission();
            final Channel channel = joinChannel(Channel.channel("test").setting(Channel.PUBLIC, true).create());
            assertThat(chatter().getChannels()).contains(channel);
        }
    }
}
