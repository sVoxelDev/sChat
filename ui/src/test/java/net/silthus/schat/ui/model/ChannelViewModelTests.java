/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.ui.model;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.schat.channel.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.ui.ComponentAssertions.assertTextContains;

class ChannelViewModelTests {
    private Channel channel;
    private ChannelViewModel model;

    @BeforeEach
    void setUp() {
        channel = Channel.channel("test").name(text("TestCh")).create();
        model = new ChannelViewModel(channel, () -> false);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_channel_throws_npe() {
        assertNPE(() -> new ChannelViewModel(null, () -> false));
    }

    @Nested class given_channel_is_not_active {
        @Test
        void returns_channel_display_name() {
            assertTextContains(model.render(), "TestCh");
        }

        @Test
        void then_has_click_event_to_join_channel() {
            assertTextContains(model.render(), "<click:run_command:\"/channel join test\">");
        }
    }

    @Nested class given_channel_is_active {
        @BeforeEach
        void setUp() {
            model.set(ChannelViewModel.CHANNEL_IS_ACTIVE, true);
        }

        @Test
        void then_name_is_underlined() {
            assertTextContains(model.render(), "<underlined>");
        }

        @Nested class given_different_active_decoration {
            @BeforeEach
            void setUp() {
                model.set(ChannelViewModel.ACTIVE_CHANNEL_DECORATION, TextDecoration.BOLD);
            }

            @Test
            void then_name_is_bold() {
                assertTextContains(model.render(), "<bold>");
            }
        }

        @Nested class given_active_color {
            @BeforeEach
            void setUp() {
                model.set(ChannelViewModel.ACTIVE_CHANNEL_COLOR, NamedTextColor.BLUE);
            }

            @Test
            void then_name_has_color() {
                assertTextContains(model.render(), "<blue>");
            }

            @Nested class when_channel_has_display_name_with_color {
                @BeforeEach
                void setUp() {
                    channel.set(Channel.DISPLAY_NAME, text("Test").color(NamedTextColor.RED));
                }

                @Test
                void then_name_keeps_color_of_channel() {
                    assertTextContains(model.render(), "<red>");
                }
            }
        }
    }
}
