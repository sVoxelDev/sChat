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

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.CommandBuilder;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.channel.Channel.PRIVATE;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;

@Getter
@Accessors(fluent = true)
public class SendPrivateMessageCommand implements Command {

    public static final @NonNull Predicate<Channel> IS_PRIVATE = channel -> channel.is(PRIVATE);
    @Getter
    @Setter
    private static @NonNull Function<SendPrivateMessageCommand.Builder, SendPrivateMessageCommand.Builder> prototype = builder -> builder;

    public static SendMessageResult sendPrivateMessage(Chatter source, Chatter target, Component text) {
        return sendPrivateMessage(source, target, Message.message(text).source(source).to(target).create());
    }

    public static SendMessageResult sendPrivateMessage(Chatter source, Chatter target, Message message) {
        return sendPrivateMessageBuilder(source, target, message).create().execute();
    }

    public static Builder sendPrivateMessageBuilder(Chatter source, Chatter target, Message message) {
        return prototype.apply(new Builder(source, target, message));
    }

    private final Chatter source;
    private final Chatter target;
    private final Message message;
    private final boolean setActive;
    private final ChannelRepository repository;

    public SendPrivateMessageCommand(Builder builder) {
        this.source = builder.source;
        this.target = builder.target;
        this.message = builder.message;
        this.setActive = builder.setActive;
        this.repository = builder.channelRepository;
    }

    @Override
    public SendMessageResult execute() throws Error {
        createPrivateChannel(source, target).sendMessage(message);
        return new SendMessageResult(message, true);
    }

    private @NotNull Channel createPrivateChannel(Chatter source, Chatter target) {
        final Channel channel = repository.find(IS_PRIVATE.and(containsTarget(source)).and(containsTarget(target)))
            .orElseGet(() -> createPrivateChannel(UUID.randomUUID().toString(), target.displayName()));

        source.join(channel);
        target.join(channel);
        if (setActive()) {
            source.activeChannel(channel);
            target.activeChannel(channel);
        }

        return channel;
    }

    @NotNull
    private Predicate<Channel> containsTarget(Chatter source) {
        return channel -> channel.targets().contains(source);
    }

    private Channel createPrivateChannel(String key, Component name) {
        final Channel channel = Channel.channel(key)
            .name(name)
            .set(Channel.GLOBAL, true)
            .set(PRIVATE, true)
            .create();
        repository.add(channel);
        return channel;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder extends CommandBuilder<Builder, SendPrivateMessageCommand> {

        private final Chatter source;
        private final Chatter target;
        private final Message message;
        private ChannelRepository channelRepository = createInMemoryChannelRepository();
        private boolean setActive = true;

        protected Builder(Chatter source, Chatter target, Message message) {
            super(SendPrivateMessageCommand::new);
            this.source = source;
            this.target = target;
            this.message = message;
        }
    }
}
