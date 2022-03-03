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
package net.silthus.schat.ui;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.format.MiniMessageFormat;
import net.silthus.schat.ui.view.ViewConfig;
import net.silthus.schat.ui.views.tabbed.TabFormatConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.eventbus.EventBusMock.eventBusMock;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.ui.placeholder.ReplacementProvider.REPLACED_MESSAGE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;

class ViewModuleTest {

    private EventBusMock eventBus;

    @BeforeEach
    void setUp() {
        eventBus = eventBusMock();
        final ViewModule viewModule = new ViewModule(new ViewConfig(), eventBus);
        viewModule.init();
        viewModule.replacements().addReplacementProvider((message, text) -> text.replace("%test%", "success"));
    }

    @AfterEach
    void tearDown() {
        eventBus.close();
    }

    @NotNull
    private Message sendMessage(Channel channel) {
        return message("Hi there").to(channel).send();
    }

    @Test
    void on_sendChannelMessage_event_message_is_rendered_and_set() {
        final Message message = sendMessage(channelWith(ViewConfig.FORMAT_CONFIG, new TabFormatConfig().messageFormat(new MiniMessageFormat("%test%: <text>"))));
        assertThat(message.get(REPLACED_MESSAGE_FORMAT)).isEqualTo("success: <text>");
    }

}
