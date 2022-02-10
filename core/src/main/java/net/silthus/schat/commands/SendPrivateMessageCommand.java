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

import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.message.Message;

import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;

@Getter
@Accessors(fluent = true)
public class SendPrivateMessageCommand implements Command {

    @Getter
    @Setter
    private static @NonNull Function<SendPrivateMessageCommand.Builder, SendPrivateMessageCommand.Builder> prototype = builder -> builder;

    public static SendMessageResult sendPrivateMessage(Chatter source, Chatter target, Component text) {
        return sendPrivateMessageBuilder(source, target, text).create().execute();
    }

    public static Builder sendPrivateMessageBuilder(Chatter source, Chatter target, Component text) {
        return prototype.apply(new Builder(source, target, text));
    }

    private final Chatter source;
    private final Chatter target;
    private final Component text;
    private final boolean setActive;

    public SendPrivateMessageCommand(Builder builder) {
        this.source = builder.source;
        this.target = builder.target;
        this.text = builder.text;
        this.setActive = builder.setActive;
    }

    @Override
    public SendMessageResult execute() throws Error {
        final Channel channel = createPrivateChannel(source, target).channel();
        if (setActive())
            source().activeChannel(channel);
        final Message message = Message.message(text).to(channel).send();
        return new SendMessageResult(message, true);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, SendPrivateMessageCommand> {

        private final Chatter source;
        private final Chatter target;
        private final Component text;
        private boolean setActive = true;

        protected Builder(Chatter source, Chatter target, Component text) {
            super(SendPrivateMessageCommand::new);
            this.source = source;
            this.target = target;
            this.text = text;
        }
    }
}
