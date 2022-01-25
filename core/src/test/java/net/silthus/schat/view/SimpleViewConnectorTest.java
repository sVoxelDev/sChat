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

    private final @NonNull Display display = render -> this.renderedView = render;
    private SimpleViewConnector connector;
    private ChatterMock chatter;
    private Component renderedView;

    @BeforeEach
    void setUp() {
        chatter = chatterMock(builder -> builder.viewConnector(c -> {
            connector = new SimpleViewConnector(c, cv -> () -> c.getLastMessage().map(Message::text).orElse(Component.empty()), display);
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
