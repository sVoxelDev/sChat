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

package net.silthus.schat.ui.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.silthus.schat.channel.Channel.channel;
import static net.silthus.schat.chatter.ChatterMock.chatterMock;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

class MiniMessageFormatTest {
    private static final MiniMessage SERIALIZER = MiniMessage.miniMessage();

    @BeforeEach
    void setUp() {
    }

    private void assertFormat(String format, Message.Draft message, String expected) {
        final Component formatted = new MiniMessageFormat(format).format(message.create()).compact();
        assertThat(SERIALIZER.serialize(formatted)).isEqualTo(expected);
    }

    @Test
    void simple_text_only_message() {
        assertFormat("<gray><text>",
            message("Hi"),
            "<gray>Hi"
        );
    }

    @Test
    void formatted_text_message() {
        assertFormat("<gray><text>",
            message(text("Hi").append(text(" there!", RED, BOLD))),
            "<gray>Hi</gray><red><bold> there!"
        );
    }

    @Test
    void message_without_source_but_source_format() {
        assertFormat("<source>: <text>",
            message("Hey"),
            "<source>: Hey"
        );
    }

    @Test
    void message_with_source_and_source_format() {
        assertFormat("<source.display_name>: <text>",
            message("Hey").source(identity("Bob", text("Bobby"))),
            "Bobby: Hey"
        );
    }

    @Test
    void formats_complex_message() {
        final ChatterMock source = chatterMock(identity("Bob", text("Bobby", DARK_RED)));
        source.activeChannel(channel("test").name(text("Test", GREEN)).create());
        final Message.Draft message = message(text("Hey ").append(text("@Silthus", RED, BOLD))).source(source);

        assertFormat("<aqua>(<source.active_channel.name><aqua>) <yellow><source.display_name><gray>: <text>",
            message,
            "<aqua>(</aqua><green>Test</green><aqua>) </aqua><yellow></yellow><dark_red>Bobby</dark_red><gray>: Hey </gray><red><bold>@Silthus"
        );
    }
}
