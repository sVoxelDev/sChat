package net.silthus.schat.bukkit.adapter;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.schat.bukkit.BukkitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BukkitConnectionListenerTests extends BukkitTests {

    @BeforeEach
    void setUp() {
        loadSChatPlugin();
    }

    @Nested class onPlayerJoin {
        @Test
        void when_player_joins_then_chatter_is_created() {
            final PlayerMock player = server.addPlayer();
            assertThat(sChat().chatterRepository().contains(player.getUniqueId())).isTrue();
        }
    }
}
