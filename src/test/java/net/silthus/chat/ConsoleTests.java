package net.silthus.chat;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ConsoleTests {

    @AfterEach
    void tearDown() {
        Console.instance = null;
    }

    @Test
    void instance_beforeOnEnable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(Console::instance);
    }

    @Test
    void init_setsConsoleInstance() {
        Channel channel = new Channel("test");
        Console.init(channel);
        assertThat(Console.instance())
                .isNotNull()
                .extracting(Console::getActiveChannel)
                .isEqualTo(channel);
    }

    @Test
    void init_twice_throws() {
        Console.init(new Channel("test"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> Console.init(new Channel("test2")));
    }

    @Nested
    class AfterOnEnable extends TestBase  {

        private Console console;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();

            console = Console.instance();
        }

        @Test
        void create() {
            ChatTarget target = ChatTarget.console();
            assertThat(target).isNotNull()
                    .isInstanceOf(ChatTarget.class)
                    .isInstanceOf(ChatSource.class)
                    .extracting(ChatTarget::getIdentifier)
                    .isEqualTo(Constants.Targets.CONSOLE);

            assertThat(Console.instance()).isSameAs(target);
        }

        @Test
        void onEnable_registersConsoleListener() {
            assertThat(getRegisteredListeners()).contains(console);
        }

        @Test
        void sendMessage_sendsMessageToConsole() {

            Message message = Message.message("Hi");
            console.sendMessage(message);

            assertThat(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage())
                    .isNotNull()
                    .isEqualTo("Hi");
            assertThat(console.getLastReceivedMessage())
                    .isNotNull()
                    .isEqualTo(message);
        }

        @Test
        @Disabled
        void getActiveChannel_isNotNull() {
            assertThat(console.getActiveChannel())
                    .isNotNull();
        }
    }
}
