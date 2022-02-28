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
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.commands.SendMessageResult;
import net.silthus.schat.pointer.Configurable;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.pointer.Pointer.pointer;

public sealed interface Message extends Configurable<Message> permits MessageImpl {

    Pointer<UUID> ID = pointer(UUID.class, "id");
    Pointer<Instant> TIMESTAMP = pointer(Instant.class, "timestamp");
    Pointer<MessageSource> SOURCE = pointer(MessageSource.class, "source");
    Pointer<Component> TEXT = pointer(Component.class, "text");
    Pointer<Channel> CHANNEL = pointer(Channel.class, "channel");
    Pointer<Type> TYPE = pointer(Type.class, "type");

    Setting<Component> FORMATTED = Setting.setting(Component.class, "formatted", null);

    static @NotNull Draft message() {
        return MessageImpl.builder();
    }

    static @NotNull Draft message(@NonNull String text) {
        return message(Component.text(text));
    }

    static @NotNull Draft message(@NonNull Component text) {
        return message().text(text);
    }

    @NotNull UUID id();

    @NotNull Instant timestamp();

    @NotNull MessageSource source();

    @NotNull @Unmodifiable Collection<Channel> channels();

    @NotNull @Unmodifiable Targets targets();

    @NotNull Component text();

    @NotNull Type type();

    @NotNull SendMessageResult send();

    @NotNull Draft copy();

    sealed interface Draft permits MessageImpl.Draft {

        @NotNull UUID id();

        @NotNull Draft id(@NonNull UUID id);

        @NotNull Instant timestamp();

        @NotNull Draft timestamp(@NonNull Instant timestamp);

        @NotNull MessageSource source();

        @NotNull Draft source(@Nullable MessageSource identity);

        @NotNull Draft to(@NonNull MessageTarget target);

        @NotNull @Unmodifiable Collection<MessageTarget> targets();

        @NotNull Draft to(@NonNull Channel channel);

        @NotNull @Unmodifiable Collection<Channel> channels();

        @NotNull Component text();

        @NotNull Draft text(@Nullable Component text);

        @NotNull Type type();

        @NotNull Draft type(@NonNull Type type);

        @NotNull Message send();

        @NotNull Message create();
    }

    enum Type {
        CHAT,
        SYSTEM
    }
}
