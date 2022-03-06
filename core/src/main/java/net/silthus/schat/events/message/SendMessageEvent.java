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
package net.silthus.schat.events.message;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.silthus.schat.events.Cancellable;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Targets;

/**
 * The {@code SendMessageEvent} is called before a message is sent to its targets.
 *
 * <p>You can still modify (add/remove) targets from the message when this event is called.</p>
 *
 * <p>Cancelling the event will prevent the message from being sent to its targets.</p>
 *
 * @since 1.0.0
 */
@Accessors(fluent = true)
public final class SendMessageEvent implements SChatEvent, Cancellable {

    private final Message message;
    private final Targets targets;
    @Getter
    private final AtomicBoolean cancellationState = new AtomicBoolean(false);

    /**
     * Creates a new event.
     *
     * @param message the message of the event
     * @since 1.0.0
     */
    public SendMessageEvent(final @NonNull Message message) {
        this.message = message;
        this.targets = Targets.copyOf(message.targets());
    }

    /**
     * Gets the message that will be sent after this event is finished.
     *
     * <p>You can modify the {@link Message#targets()} or cancel the sending.</p>
     *
     * @return the message that will be sent
     * @since 1.0.0
     */
    public Message message() {
        return message;
    }

    public Targets targets() {
        return targets;
    }
}
