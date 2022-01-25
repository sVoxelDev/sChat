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

package net.silthus.schat.view;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.chatter.ChatterMock.chatterMock;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleViewConnectorTest {

    private final @NonNull ViewConnector.Out out = render -> this.renderedView = render;
    private SimpleViewConnector connector;
    private ChatterMock chatter;
    private Component renderedView;

    @BeforeEach
    void setUp() {
        chatter = chatterMock(builder -> builder.viewConnector(c -> {
            connector = new SimpleViewConnector(c, cv -> () -> c.getLastMessage().map(Message::text).orElse(Component.empty()), out);
            return connector;
        }));
    }

    @NotNull
    private Message sendRandomMessage() {
        final Message message = randomMessage();
        chatter.sendMessage(message);
        return message;
    }

    private void assertConnectorUpdateWith(Component text) {
        assertThat(renderedView).isNotNull().isEqualTo(text);
    }

    private void callUpdate() {
        connector.update();
    }

    @Nested class update {

        @Test
        void given_no_messages_then_calls_display_with_rendered_empty_view() {
            callUpdate();
            assertConnectorUpdateWith(Component.empty());
        }

        @Test
        void given_messages_then_calls_display_with_rendered_view_of_message() {
            final Message message = sendRandomMessage();
            assertConnectorUpdateWith(message.text());
        }
    }
}
