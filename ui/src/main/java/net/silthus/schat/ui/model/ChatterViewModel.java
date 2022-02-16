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

package net.silthus.schat.ui.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelSettings;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.pointer.Settings.createSettings;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"chatter"})
public class ChatterViewModel implements Configured.Modifiable<ChatterViewModel> {

    private static final Comparator<Channel> CHANNEL_COMPARATOR = Comparator.<Channel, Integer>
            comparing(c -> c.get(ChannelSettings.PRIORITY))
        .thenComparing(c -> c.get(PRIVATE))
        .thenComparing(Channel::key);
    private static final Comparator<Message> MESSAGE_COMPARATOR = Comparator.comparing(Message::timestamp);

    public static ChatterViewModel of(@NonNull Chatter chatter) {
        return new ChatterViewModel(chatter);
    }

    private final Chatter chatter;
    private final Settings settings = createSettings();

    ChatterViewModel(@NonNull Chatter chatter) {
        this.chatter = chatter;
    }

    public @NotNull @Unmodifiable List<Message> messages() {
        return chatter.messages().stream()
            .filter(this::isMessageDisplayed)
            .sorted(MESSAGE_COMPARATOR)
            .toList();
    }

    @NotNull
    private Stream<Message> activeChannelMessages() {
        return chatter.activeChannel()
            .map(Channel::messages)
            .stream()
            .flatMap(Collection::stream);
    }

    private boolean isMessageDisplayed(Message message) {
        return true;
    }

    public boolean isPrivateChannel() {
        return chatter.activeChannel().map(channel -> channel.is(PRIVATE)).orElse(false);
    }

    public boolean noActiveChannel() {
        return chatter.activeChannel().isEmpty();
    }

    public boolean isSystemMessage(Message message) {
        return message.type() == Message.Type.SYSTEM;
    }

    public boolean hasActiveChannel() {
        return chatter.activeChannel().isPresent();
    }

    public @NotNull @Unmodifiable List<Channel> channels() {
        return chatter.channels().stream()
            .sorted(CHANNEL_COMPARATOR)
            .toList();
    }

    public boolean isActiveChannel(Channel channel) {
        return chatter.isActiveChannel(channel);
    }

    public boolean isNotSentToChannel(Message message) {
        return message.channels().isEmpty();
    }

    public boolean isSentToActiveChannel(Message message) {
        return chatter.activeChannel().map(channel -> message.channels().contains(channel)).orElse(false);
    }

    public boolean isSentToChannel(Message message) {
        return message.targets().filter(target -> target instanceof Channel).size() > 0;
    }
}
