package net.silthus.schat.ui.view;

import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.ChatterMock;
import org.junit.jupiter.api.Test;

class ViewTest {

    @Test
    void empty_view_update_does_not_send_empty_raw_message() {
        final EmptyView view = new EmptyView();
        view.update();
        view.chatter().assertReceivedNoRawMessage();
    }

    private static final class EmptyView implements View {
        private final ChatterMock chatter = ChatterMock.randomChatter();

        @Override
        public ChatterMock chatter() {
            return chatter;
        }

        @Override
        public Component render() {
            return Component.empty();
        }
    }
}
