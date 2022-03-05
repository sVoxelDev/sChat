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
package net.silthus.schat.commands;

import java.io.Serial;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.command.Result;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.message.Message.Type.CHAT;
import static net.silthus.schat.message.Message.message;

/**
 * Sends a chat message to the active channel of the chatter.
 *
 * @since next
 */
public class ChatCommand implements Command {

    /**
     * Sends the given chat message to the active channel of the chatter.
     *
     * @param chatter the chatter that is chatting
     * @param text the message
     * @return the message that was sent
     * @throws NoActiveChannel if the chatter has no active channel
     * @since next
     */
    public static Message chat(Chatter chatter, Component text) throws NoActiveChannel {
        return new Builder(chatter).text(text).create().execute().message();
    }

    /**
     * Creates a new chat command builder.
     *
     * @param chatter the chatter that is chatting
     * @return the builder
     * @since next
     */
    public static Builder chatCommand(Chatter chatter) {
        return new Builder(chatter);
    }

    private final @NonNull Chatter chatter;
    private final @NonNull Component text;

    protected ChatCommand(Builder builder) {
        this.chatter = builder.chatter;
        this.text = builder.text;
    }

    @Override
    public ChatResult execute() throws NoActiveChannel {
        final Channel channel = chatter.activeChannel().orElseThrow(NoActiveChannel::new);
        return new ChatResult(sendMessageTo(channel));
    }

    @NotNull
    private Message sendMessageTo(Channel channel) {
        return message()
            .source(chatter)
            .text(text)
            .to(channel)
            .type(CHAT)
            .send();
    }

    /**
     * The result of the {@link ChatCommand} containing the final message.
     *
     * @since next
     */
    public record ChatResult(Message message) implements Result {
        @Override
        public boolean wasSuccessful() {
            return true;
        }
    }

    /**
     * The builder of a {@link ChatCommand}.
     *
     * @since next
     */
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, ChatCommand> {

        private final Chatter chatter;
        private @NonNull Component text = Component.empty();

        protected Builder(@NonNull Chatter chatter) {
            super(ChatCommand::new);
            this.chatter = chatter;
        }
    }

    /**
     * The exception is thrown if a chatter tries to chat without an active channel.
     *
     * @since next
     */
    public static final class NoActiveChannel extends Error {
        @Serial private static final long serialVersionUID = 6326031138206622013L;
    }
}
