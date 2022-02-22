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

import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.events.message.SendMessageEvent;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import net.silthus.schat.message.Targets;

import static net.silthus.schat.commands.SendPrivateMessageCommand.sendPrivateMessage;

@Getter
@Accessors(fluent = true)
public class SendMessageCommand implements Command {

    @Getter
    private static @NonNull Consumer<Builder> prototype = builder -> {};

    public static void prototype(Consumer<Builder> consumer) {
        prototype = prototype().andThen(consumer);
    }

    public static SendMessageResult sendMessage(Message message) {
        return sendMessageBuilder(message).create().execute();
    }

    public static Builder sendMessageBuilder(Message message) {
        final Builder builder = new Builder(message);
        prototype().accept(builder);
        return builder;
    }

    private final @NonNull Message message;
    private final @NonNull EventBus eventBus;

    public SendMessageCommand(Builder builder) {
        this.message = builder.message;
        this.eventBus = builder.eventBus;
    }

    @Override
    public SendMessageResult execute() throws Error {
        return fireEventAndSendMessage(message);
    }

    private SendMessageResult fireEventAndSendMessage(Message message) {
        final SendMessageEvent event = fireEvent(message);
        if (event.isNotCancelled())
            return sendMessage(event);
        else
            return new SendMessageResult(message, false);
    }

    protected SendMessageEvent fireEvent(Message message) {
        return eventBus.post(new SendMessageEvent(message));
    }

    private SendMessageResult sendMessage(SendMessageEvent event) {
        if (event.message().source() instanceof Chatter source && targetsSingleChatter(event.targets()))
            return deliverPrivateMessage(source, targetOf(event), event.message());
        else
            return deliverMessage(event.targets(), event.message());
    }

    protected SendMessageResult deliverPrivateMessage(Chatter source, Chatter target, Message message) {
        return sendPrivateMessage(source, target, message.text());
    }

    private Chatter targetOf(SendMessageEvent event) {
        return (Chatter) event.targets().filter(MessageTarget.IS_CHATTER).get(0);
    }

    protected SendMessageResult deliverMessage(MessageTarget target, Message message) {
        return target.sendMessage(message);
    }

    private boolean targetsSingleChatter(Targets targets) {
        return targets.filter(MessageTarget.IS_CHATTER).size() == 1;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, SendMessageCommand> {

        private final Message message;
        private EventBus eventBus;

        public Builder(Message message) {
            super(SendMessageCommand::new);
            this.message = message;
        }
    }

    @Log(topic = "sChat:SendMessage")
    public static class Logging extends SendMessageCommand {

        public Logging(SendMessageCommand.Builder builder) {
            super(builder);
        }

        @Override
        protected SendMessageEvent fireEvent(Message message) {
            final SendMessageEvent event = super.fireEvent(message);
            if (event.isCancelled())
                log.info("SendMessageEvent CANCELLED for: " + message);
            return event;
        }

        @Override
        protected SendMessageResult deliverPrivateMessage(Chatter source, Chatter target, Message message) {
            final SendMessageResult result = super.deliverPrivateMessage(source, target, message);
            log.info("Delivered PRIVATE Message '" + message + "' from '" + source + "' to '" + target + "' --> " + (result.wasSuccessful() ? "SUCCESS" : "FAILED"));
            return result;
        }

        @Override
        protected SendMessageResult deliverMessage(MessageTarget target, Message message) {
            final SendMessageResult result = super.deliverMessage(target, message);
            log.info("Delivered Message '" + message + "' from '" + message.source() + "' to '" + target + "' --> " + (result.wasSuccessful() ? "SUCCESS" : "FAILED"));
            return result;
        }
    }
}
