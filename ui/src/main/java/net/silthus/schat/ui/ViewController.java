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

import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.EventListener;
import net.silthus.schat.events.channel.ChatterJoinedChannelEvent;
import net.silthus.schat.events.channel.ChatterLeftChannelEvent;
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.events.chatter.ChatterReceivedMessageEvent;
import net.silthus.schat.events.message.SendChannelMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.format.Format;

public class ViewController implements EventListener {

    private final Replacements replacements;
    private final ViewProvider viewProvider;

    public ViewController(ViewProvider viewProvider, Replacements replacements) {
        this.viewProvider = viewProvider;
        this.replacements = replacements;
    }

    @Override
    public void bind(EventBus bus) {
        bus.on(SendChannelMessageEvent.class, this::handleMessage);

        bindViewUpdateEvents(bus);
    }

    private void bindViewUpdateEvents(EventBus bus) {
        bus.on(ChatterReceivedMessageEvent.class, event -> updateView(event.chatter()));
        bus.on(ChatterLeftChannelEvent.class, event -> updateView(event.chatter()));
        bus.on(ChatterJoinedChannelEvent.class, event -> updateView(event.chatter()));
        bus.on(ChatterChangedActiveChannelEvent.class, event -> updateView(event.chatter()));
    }

    private void updateView(Chatter chatter) {
        viewProvider.view(chatter).update();
    }

    private void handleMessage(SendChannelMessageEvent event) {
        final Component formatted = formatMessage(event);
        event.message().set(Message.FORMATTED, replacements.applyTo(event.message(), formatted));
    }

    private Component formatMessage(SendChannelMessageEvent event) {
        return event.channel().get(Format.MESSAGE_FORMAT)
            .format(View.empty(), event.message());
    }
}
