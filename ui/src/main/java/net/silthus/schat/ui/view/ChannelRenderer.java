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

package net.silthus.schat.ui.view;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.model.ChatterViewModel;

@Getter
public class ChannelRenderer implements Configured {
    private final ChatterViewModel viewModel;
    private final Settings settings;

    // TODO: change to channel view model
    ChannelRenderer(ChatterViewModel viewModel, Settings settings) {
        this.viewModel = viewModel;
        this.settings = settings;
    }

    public Component renderChannel(Channel channel) {
        if (viewModel.isActiveChannel(channel))
            return get(TabbedChannelsView.ACTIVE_CHANNEL_FORMAT).format(channel.getDisplayName());
        else
            return channel.getDisplayName();
    }
}
