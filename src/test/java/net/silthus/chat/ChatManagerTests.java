package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.config.PluginConfig;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ChatManagerTests extends TestBase {

    private ChatManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = new ChatManager(plugin);
    }

    @Test
    void onEnable_createsChannelManager() {

        assertThat(plugin.getChatManager())
                .isNotNull();
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
        manager.registerChatter(player);
        assertThat(manager.getChatters()).hasSize(1);

        Chatter chatter = manager.getChatter(player);

        assertThat(manager.getChatters()).hasSize(1);
        assertThat(chatter)
                .isNotNull()
                .extracting(Chatter::getUniqueId)
                .isEqualTo(player.getUniqueId());
    }

    @Test
    void registerChatter_addsPlayerToChatterCache() {

        PlayerMock player = new PlayerMock(server, "test");
        manager.registerChatter(player);

        assertThat(manager.getChatters())
                .hasSize(1)
                .containsOnly(Chatter.of(player));
    }

    @Test
    void registerChatter_registersChatListeners() {

        Chatter chatter = manager.registerChatter(new PlayerMock(server, "test"));

        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void unregisterChatter_removesChatterFromCache() {

        PlayerMock player = new PlayerMock(server, "test");
        manager.registerChatter(player);
        assertThat(manager.getChatters()).hasSize(1);

        manager.unregisterChatter(player);
        assertThat(manager.getChatters()).isEmpty();
    }

    @Test
    void unregisterChatter_removesChatterFromListeners() {

        PlayerMock player = new PlayerMock(server, "test");
        Chatter chatter = manager.registerChatter(player);
        assertThat(getRegisteredListeners()).contains(chatter);

        manager.unregisterChatter(player);
        assertThat(getRegisteredListeners()).doesNotContain(chatter);
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
}
