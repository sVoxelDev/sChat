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
package net.silthus.schat.ui.views.tabbed;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;

public class PrivateChannelTab extends ChannelTab implements Tab {

    protected PrivateChannelTab(@NonNull TabbedChannelsView view, @NonNull Channel channel, TabFormatConfig config) {
        super(view, channel, config);
    }

    @Override
    public Component name() {
        return channel().targets().stream()
            .filter(target -> target instanceof Chatter)
            .filter(target -> !target.equals(Chatter.empty()))
            .filter(target -> !target.equals(view().chatter()))
            .findFirst()
            .map(target -> (Chatter) target)
            .map(Identified::displayName)
            .orElse(channel().displayName());
    }

    @Override
    protected boolean isMessageDisplayed(Message message) {
        return message.channels().contains(channel());
    }
}
