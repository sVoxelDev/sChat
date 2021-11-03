package net.silthus.chat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSourceTest extends TestBase {

    @Test
    void of_identifier() {
        ChatSource source = ChatSource.named("test");
        assertIdAndName(source, "test", "test");
    }

    @Test
    void of_identifier_withDisplayName() {
        ChatSource source = ChatSource.named("test", "Test Source");
        assertIdAndName(source, "test", "Test Source");
    }

    @Test
    void isPlayer_returnsFalse() {
        assertThat(ChatSource.named("test").isPlayer()).isFalse();
    }

    @Test
    void isPlayer_ofPlayer_returnsTrue() {
        assertThat(ChatSource.player(server.addPlayer()).isPlayer()).isTrue();
    }

    private void assertIdAndName(ChatSource source, String id, String name) {
        assertThat(source)
                .isNotNull()
                .extracting(
                        ChatSource::getIdentifier,
                        ChatSource::getDisplayName
                ).contains(
                        id,
                        name
                );
    }

    @Test
    void message_createsMessageWithSource() {

        ChatSource source = ChatSource.named("test");
        Message message = source.message("Hi there!").send();

        assertThat(message.getSource()).isEqualTo(source);
    }
}