package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ChannelManagerTests extends TestBase {

    private ChannelManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = plugin.getChannelManager();
    }

    @Test
    void onEnable_createsChannelManager() {

        assertThat(manager)
                .isNotNull()
                .isInstanceOf(Listener.class);
        assertThat(getRegisteredListeners())
                .contains(manager);
    }

    @Test
    void getChannels_isEmptyBeforeLoad() {

        ChannelManager manager = new ChannelManager(plugin);
        assertThat(manager.getChannels()).isEmpty();
    }

    @Test
    void getChannels_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> manager.getChannels().add(new Channel("test")));
    }

    @Test
    void load_loadsChannelsFromConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("channels.test1.name", "Test 1");
        cfg.set("channels.test2.name", "Test 2");
        manager.load(cfg);

        assertThat(manager.getChannels())
                .hasSize(2);
    }

    @Test
    void getChatters_isEmpty_afterInit() {

        assertThat(manager.getChatters()).isEmpty();
    }

    @Test
    void getChatters_isImmutable() {

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> manager.getChatters().add(Chatter.of(server.addPlayer())));
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

        manager.onQuit(new PlayerQuitEvent(player, "bye"));
        assertThat(manager.getChatters()).isEmpty();
        assertThat(getRegisteredListeners())
                .doesNotContain(Chatter.of(player));
    }

    @Test
    void onQuit_noRegisteredPlayer_doesNothing() {

        PlayerMock player = new PlayerMock(server, "test");

        manager.onQuit(new PlayerQuitEvent(player, "bye"));
        assertThat(manager.getChatters()).isEmpty();
        assertThat(getRegisteredListeners())
                .doesNotContain(Chatter.of(player));
    }
}
