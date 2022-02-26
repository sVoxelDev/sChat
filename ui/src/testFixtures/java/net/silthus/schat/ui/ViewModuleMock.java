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

import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.ui.view.ViewConfig;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;

public final class ViewModuleMock extends ViewModule {

    public static ViewModuleMock viewModuleMock(EventBus eventBus) {
        final ViewModuleMock module = new ViewModuleMock(new ViewConfig(), eventBus);
        module.init();
        return module;
    }

    private ViewModuleMock(ViewConfig config, EventBus eventBus) {
        super(config, eventBus);
    }

    @Override
    public void init() {
        super.init();

        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(createInMemoryChannelRepository(eventBus())));
    }
}
