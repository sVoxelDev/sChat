package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static net.silthus.chat.Message.message;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChatterTests extends TestBase {
    private PlayerMock player;

    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.Permissions.getChannelPermission(ChatTarget.channel("test")), true);
        chatter = plugin.getChatterManager().registerChatter(spy(Chatter.of(player)));
    }

    private void sendMessage(Player source, String message) {
        ChatSource.player(source).message(message).to(chatter).send();
    }

    @Test
    void create_PlayerChatter() {
        assertThat(chatter)
                .isInstanceOf(Listener.class)
                .extracting(
                        Chatter::getIdentifier,
                        Chatter::getDisplayName,
                        ChatTarget::getIdentifier
                ).contains(
                        player.getUniqueId().toString(),
                        player.getDisplayName(),
                        player.getUniqueId().toString()
                );
    }

    @Test
    void create_registersOnChatListener() {

        Bukkit.getPluginManager().registerEvents(chatter, plugin);
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void of_usesGlobalChatterCache() {
        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        Message message = message("test").send();
        chatter.addReceivedMessage(message);

        Chatter newChatter = Chatter.of(player);
        assertThat(newChatter).isSameAs(chatter);
        assertThat(newChatter.getLastReceivedMessage()).isEqualTo(message);
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
                .extracting(this::toText)
                .isEqualTo("Hi there");
    }

    @Test
    void getActiveChannel() {
        Channel channel = chatter.getActiveChannel();

        assertThat(channel).isNull();
    }

    @Test
    void setActiveChannel_setsChannel() {
        Channel channel = ChatTarget.channel("test");
        chatter.setActiveChannel(channel);

        assertThat(chatter.getActiveChannel())
                .isEqualTo(channel);
    }

    @Test
    void setActiveChannel_addsPlayerAsTargetToChannel() {
        Channel channel = ChatTarget.channel("test");
        assertThat(channel.getTargets()).isEmpty();
        chatter.setActiveChannel(channel);
        assertThat(channel.getTargets()).contains(chatter);
    }

    @Test
    void setActiveChannel_toNull() {
        Channel channel = ChatTarget.channel("test");
        chatter.setActiveChannel(channel);
        chatter.setActiveChannel(null);
        assertThat(chatter.getActiveChannel()).isNull();
    }

    @Test
    void subscribe_addsChatterAsChannelTarget() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        assertThat(channel.getTargets()).contains(chatter);
        assertThat(chatter.getSubscriptions()).contains(channel);
    }

    @Test
    void subscribe_onlyAddsChatterOnce() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);
        chatter.subscribe(channel);

        assertThat(channel.getTargets()).hasSize(1);
        assertThat(chatter.getSubscriptions()).containsOnlyOnce(channel);
    }

    @Test
    void subscribe_sendsMessageToSubscriber() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        channel.sendMessage("test");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(this::toText)
                .isEqualTo("test");
    }

    @Test
    void not_subscribed_doesNotSendMessageToChatter() {
        Channel channel = ChatTarget.channel("test");
        channel.sendMessage("test");

        assertThat(chatter.getLastReceivedMessage()).isNull();
    }

    @Test
    void unsubscribe_doesNothingIfNotSubscribed() {

        assertThatCode(() -> chatter.unsubscribe(ChatTarget.channel("test")))
                .doesNotThrowAnyException();
    }

    @Test
    void unsubscribe_removesSubscription() {

        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);
        assertThat(chatter.getSubscriptions()).contains(channel);

        chatter.unsubscribe(channel);
        assertThat(chatter.getSubscriptions()).doesNotContain(channel);
    }

    @Test
    void unsubscribe_doesNotSendMessageToOldSubscriber() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        chatter.unsubscribe(channel);
        channel.sendMessage("test");
        assertThat(chatter.getLastReceivedMessage()).isNull();
    }

    @Test
    void join_subscribesAndSetsActiveChannel() throws AccessDeniedException {

        Channel channel = ChatTarget.channel("test");
        chatter.join(channel);
        assertThat(chatter.getSubscriptions()).contains(channel);
        assertThat(channel.getTargets()).contains(chatter);
        assertThat(chatter.getActiveChannel()).isSameAs(channel);
    }

    @Test
    void join_throwsIfPlayerCannotJoinChannel() {

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> chatter.join(createChannel("foo", cfg -> cfg.protect(true))));
    }

    @Test
    void canJoin_returnsTrueIfPlayerCanJoinTheChannel() {
        assertThat(chatter.canJoin(ChatTarget.channel("test"))).isTrue();
    }

    @Test
    void canJoin_isFalse_ifPlayerHasNoPermission() {
        assertThat(chatter.canJoin(createChannel("foobar", cfg -> cfg.protect(true)))).isFalse();
    }

    @Nested
    @DisplayName("with player chatting")
    class PlayerChatEvent {

        private ArgumentCaptor<AsyncPlayerChatEvent> eventCaptor;

        @BeforeEach
        public void setUp() {
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

            Channel channel = ChatTarget.channel("test");
            chatter.setActiveChannel(channel);
            AsyncPlayerChatEvent event = chat("Hi");

            assertThat(channel.getLastReceivedMessage())
                    .isNotNull()
                    .extracting(ChatterTests.this::toText)
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

            Channel channel = ChatTarget.channel("test");
            chatter.setActiveChannel(channel);

            PlayerMock player2 = new PlayerMock(server, "test");
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
            server.getScheduler().waitAsyncEventsFinished();
            verify(chatter, atLeastOnce()).onPlayerChat(eventCaptor.capture());
            return eventCaptor.getValue();
        }
    }
}
