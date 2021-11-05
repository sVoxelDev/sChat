package net.silthus.chat.targets;

import net.silthus.chat.ChatTarget;
import net.silthus.chat.Conversation;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractConversationTest extends TestBase {

    private Conversation conversation;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        conversation = createChannel("testing");
    }

    @Test
    void subscribe_returnsSubscription() {
        conversation.subscribe(ChatTarget.player(server.addPlayer()));
    }
}