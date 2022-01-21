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

import java.util.List;
import lombok.Getter;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static net.silthus.schat.pointer.Settings.createSettings;

@Getter
final class ChatterViewModel implements ViewModel {

    private final Chatter chatter;
    private final Settings settings = createSettings();

    ChatterViewModel(Chatter chatter) {
        this.chatter = chatter;
    }

    @Override
    public @NotNull @Unmodifiable List<Message> getMessages() {
        return chatter.getMessages().stream()
            .filter(this::isMessageDisplayed)
            .sorted()
            .toList();
    }

    private boolean isMessageDisplayed(Message message) {
        return notSentToChannel(message) || hasActiveChannel() && sentToActiveChannel(message);
    }

    private boolean notSentToChannel(Message message) {
        return message.channels().isEmpty();
    }

    private boolean hasActiveChannel() {
        return chatter.getActiveChannel().isPresent();
    }

    private boolean sentToActiveChannel(Message message) {
        return chatter.getActiveChannel().map(channel -> message.channels().contains(channel)).orElse(false);
    }

    @Override
    public @NotNull @Unmodifiable List<Channel> getChannels() {
        return chatter.getChannels().stream().sorted().toList();
    }

    @Override
    public boolean isActiveChannel(Channel channel) {
        return chatter.isActiveChannel(channel);
    }
}
