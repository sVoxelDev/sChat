package net.silthus.chat;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import net.silthus.chat.config.ConsoleConfig;
import org.bukkit.event.server.ServerCommandEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

public class ConsoleTests {

    @AfterEach
    void tearDown() {
        Console.instance = null;
    }

    @Test
    void instance_beforeOnEnable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(Console::console);
    }

    @Nested
    class AfterOnEnable extends TestBase  {

        private Console console;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();

            console = Console.console();
        }

        @Test
        void init_twice_throws() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> Console.init(mock(ConsoleConfig.class)));
        }

        @Test
        void create() {
            ChatTarget target = ChatTarget.console();
            assertThat(target).isNotNull()
                    .isInstanceOf(ChatTarget.class)
                    .isInstanceOf(ChatSource.class)
                    .extracting(ChatTarget::getIdentifier)
                    .isEqualTo(Constants.Targets.CONSOLE);

            assertThat(Console.console()).isSameAs(target);
        }

        @Test
        void onEnable_registersConsoleListener() {
            assertThat(getRegisteredListeners()).contains(console);
        }

        @Test
        void sendMessage_sendsMessageToConsole() {

            Message message = Message.message("Hi").to(console).send();

            assertThat(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage())
                    .isNotNull()
                    .isEqualTo("Hi");
            assertThat(console.getLastReceivedMessage())
                    .isNotNull()
                    .isEqualTo(message);
        }

        @Test
        void getTarget_isNotNull() {
            assertThat(console.getTarget())
                    .isNotNull();
        }

        @Test
        void onChat_sendsMessageToDefaultTarget() {

            console.onConsoleChat(new ServerCommandEvent(server.getConsoleSender(), "Hi there!"));
            assertThat(console.getTarget().getLastReceivedMessage())
                    .isNotNull()
                    .extracting(this::toText)
                    .isEqualTo("Console: Hi there!");
            assertThat(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage())
                    .isNotNull()
                    .contains("Hi there!");
        }

        @Test
        void onChat_doesNotSendCommandsAsMessage() {
            console.onConsoleChat(new ServerCommandEvent(server.getConsoleSender(), "/tell hi"));

            assertThat(console.getTarget().getLastReceivedMessage())
                    .isNull();
            assertThat(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage())
                    .isNull();
        }
    }
}
