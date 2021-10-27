package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ChannelManagerTests extends TestBase {

    private ChannelManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        HandlerList.unregisterAll(plugin.getChannelManager());
        manager = new ChannelManager(plugin);
        Bukkit.getPluginManager().registerEvents(manager, plugin);
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
        assertThat(manager.getChannels()).isEmpty();
    }

    @Test
    void getChannels_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> manager.getChannels().add(new Channel("test")));
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
    void getChatter_createsChatterOnTheFly() {
        PlayerMock player = new PlayerMock(server, "test");
        Chatter chatter = manager.getChatter(player);
        assertThat(chatter)
                .isNotNull()
                .extracting(Chatter::getUniqueId)
                .isEqualTo(player.getUniqueId());
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void getChatter_doesNotCreateDuplicateChatter() {
        PlayerMock player = server.addPlayer();
        assertThat(manager.getChatters()).hasSize(1);
        Chatter chatter = manager.getChatter(player);
        assertThat(manager.getChatters()).hasSize(1);
        assertThat(chatter)
                .isNotNull()
                .extracting(Chatter::getUniqueId)
                .isEqualTo(player.getUniqueId());
    }

    @Nested
    class Load {

        @Test
        void load_loadsChannelsFromConfig() {

            loadTwoChannels();

            assertThat(manager.getChannels())
                    .hasSize(2);
        }

        @Test
        void load_doesNotError_ifConfigSectionIsEmpty() {
            assertThatCode(this::loadFromEmptyChannelConfig)
                    .doesNotThrowAnyException();
            assertThat(manager.getChannels()).isEmpty();
        }

        @Test
        void load_clearsPreviousChannels() {
            loadTwoChannels();
            assertThat(manager.getChannels()).hasSize(2);

            loadFromEmptyChannelConfig();
            assertThat(manager.getChannels()).isEmpty();
        }

        private void loadTwoChannels() {
            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("channels.test1.name", "Test 1");
            cfg.set("channels.test2.name", "Test 2");
            manager.load(new PluginConfig(cfg));
        }

        private void loadFromEmptyChannelConfig() {
            manager.load(new PluginConfig(new MemoryConfiguration()));
        }

    }

    @Nested
    class Listeners {

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
}
