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
import net.silthus.schat.ui.placeholder.Replacements;
import net.silthus.schat.ui.view.ViewConfig;
import net.silthus.schat.ui.view.ViewController;
import net.silthus.schat.ui.view.ViewFactory;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.ui.views.Views;
import net.silthus.schat.ui.views.tabbed.TabbedChannelsView;

import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.INACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static net.silthus.schat.ui.format.Format.SELF_MESSAGE_FORMAT;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;

@Getter
@Accessors(fluent = true)
public class ViewModule {

    private final ViewConfig config;
    private final EventBus eventBus;
    private final ViewFactory viewFactory;
    private final ViewProvider viewProvider;
    private final ViewController viewController;
    private final Replacements replacements = new Replacements();

    public ViewModule(ViewConfig config, EventBus eventBus) {
        this.config = config;
        this.eventBus = eventBus;
        this.viewFactory = chatter -> {
            final TabbedChannelsView view = Views.tabbedChannels(chatter, config());
            eventBus.register(view);
            return view;
        };
        this.viewProvider = cachingViewProvider(viewFactory());

        this.viewController = new ViewController(replacements);

        init();
    }

    public void init() {
        configurePrivateChannel(config);
        viewController.bind(eventBus);
    }

    public static void configurePrivateChannel(ViewConfig config) {
        Settings format = config.privateChatFormat();
        PrivateChannel.configure(channel -> channel
            .set(MESSAGE_FORMAT, format.get(MESSAGE_FORMAT))
            .set(SELF_MESSAGE_FORMAT, format.get(SELF_MESSAGE_FORMAT))
            .set(ACTIVE_TAB_FORMAT, format.get(ACTIVE_TAB_FORMAT))
            .set(INACTIVE_TAB_FORMAT, format.get(INACTIVE_TAB_FORMAT))
        );
    }
}
