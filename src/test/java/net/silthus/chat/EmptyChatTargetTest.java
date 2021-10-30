package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static net.silthus.chat.Constants.Targets.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

class EmptyChatTargetTest {

    @Test
    void create() {
        ChatTarget target = ChatTarget.empty();
        assertThat(target)
                .extracting(ChatTarget::getIdentifier)
                .isEqualTo(SYSTEM);
    }
}