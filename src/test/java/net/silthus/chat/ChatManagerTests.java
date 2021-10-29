package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ChatManagerTests extends TestBase {

    private ChatManager manager;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        manager = new ChatManager(plugin);
    }

    @Test
    void onEnable_createsChatManager() {

        assertThat(plugin.getChatManager())
                .isNotNull();
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
        Chatter chatter = manager.getOrCreateChatter(player);
        assertThat(chatter)
                .isNotNull()
                .extracting(Chatter::getUniqueId)
                .isEqualTo(player.getUniqueId());
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void getChatter_doesNotCreateDuplicateChatter() {
        PlayerMock player = addChatter();
        assertThat(manager.getChatters()).hasSize(1);

        Chatter chatter = manager.getOrCreateChatter(player);

        assertThat(manager.getChatters()).hasSize(1);
        assertThat(chatter)
                .isNotNull()
                .extracting(Chatter::getUniqueId)
                .isEqualTo(player.getUniqueId());
    }

    @NotNull
    private PlayerMock addChatter() {
        PlayerMock player = server.addPlayer();
        manager.registerChatter(player);
        return player;
    }

    @Test
    void getChatter_byId() {
        PlayerMock player = addChatter();

        assertThat(manager.getChatter(player.getUniqueId()))
                .isNotNull()
                .extracting(Chatter::getUniqueId)
                .isEqualTo(player.getUniqueId());
    }

    @Test
    void getChatter_byName() {
        PlayerMock player = addChatter();

        assertThat(manager.getChatter(player.getName()))
                .isPresent()
                .get().extracting(Chatter::getUniqueId)
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
    class Listeners {

        private ChatManager.PlayerListener listener;

        @BeforeEach
        void setUp() {
            listener = manager.playerListener;
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
        void onJoin_player_autoSubscribes_toConfiguredChannels() {

            Channel channel = new Channel("test");
            plugin.getChannelRegistry().add(channel);
            PlayerMock player = new PlayerMock(server, "test");
            player.addAttachment(plugin, Constants.Permissions.getAutoJoinPermission(channel), true);
            assertThat(channel.getTargets()).isEmpty();

            server.addPlayer(player);
            assertThat(channel.getTargets()).contains(Chatter.of(player));
        }

        @Test
        void onJoin_doesNotSubscribe_toChannelsWithoutPermission() {

            Channel channel = new Channel("test");
            plugin.getChannelRegistry().add(channel);

            PlayerMock player = server.addPlayer();
            Chatter chatter = manager.getOrCreateChatter(player);
            assertThat(chatter.getSubscriptions()).isEmpty();
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
}
