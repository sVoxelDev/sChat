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

import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Settings;
import net.silthus.schat.ui.util.ViewHelper;
import net.silthus.schat.ui.views.tabbed.Tab;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_DECORATION;
import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.INACTIVE_TAB_DECORATION;
import static net.silthus.schat.ui.format.Format.INACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static net.silthus.schat.ui.util.ViewHelper.renderPrivateChannelName;

@Data
@Accessors(fluent = true)
public class ViewConfig {

    private int height = 100;
    private Settings format = Settings.createSettings();
    private Settings privateChatFormat = Settings.settingsBuilder()
        .withStatic(MESSAGE_FORMAT, (view, message) -> ViewHelper.renderPrivateMessage(view.chatter(), (Message) message))
        .withStatic(ACTIVE_TAB_FORMAT, (view, tab) ->
            tab.get(Tab.CHANNEL)
                .map(channel -> ACTIVE_TAB_DECORATION.apply(renderPrivateChannelName(view.chatter(), channel)))
                .orElse(text("Unknown"))
        )
        .withStatic(INACTIVE_TAB_FORMAT, (view, tab) ->
            tab.get(Tab.CHANNEL)
                .map(channel -> INACTIVE_TAB_DECORATION.apply((Tab) tab, renderPrivateChannelName(view.chatter(), channel)))
                .orElse(text("Unknown"))
        )
        .create();
    private JoinConfiguration channelJoinConfig = JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build();

    public void channelJoinConfig(JoinConfiguration channelJoinConfig) {
        if (channelJoinConfig != null)
            this.channelJoinConfig = channelJoinConfig;
    }
}
