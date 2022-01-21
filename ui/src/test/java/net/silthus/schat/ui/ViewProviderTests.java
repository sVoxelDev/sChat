package net.silthus.schat.ui;

import net.silthus.schat.chatter.ChatterMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.ui.ViewProvider.simpleViewProvider;
import static org.assertj.core.api.Assertions.assertThat;

class ViewProviderTests {
    private ViewProvider viewProvider;

    @BeforeEach
    void setUp() {
        viewProvider = simpleViewProvider();
    }

    @Nested class getView {
        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_throws_npe() {
            assertNPE(() -> viewProvider.getView(null));
        }

        @Nested class given_valid_chatter {
            private ChatterMock chatter;

            @BeforeEach
            void setUp() {
                chatter = randomChatter();
            }

            @Test
            void then_returns_chatter_view() {
                final View view = viewProvider.getView(chatter);
                assertThat(view).isNotNull();
            }

            @Test
            void then_view_is_cached() {
                final View view = viewProvider.getView(chatter);
                assertThat(view).isSameAs(viewProvider.getView(chatter));
            }
        }
    }
}
