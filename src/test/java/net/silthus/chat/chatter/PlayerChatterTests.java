package net.silthus.chat.chatter;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.*;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerChatterTests extends TestBase {

    private PlayerMock player;
    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        chatter = Chatter.of(player);
    }

    @Test
    void create_PlayerChatter() {
        assertThat(chatter)
                .extracting(
                        Chatter::getUniqueId,
                        Chatter::getDisplayName
                ).contains(
                        player.getUniqueId(),
                        player.getDisplayName()
                );
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

        assertThat(player.nextMessage())
                .isEqualTo("Hello Chatter!");
    }

    @Test
    void sendMessage_formatsTheMessageIfNotFormatted() {
        sendMessage(server.addPlayer(), "Hi");

        assertThat(player.nextMessage()).isEqualTo("Player1: Hi");
    }

    @Test
    void sendMessage_storesLastMessage() {
        PlayerMock sender = server.addPlayer();
        sendMessage(sender, "Hi there");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::message)
                .isEqualTo("Player1: Hi there");
    }

    @Test
    void getFocusedChannel() {
        Channel channel = chatter.getFocusedChannel();

        assertThat(channel).isNull();
    }

    @Test
    void setFocusedChannel_setsChannel() {
        Channel channel = new Channel("test");
        chatter.setFocusedChannel(channel);

        assertThat(chatter.getFocusedChannel())
                .isEqualTo(channel);
    }

    private void sendMessage(Player source, String message) {
        chatter.sendMessage(Message.of(ChatSource.of(source), message));
    }
}
