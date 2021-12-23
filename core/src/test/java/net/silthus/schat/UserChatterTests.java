package net.silthus.schat;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRegistry;
import net.silthus.schat.handler.types.DefaultJoinGameHandler;
import net.silthus.schat.handler.types.UserJoinChannelHandler;
import net.silthus.schat.identity.Identity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.chatter.ChatterRegistry.createInMemoryRegistry;
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
    private UUID playerId;

    @BeforeEach
    void setUp() {
        registry = createInMemoryRegistry();
        playerId = UUID.randomUUID();
        user = mock(User.class);
        when(user.getIdentity()).thenReturn(Identity.identity(playerId, NAME, DISPLAY_NAME));
    }

    private void joinGame() {
        new DefaultJoinGameHandler(registry, UserJoinChannelHandler::new).joinGame(user);
    }

    @Nested
    class JoinGame {

        @BeforeEach
        void setUp() {
            joinGame();
        }

        @Test
        void adds_chatter_to_registry() {
            assertThat(registry.contains(playerId)).isTrue();
        }

        @Test
        void sets_player_name() {
            assertThat(registry.get(playerId))
                .isPresent().get()
                .extracting(Chatter::getName).isEqualTo(NAME);
        }

        @Test
        void sets_player_display_name() {
            assertThat(registry.get(playerId))
                .isPresent().get()
                .extracting(Chatter::getDisplayName).isEqualTo(DISPLAY_NAME);
        }

    }

    @Test
    void givenNoJoinPermission_joinChannel_throws() {
        joinGame();
        final Chatter chatter = registry.get(playerId).get();
        when(user.hasPermission(anyString())).thenReturn(false);
        assertThatExceptionOfType(Channel.AccessDenied.class)
            .isThrownBy(() -> chatter.join(createChannel("test")));
    }
}
