package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.md_5.bungee.api.ChatColor;
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
                        Channel::getAlias,
                        Channel::getPermission
                ).contains(
                        "test",
                        Constants.CHANNEL_PERMISSION + ".test"
                );
        assertThat(channel.getName())
                .isEqualTo("test");
        assertThat(channel.getConfig())
                .extracting(ChannelConfig::getName)
                .isEqualTo(null);
        assertThat(channel.getConfig().getFormat())
                .extracting(
                        Format::getPrefix,
                        Format::getSuffix,
                        Format::getChatColor
                ).contains(
                        null,
                        ": ",
                        null
                );
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
        channel.join(player);

        assertThat(channel.getTargets())
                .contains(player);
    }

    @Test
    void join_canOnlyJoinChannelOnce() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.join(player);
        channel.join(player);

        assertThat(channel.getTargets())
                .hasSize(1);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void join_throwsNullPointer_ifPlayerIsNull() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> channel.join(null));
    }

    @Test
    void leave_removesChatTarget_fromChannelTargets() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.join(player);
        assertThat(channel.getTargets()).contains(player);

        channel.leave(player);

        assertThat(channel.getTargets()).isEmpty();
    }

    @Test
    void leave_doesNothingIfPlayerIsNotJoined() {

        Chatter player = Chatter.of(server.addPlayer());
        channel.join(player);
        assertThatCode(() -> channel.leave(Chatter.of(server.addPlayer())))
                .doesNotThrowAnyException();
        assertThat(channel.getTargets()).contains(player);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void leave_throwsNullPointer_ifPlayerIsNull() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> channel.leave(null));
    }

    @Test
    void sendMessage_sendsMessageToNobody() {

        PlayerMock player = server.addPlayer();
        channel.sendMessage(new Message(ChatSource.of(player), "test"));

        assertThat(channel.getTargets()).isEmpty();
        assertThat(player.nextMessage()).isNull();
    }

    @Test
    void sendMessage_sendsMessageToAllJoinedTargets() {

        PlayerMock player0 = server.addPlayer();
        Chatter chatter0 = Chatter.of(player0);
        channel.join(chatter0);
        PlayerMock player1 = server.addPlayer();
        Chatter chatter1 = Chatter.of(player1);
        channel.join(chatter1);
        PlayerMock player2 = server.addPlayer();

        channel.sendMessage(Message.of(chatter0, "test"));

        assertThat(player0.nextMessage()).isEqualTo("Player0: test");
        assertThat(player1.nextMessage()).isEqualTo("Player0: test");
        assertThat(player2.nextMessage()).isNull();
    }

    @Test
    void sendFormattedMessage_doesNotFormatAgain() {
        Message message = Message.of(ChatSource.of(server.addPlayer()), "test");

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.join(chatter);
        channel.sendMessage(message);

        assertThat(player.nextMessage()).isEqualTo("Player0: test");
    }

    @Test
    void sendMessage_storesLastMessage() {
        Message message = Message.of(ChatSource.of(server.addPlayer()), "test");

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.join(chatter);
        channel.sendMessage(message);

        assertThat(channel.getLastReceivedMessage())
                .isNotNull()
                .extracting(
                        Message::message,
                        m -> m.source().getDisplayName()
                ).contains(
                        "test",
                        "Player0"
                );
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

        Message message = Message.of(ChatSource.of(server.addPlayer()), "test");

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.join(chatter);
        channel.sendMessage(message);
        channel.sendMessage(Message.of(ChatSource.of(server.addPlayer()), "foobar"));
        channel.sendMessage("Heyho");

        assertThat(channel.getReceivedMessages())
                .hasSize(3)
                .extracting(Message::message)
                .containsExactly(
                        "test",
                        "foobar",
                        "Heyho"
                );
    }

    @Nested
    @DisplayName("with config")
    class WithConfig {

        @Test
        void createFromConfig() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "Test");
            cfg.set("prefix", "[Test] ");
            cfg.set("suffix", " - ");
            cfg.set("chat_color", "GRAY");

            Channel channel = new Channel("test", new ChannelConfig(cfg));

            assertThat(channel.getName())
                    .isEqualTo("Test");
            assertThat(channel.getConfig().getFormat())
                    .extracting(
                            Format::getPrefix,
                            Format::getSuffix,
                            Format::getChatColor
                    ).contains(
                            "[Test] ",
                            " - ",
                            ChatColor.GRAY
                    );
        }

    }
}
