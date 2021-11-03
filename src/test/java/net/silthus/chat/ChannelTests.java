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

        channel = ChatTarget.channel("test");
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
                .extracting(ChannelConfig::name)
                .isEqualTo(null);
        assertThat(channel.getConfig().format())
                .isNotNull();
    }

    @Test
    void create_lowerCasesIdentifier() {
        Channel channel = ChatTarget.channel("TEsT");
        assertThat(channel.getIdentifier()).isEqualTo("test");
    }

    @Test
    void equalsBasedOnAlias() {
        Channel channel1 = ChatTarget.channel("test");
        Channel channel2 = ChatTarget.channel("test");

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
        ChatSource.player(player).message("test").to(channel).send();

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

        chatter0.message("test").to(channel).send();

        assertThat(player0.nextMessage()).contains("test");
        assertThat(player1.nextMessage()).contains("test");
        assertThat(player2.nextMessage()).isNull();
    }

    @Test
    void sendFormattedMessage_doesNotFormatAgain() {

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        chatter.subscribe(channel);

        ChatSource.player(server.addPlayer())
                .message("test")
                .to(channel)
                .format(Format.defaultFormat())
                .send();

        assertThat(player.nextMessage()).isEqualTo("Player1: test");
    }

    @Test
    void sendMessage_storesLastMessage() {

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.add(chatter);

        ChatSource.player(server.addPlayer())
                .message("test")
                .to(channel)
                .send();

        assertThat(channel.getLastReceivedMessage())
                .isNotNull()
                .extracting(
                        this::toText,
                        m -> m.getSource().getDisplayName()
                ).contains(
                        "&6[&atest&6]&ePlayer1&7: &atest",
                        "Player1"
                );
    }

    @Test
    void sendMessage_storesFormattedChannelMessage() {
        Channel channel = Channel.channel("test", ChannelConfig.defaults().format(Format.miniMessage("[<channel_name]<sender_name>: <message>")));
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

        ChatSource source1 = ChatSource.player(server.addPlayer());
        ChatSource source2 = ChatSource.player(server.addPlayer());

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.add(chatter);

        source1.message("test").to(channel).send();
        source2.message("foobar").to(channel).send();
        channel.sendMessage("Heyho");

        assertThat(channel.getReceivedMessages())
                .hasSize(3)
                .extracting(message -> toText(message.getText()))
                .containsExactly(
                        "test",
                        "foobar",
                        "Heyho"
                );
    }

    @Test
    void sendMessage_setsTargetToChannel() {

        Chatter chatter = Chatter.of(server.addPlayer());
        channel.add(chatter);

        ChatSource.player(server.addPlayer())
                .message("test")
                .to(channel)
                .send();

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::getChannel)
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

    @Test
    void canAutoJoin_true_ifConfigured() {

        Channel channel = createChannel(config -> config.autoJoin(true));
        assertThat(channel.canAutoJoin(server.addPlayer())).isTrue();
    }

    @Test
    void canAutoJoin_playerWithPermission_notConfigured_isTrue() {
        Channel channel = createChannel(config -> config.autoJoin(false));
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, channel.getAutoJoinPermission(), true);

        assertThat(channel.canAutoJoin(player)).isTrue();
    }

    @Test
    void canAutoJoin_protectedChannel_noPermission_isFalse() {
        Channel channel = createChannel(config -> config.autoJoin(true).protect(true));

        assertThat(channel.canAutoJoin(server.addPlayer())).isFalse();
    }

    @Test
    void canAutoJoin_protected_bothPermissions_isTrue() {
        Channel channel = createChannel(config -> config.protect(true).autoJoin(false));

        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, channel.getPermission(), true);
        player.addAttachment(plugin, channel.getAutoJoinPermission(), true);

        assertThat(channel.canAutoJoin(player)).isTrue();
    }

    @Test
    void message_isSentToConsole() {
        Channel channel = createChannel(config -> config.sendToConsole(true));

        Message message = channel.sendMessage("test");
        assertThat(Console.console().getLastReceivedMessage().getParent())
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void message_sendToConsole_false_isNotSentToConsole() {
        Channel channel = createChannel(config -> config.sendToConsole(false));

        channel.sendMessage("test");
        assertThat(Console.console().getLastReceivedMessage())
                .isNull();
    }

    @Nested
    @DisplayName("with config")
    class WithConfig {

        @Test
        void createFromConfig() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "Test");
            cfg.set("format", "<green><message>");
            cfg.set("console", true);

            Channel channel = Channel.channel("test", ChannelConfig.of(cfg));

            assertThat(channel.getName()).isEqualTo("Test");
            assertThat(channel.getConfig().format()).isNotNull();
            assertThat(channel.getConfig().sendToConsole()).isTrue();
        }

    }
}
