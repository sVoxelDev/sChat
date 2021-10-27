package net.silthus.chat.listeners;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.ChatManager;
import net.silthus.chat.Chatter;
import net.silthus.chat.TestBase;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerListenerTest extends TestBase {

    private ChatManager manager;
    private PlayerListener listener;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = plugin.getChatManager();
        listener = plugin.getPlayerListener();
    }

    @Test
    void registerListener() {

        assertThat(listener).isNotNull();
        assertThat(getRegisteredListeners())
                .contains(listener);
    }

    @Test
    void playerIsAddedToChatters_onJoin() {

        Chatter chatter = Chatter.of(server.addPlayer());

        assertThat(manager.getChatters())
                .hasSize(1)
                .containsExactly(chatter);
        assertThat(getRegisteredListeners())
                .contains(chatter);
    }

    @Test
    void onQuit_removesPlayerFromChatters() {

        PlayerMock player = server.addPlayer();
        assertThat(manager.getChatters()).hasSize(1);

        listener.onQuit(new PlayerQuitEvent(player, "bye"));
        assertThat(manager.getChatters()).isEmpty();
        assertThat(getRegisteredListeners())
                .doesNotContain(Chatter.of(player));
    }

    @Test
    void onQuit_noRegisteredPlayer_doesNothing() {

        PlayerMock player = new PlayerMock(server, "test");

        listener.onQuit(new PlayerQuitEvent(player, "bye"));
        assertThat(manager.getChatters()).isEmpty();
        assertThat(getRegisteredListeners())
                .doesNotContain(Chatter.of(player));
    }
}