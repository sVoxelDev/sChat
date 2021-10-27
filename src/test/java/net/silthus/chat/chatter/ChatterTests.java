package net.silthus.chat.chatter;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ChatterTests extends TestBase {

    private PlayerMock player;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        chatter = spy(Chatter.of(player));
    }

    @Test
    void create_PlayerChatter() {
        assertThat(chatter)
                .isInstanceOf(Listener.class)
                .extracting(
                        Chatter::getUniqueId,
                        Chatter::getDisplayName
                ).contains(
                        player.getUniqueId(),
                        player.getDisplayName()
                );
    }

    @Test
    void create_registersOnChatListener() {

        Bukkit.getPluginManager().registerEvents(chatter, plugin);
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void equals_isTrue_forSamePlayer() {

        PlayerMock player = server.addPlayer();

        Chatter chatter0 = Chatter.of(player);
        Chatter chatter1 = Chatter.of(player);

        assertThat(chatter0).isEqualTo(chatter1);
    }

    @Test
    void sendMessage_sendsMessageToPlayer() {
        chatter.sendMessage("Hello Chatter!");

        assertReceivedMessage(player, "Hello Chatter!");
    }

    @Test
    void sendMessage_formatsTheMessageIfNotFormatted() {
        sendMessage(server.addPlayer(), "Hi");

        assertReceivedMessage(player, "Player1: Hi");
    }

    @Test
    void sendMessage_storesLastMessage() {
        PlayerMock sender = server.addPlayer();
        sendMessage(sender, "Hi there");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::message)
                .isEqualTo("Hi there");
    }

    @Test
    void getFocusedChannel() {
        Channel channel = chatter.getActiveChannel();

        assertThat(channel).isNull();
    }

    @Test
    void setFocusedChannel_setsChannel() {
        Channel channel = new Channel("test");
        chatter.setActiveChannel(channel);

        assertThat(chatter.getActiveChannel())
                .isEqualTo(channel);
    }

    private void sendMessage(Player source, String message) {
        chatter.sendMessage(Message.of(ChatSource.of(source), message));
    }

    @Nested
    @Disabled
    @DisplayName("with player chatting")
    class PlayerChatEvent {

        private ArgumentCaptor<AsyncPlayerChatEvent> eventCaptor;

        @BeforeEach
        void setUp() {
            Bukkit.getPluginManager().registerEvents(chatter, plugin);
            eventCaptor = ArgumentCaptor.forClass(AsyncPlayerChatEvent.class);
        }

        @Test
        void onChat_catchesChatEvent() {

            AsyncPlayerChatEvent event = chat("Hello!");

            assertThat(event.getMessage()).isEqualTo("Hello!");
        }

        @Test
        void onChat_forwardsMessageToActiveChannel() {

            Channel channel = new Channel("test");
            chatter.setActiveChannel(channel);
            AsyncPlayerChatEvent event = chat("Hi");

            assertThat(channel.getLastReceivedMessage())
                    .isNotNull()
                    .extracting(Message::formattedMessage)
                    .isEqualTo("Player0: Hi");
            assertThat(event.isCancelled()).isTrue();
        }

        @Test
        void onChat_withNoActiveChannel_sendsPlayerAnErrorMessage() {

            AsyncPlayerChatEvent event = chat("Hi");

            assertThat(player.nextMessage())
                    .isEqualTo(Constants.Errors.NO_ACTIVE_CHANNEL);
            assertThat(chatter.getLastReceivedMessage()).isNull();
            assertThat(event.isCancelled()).isTrue();
        }

        @Test
        void onChat_onlyReactsToChatter() {

            Channel channel = new Channel("test");
            chatter.setActiveChannel(channel);

            PlayerMock player2 = server.addPlayer();
            AsyncPlayerChatEvent event = chat(player2, "hi");

            assertThat(event.isCancelled()).isFalse();
            assertThat(player2.nextMessage()).isNull();
            assertThat(channel.getLastReceivedMessage()).isNull();
        }

        private AsyncPlayerChatEvent chat(String message) {
            return chat(player, message);
        }

        private AsyncPlayerChatEvent chat(Player player, String message) {
            player.chat(message);
            verify(chatter).onPlayerChat(eventCaptor.capture());
            return eventCaptor.getValue();
        }
    }
}
