package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.config.ChannelConfig;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ChannelTests extends TestBase {

    private Channel channel;

    @BeforeEach
    public void setUp() {
        super.setUp();

        channel = new Channel("test");
    }

    @Test
    void create() {

        assertThat(channel)
                .isInstanceOf(ChatTarget.class);
        assertThat(channel)
                .extracting(
                        Channel::getIdentifier,
                        Channel::getPermission
                ).contains(
                        "test",
                        Constants.Permissions.CHANNEL_PERMISSION + ".test"
                );
        assertThat(channel.getName())
                .isEqualTo("test");
        assertThat(channel.getConfig())
                .extracting(ChannelConfig::getName)
                .isEqualTo(null);
        assertThat(channel.getConfig().getFormat())
                .isNotNull();
    }

    @Test
    void create_lowerCasesIdentifier() {
        Channel channel = new Channel("TEsT");
        assertThat(channel.getIdentifier()).isEqualTo("test");
    }

    @Test
    void equalsBasedOnAlias() {
        Channel channel1 = new Channel("test");
        Channel channel2 = new Channel("test");

        assertThat(channel1).isEqualTo(channel2);
    }

    @Test
    void hasEmptyTargetList_byDefault() {
        assertThat(channel.getTargets())
                .isEmpty();
    }

    @Test
    void getTargets_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> channel.getTargets().add(Chatter.of(server.addPlayer())));
    }

    @Test
    void join_addsPlayerToChannelTargets() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.add(player);

        assertThat(channel.getTargets())
                .contains(player);
    }

    @Test
    void join_canOnlyJoinChannelOnce() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.add(player);
        channel.add(player);

        assertThat(channel.getTargets())
                .hasSize(1);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void join_throwsNullPointer_ifPlayerIsNull() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> channel.add(null));
    }

    @Test
    void leave_removesChatTarget_fromChannelTargets() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.add(player);
        assertThat(channel.getTargets()).contains(player);

        channel.remove(player);

        assertThat(channel.getTargets()).isEmpty();
    }

    @Test
    void leave_doesNothingIfPlayerIsNotJoined() {

        Chatter player = Chatter.of(server.addPlayer());
        channel.add(player);
        assertThatCode(() -> channel.remove(Chatter.of(server.addPlayer())))
                .doesNotThrowAnyException();
        assertThat(channel.getTargets()).contains(player);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void leave_throwsNullPointer_ifPlayerIsNull() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> channel.remove(null));
    }

    @Test
    void sendMessage_sendsMessageToNobody() {

        PlayerMock player = server.addPlayer();
        channel.sendMessage(Message.message(ChatSource.player(player), "test"));

        assertThat(channel.getTargets()).isEmpty();
        assertThat(player.nextMessage()).isNull();
    }

    @Test
    void sendMessage_sendsMessageToAllJoinedTargets() {

        PlayerMock player0 = server.addPlayer();
        Chatter chatter0 = Chatter.of(player0);
        channel.add(chatter0);
        PlayerMock player1 = server.addPlayer();
        Chatter chatter1 = Chatter.of(player1);
        channel.add(chatter1);
        PlayerMock player2 = server.addPlayer();

        channel.sendMessage(Message.message(chatter0, "test"));

        assertThat(player0.nextMessage()).isEqualTo("Player0: test");
        assertThat(player1.nextMessage()).isEqualTo("Player0: test");
        assertThat(player2.nextMessage()).isNull();
    }

    @Test
    void sendFormattedMessage_doesNotFormatAgain() {
        Message message = Message.message(ChatSource.player(server.addPlayer()), "test");

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.add(chatter);
        channel.sendMessage(message);

        assertThat(player.nextMessage()).isEqualTo("Player0: test");
    }

    @Test
    void sendMessage_storesLastMessage() {
        Message message = Message.message(ChatSource.player(server.addPlayer()), "test");

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.add(chatter);
        channel.sendMessage(message);

        assertThat(channel.getLastReceivedMessage())
                .isNotNull()
                .extracting(
                        Message::getMessage,
                        m -> m.getSource().getDisplayName()
                ).contains(
                        "test",
                        "Player0"
                );
    }

    @Test
    void sendMessage_storesFormattedChannelMessage() {
        Channel channel = new Channel("test", ChannelConfig.builder().format(Format.miniMessage("[<channel_name]<sender_name>: <message>")).build());
        Message message = ChatSource.named("test").message("test").to(channel).send();

        assertThat(channel.getLastReceivedMessage())
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void getLastReceivedMessage_returnsNullByDefault() {

        assertThat(channel.getLastReceivedMessage())
                .isNull();
    }

    @Test
    void getReceivedMessages_isEmpty() {

        assertThat(channel.getReceivedMessages())
                .isEmpty();
    }

    @Test
    void sendMultipleMessage_returnedbyLastMessages() {

        Message message = Message.message(ChatSource.player(server.addPlayer()), "test");

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.add(chatter);
        channel.sendMessage(message);
        channel.sendMessage(Message.message(ChatSource.player(server.addPlayer()), "foobar"));
        channel.sendMessage("Heyho");

        assertThat(channel.getReceivedMessages())
                .hasSize(3)
                .extracting(Message::getMessage)
                .containsExactly(
                        "test",
                        "foobar",
                        "Heyho"
                );
    }

    @Test
    void sendMessage_setsTargetToChannel() {

        Message message = Message.message(ChatSource.player(server.addPlayer()), "test");
        Chatter chatter = Chatter.of(server.addPlayer());
        channel.add(chatter);

        channel.sendMessage(message);

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::getTarget)
                .isSameAs(channel);
    }

    @Test
    void subscribedTarget_isRemoved_onUnregister() {
        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        chatter.subscribe(channel);

        assertThat(channel.getTargets()).contains(chatter);
        plugin.getChatterManager().unregisterChatter(player);
        assertThat(channel.getTargets()).isEmpty();
    }

    @Test
    void canJoin_unprotected_isTrue() {
        Channel channel = createChannel(c -> c.protect(false));

        assertThat(channel.canJoin(server.addPlayer())).isTrue();
    }

    @Test
    void canJoin_protected_withoutPermission_isFalse() {
        Channel channel = createChannel(c -> c.protect(true));

        assertThat(channel.canJoin(server.addPlayer())).isFalse();
    }

    @Test
    void canJoin_protected_withPermission_isTrue() {
        Channel channel = createChannel(c -> c.protect(true));

        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, channel.getPermission(), true);

        assertThat(channel.canJoin(player)).isTrue();
    }

    @Nested
    @DisplayName("with config")
    class WithConfig {

        @Test
        void createFromConfig() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "Test");
            cfg.set("format", "<green><message>");

            Channel channel = new Channel("test", ChannelConfig.of(cfg));

            assertThat(channel.getName())
                    .isEqualTo("Test");
            assertThat(channel.getConfig().getFormat())
                    .isNotNull();
        }

    }
}
