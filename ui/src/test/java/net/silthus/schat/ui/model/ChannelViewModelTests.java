/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        model = new ChannelViewModel(channel);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_channel_throws_npe() {
        assertNPE(() -> new ChannelViewModel(null));
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
