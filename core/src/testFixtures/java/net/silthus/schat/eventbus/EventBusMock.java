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
package net.silthus.schat.eventbus;

import java.util.LinkedList;
import java.util.Queue;
import lombok.NonNull;
import net.silthus.schat.channel.ChannelPrototype;
import net.silthus.schat.chatter.ChatterPrototype;
import net.silthus.schat.commands.JoinChannelCommand;
import net.silthus.schat.commands.LeaveChannelCommand;
import net.silthus.schat.commands.SendMessageCommand;
import net.silthus.schat.events.SChatEvent;

import static org.assertj.core.api.Assertions.assertThat;

public class EventBusMock extends EventBusImpl {

    public static EventBusMock eventBusMock() {
        return new EventBusMock();
    }

    private final Queue<SChatEvent> events = new LinkedList<>();

    protected EventBusMock() {
        ChannelPrototype.configure(this);
        ChatterPrototype.configure(this);
        SendMessageCommand.prototype(builder -> builder.eventBus(this));
        JoinChannelCommand.prototype(builder -> builder.eventBus(this));
        LeaveChannelCommand.prototype(builder -> builder.eventBus(this));
    }

    @Override
    public <E extends SChatEvent> E post(@NonNull E event) {
        events.add(event);
        return super.post(event);
    }

    public <E extends SChatEvent> void assertEventFired(E event) {
        assertThat(events).contains(event);
    }

    public <E extends SChatEvent> void assertEventFired(Class<E> type) {
        assertThat(events).hasAtLeastOneElementOfType(type);
    }

    public <E extends SChatEvent> void assertNoEventFired(E event) {
        assertThat(events).doesNotContain(event);
    }

    public <E extends SChatEvent> void assertNoEventFired(Class<E> type) {
        assertThat(events).doesNotHaveAnyElementsOfTypes(type);
    }

    public void reset() {
        events.clear();
    }
}
