package net.silthus.schat.core;

import net.silthus.schat.Chatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelManagerTest {

    private Chatter chatter;
    private ChannelImpl channel;

    @BeforeEach
    void setUp() {
        chatter = new ChatterImpl();
        channel = new ChannelDummy();
    }

    @Test
    void getChannels_isEmpty() {
        assertThat(chatter.getChannels()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getChannels_isUnmodifiable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> chatter.getChannels().add(channel));
    }

    @Nested
    class JoinChannel {

        @BeforeEach
        void setUp() {
            chatter.join(channel);
        }

        @Test
        void join_addsChannel_toChatter() {
            assertThat(chatter.getChannels()).contains(channel);
        }

        @Test
        void join_addsTarget_toChannel() {
            assertThat(channel.getTargets()).contains(chatter);
        }

        @Nested
        class LeaveChannel {

            @BeforeEach
            void setUp() {
                chatter.leave(channel);
            }

            @Test
            void leave_removesChannel_fromChatter() {
                assertThat(chatter.getChannels()).doesNotContain(channel);
            }

            @Test
            void leave_removesTarget_fromChannel() {
                assertThat(channel.getTargets()).doesNotContain(chatter);
            }
        }
    }

    @Nested
    class SetActiveChannel {

        @BeforeEach
        void setUp() {
            chatter.setActiveChannel(channel);
        }

        @Test
        void getActiveChannel_returnsActiveChannel() {
            assertThat(chatter.getActiveChannel())
                .isPresent().get()
                .isEqualTo(channel);
        }

        @Test
        void setActiveChannel_addsChannel() {
            assertThat(chatter.getChannels()).contains(channel);
        }

        @Test
        void setActiveChannel_addsChatter_asTarget() {
            assertThat(channel.getTargets()).contains(chatter);
        }

        @Test
        void leave_removesActiveTarget_ifSame() {
            chatter.leave(channel);
            assertThat(chatter.getActiveChannel()).isEmpty();
        }
    }
}
