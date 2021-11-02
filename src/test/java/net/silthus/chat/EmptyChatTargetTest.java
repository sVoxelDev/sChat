package net.silthus.chat;

import net.silthus.chat.config.ChannelConfig;
import org.junit.jupiter.api.Test;

import static net.silthus.chat.Constants.Targets.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

class EmptyChatTargetTest {

    @Test
    void create() {
        ChatTarget target = ChatTarget.nil();
        assertThat(target)
                .extracting(ChatTarget::getIdentifier)
                .isEqualTo(EMPTY);
    }

    @Test
    void channel_fromConfig() {
        Channel channel = ChatTarget.channel("test", ChannelConfig.defaults().name("Test 1"));
        assertThat(channel)
                .isNotNull()
                .extracting(
                        Channel::getIdentifier,
                        Channel::getName
                ).contains(
                        "test",
                        "Test 1"
                );
    }
}