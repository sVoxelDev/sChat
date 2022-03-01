package net.silthus.schat.ui.views.tabbed;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelHelper;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.ui.view.ViewConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.ui.views.tabbed.TabFormatConfig.FORMAT_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelTabTest {

    private final @NotNull ChatterMock chatter = ChatterMock.randomChatter();
    private final Channel channel = ChannelHelper.randomChannel();
    private final ChannelTab tab = new ChannelTab(new TabbedChannelsView(chatter, new ViewConfig()), channel, channel.get(FORMAT_CONFIG));
    private final EventBusMock eventBus = EventBusMock.eventBusMock();

    @BeforeEach
    void setUp() {
        chatter.join(channel);
    }

    @AfterEach
    void tearDown() {
        eventBus.close();
    }

    private void sendMessages(int amount) {
        for (int i = 0; i < amount; i++) {
            channel.sendMessage(randomMessage());
        }
    }

    @Nested
    class length {

        @Test
        void empty_tab_is_of_zero_length() {
            assertThat(tab.length()).isZero();
        }

        @Test
        void tab_with_messages_has_size_of_message_count() {
            sendMessages(3);
            assertThat(tab.length()).isEqualTo(3);
            sendMessages(2);
            assertThat(tab.length()).isEqualTo(5);
        }
    }
}
