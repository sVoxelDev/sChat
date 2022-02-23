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

import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.PrivateChannel;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.pointer.Settings;

import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.INACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static net.silthus.schat.ui.format.Format.SELF_MESSAGE_FORMAT;

@Getter
@Accessors(fluent = true)
public final class ViewModule {

    private final ViewConfig config;
    private final EventBus eventBus;
    private final Replacements replacements = new Replacements();
    private final ViewController viewController = new ViewController(this);

    public ViewModule(ViewConfig config, EventBus eventBus) {
        this.config = config;
        this.eventBus = eventBus;

        init();
    }

    public void init() {
        configurePrivateChannel(config);
        viewController.bind(eventBus);
    }

    private void configurePrivateChannel(ViewConfig config) {
        Settings format = config.privateChatFormat();
        PrivateChannel.configure(channel -> channel
            .set(MESSAGE_FORMAT, format.get(MESSAGE_FORMAT))
            .set(SELF_MESSAGE_FORMAT, format.get(SELF_MESSAGE_FORMAT))
            .set(ACTIVE_TAB_FORMAT, format.get(ACTIVE_TAB_FORMAT))
            .set(INACTIVE_TAB_FORMAT, format.get(INACTIVE_TAB_FORMAT))
        );
    }
}
