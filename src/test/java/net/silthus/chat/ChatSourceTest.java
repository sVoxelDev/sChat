package net.silthus.chat;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSourceTest extends TestBase {

    private ChatSource source;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        source = ChatSource.of(server.addPlayer());
    }

    @Test
    void sendMessageTo() {

        PlayerMock player = server.addPlayer();
        ChatTarget target = ChatTarget.of(player);
        source.sendMessageTo(target, "Hi there!");

        assertThat(target.getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::formattedMessage)
                .isEqualTo("Player0: Hi there!");
        assertReceivedMessage(player, "Player0: Hi there!");
    }
}