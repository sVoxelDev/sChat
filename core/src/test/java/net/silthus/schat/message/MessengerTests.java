package net.silthus.schat.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.message.Message.emptyMessage;

class MessengerTests {

    private Messenger interactor;

    @BeforeEach
    void setUp() {
        interactor = new Messenger();
    }

    @Nested class send {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_message_throws_npe() {
            assertNPE(() -> interactor.send(null));
        }

        @Test
        void given_empty_message_without_targets_does_nothing() {
            interactor.send(emptyMessage());
        }
    }
}
