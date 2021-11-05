package net.silthus.chat.targets;

import net.silthus.chat.Chatter;
import net.silthus.chat.Conversation;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.Test;

public class DirectConversationTests extends TestBase {

    @Test
    void create() {
        Conversation conversation = Conversation.direct(Chatter.of(server.addPlayer()), Chatter.of(server.addPlayer()));
    }
}
