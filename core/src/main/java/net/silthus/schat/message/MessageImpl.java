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

package net.silthus.schat.message;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.message.SendMessageEvent;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.pointer.Pointers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"id"})
final class MessageImpl implements Message {

    @Setter
    @Accessors
    private static Function<MessageImpl.Draft, MessageImpl.Draft> prototype = draft -> draft;

    static MessageImpl.Draft builder() {
        return prototype.apply(new Draft());
    }

    private final UUID id;
    private final Instant timestamp;
    private final Identity source;
    private final transient Targets targets;
    private final Component text;
    private final Type type;
    private final transient EventBus eventBus;
    private final transient Pointers pointers;

    private MessageImpl(Draft draft) {
        this.id = draft.id;
        this.timestamp = draft.timestamp;
        this.source = draft.source;
        this.targets = Targets.unmodifiable(Targets.copyOf(draft.targets));
        this.text = draft.text;
        this.type = draft.type;
        this.eventBus = draft.eventBus;
        this.pointers = Pointers.pointers()
            .withStatic(Message.ID, id)
            .withStatic(Message.TIMESTAMP, timestamp)
            .withStatic(Message.SOURCE, source)
            .withStatic(Message.TEXT, text)
            .withStatic(Message.TYPE, type)
            .create();
    }

    @Override
    public @NotNull @Unmodifiable Collection<Channel> channels() {
        return filterAndMapChannels(targets.stream()).toList();
    }

    @Override
    public @NotNull Pointers getPointers() {
        return pointers;
    }

    @Override
    public @NotNull MessageImpl send() {
        final SendMessageEvent event = eventBus.post(new SendMessageEvent(this));
        if (event.isNotCancelled())
            event.targets().sendMessage(this);
        return this;
    }

    @Override
    public Message.@NotNull Draft copy() {
        return new Draft(this);
    }

    @NotNull
    private static Stream<Channel> filterAndMapChannels(Stream<MessageTarget> stream) {
        return stream
            .filter(messageTarget -> messageTarget instanceof Channel)
            .map(messageTarget -> (Channel) messageTarget);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class Draft implements Message.Draft {
        private UUID id = UUID.randomUUID();
        private Instant timestamp = Instant.now();
        private Identity source = Identity.nil();
        private Targets targets = new Targets();
        private Component text = Component.empty();
        private Type type = Type.SYSTEM;
        private EventBus eventBus = EventBus.empty();

        private Draft() {
        }

        private Draft(Message message) {
            this.id = message.id();
            this.timestamp = message.timestamp();
            this.source = message.source();
            this.targets = Targets.copyOf(message.targets());
            this.text = message.text();
            this.type = message.type();
        }

        public @NotNull Draft text(@Nullable Component text) {
            if (text != null)
                this.text = text;
            return this;
        }

        @Override
        public @NotNull Draft source(@Nullable Identity source) {
            if (source != null)
                this.source = source;
            return this;
        }

        @Override
        public @NotNull Draft to(@NonNull MessageTarget target) {
            this.targets.add(target);
            return this;
        }

        @Override
        public @NotNull @Unmodifiable Collection<MessageTarget> targets() {
            return Collections.unmodifiableCollection(targets);
        }

        @Override
        public @NotNull Draft to(@NonNull Channel channel) {
            this.targets.add(channel);
            return this;
        }

        @Override
        public @NotNull @Unmodifiable Collection<Channel> channels() {
            return filterAndMapChannels(targets.stream()).toList();
        }

        @Override
        public @NotNull Message send() {
            return create().send();
        }

        @Override
        public @NotNull MessageImpl create() {
            return new MessageImpl(this);
        }
    }
}
