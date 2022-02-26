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

import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.channel.ChatterJoinedChannelEvent;
import net.silthus.schat.events.channel.ChatterLeftChannelEvent;
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.events.chatter.ChatterReceivedMessageEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.message.MessageHelper.randomMessage;

class ViewControllerTest {
    private EventBusMock events;
    private ViewMock view;
    private ChatterMock chatter;

    @BeforeEach
    void setUp() {
        chatter = ChatterMock.randomChatter();
        view = new ViewMock(chatter);
        ViewController controller = new ViewController(chatter -> view, new Replacements());
        events = EventBusMock.eventBusMock();
        controller.bind(events);
    }

    @AfterEach
    void tearDown() {
        events.close();
    }

    @Nested
    class view_updates {

        @Test
        void when_chatter_receives_message() {
            events.post(new ChatterReceivedMessageEvent(chatter, randomMessage()));
            view.assertUpdateCalled();
        }

        @Test
        void when_chatter_joins_channel() {
            events.post(new ChatterJoinedChannelEvent(chatter, randomChannel()));
            view.assertUpdateCalled();
        }

        @Test
        void when_chatter_changes_active_channel() {
            events.post(new ChatterChangedActiveChannelEvent(chatter, null, null));
            view.assertUpdateCalled();
        }

        @Test
        void when_chatter_left_channel() {
            events.post(new ChatterLeftChannelEvent(chatter, randomChannel()));
            view.assertUpdateCalled();
        }
    }
}
