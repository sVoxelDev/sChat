package net.silthus.schat.bukkit.adapter;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.UUID;
import net.silthus.schat.bukkit.BukkitTests;
import net.silthus.schat.chatter.Chatter;
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
        factory = new BukkitChatterFactory(audiences, viewProvider);
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
            assertThat(createChatter().getName()).isEqualTo(player.getName());
        }

        @Test
        void then_chatter_display_name_is_player_display_name() {
            player.setDisplayName("Bob");
            assertThat(createChatter().getDisplayName()).isEqualTo(text("Bob"));
        }

        @Test
        void given_player_changes_display_name_then_chatter_name_changes() {
            final Chatter chatter = createChatter();
            player.setDisplayName("Bob");
            assertThat(chatter.getDisplayName()).isEqualTo(text("Bob"));
        }

        @Nested class when_message_is_send {
            @BeforeEach
            void setUp() {
                createChatter().sendMessage(message("Hey"));
            }

            @Test
            void then_player_receives_message() {
                assertLastMessageIs(player, "Hey");
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
            assertThat(createChatter().getName()).isEqualTo(player.getName());
        }
    }
}
