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

package net.silthus.schat.platform.chatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.sender.SenderMock;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.view.ViewConnector;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.ui.view.ViewFactory.empty;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ChatterFactoryStub extends AbstractChatterFactory {

    private final Map<UUID, SenderMock> chatterStubs = new HashMap<>();

    public ChatterFactoryStub() {
        super(cachingViewProvider(empty()));
    }

    public ChatterFactoryStub(ViewProvider viewProvider) {
        super(viewProvider);
    }

    public Chatter stubSenderAsChatter(SenderMock sender) {
        chatterStubs.put(sender.uniqueId(), sender);
        return createChatter(sender.uniqueId());
    }

    public void removeSenderStub(SenderMock sender) {
        chatterStubs.remove(sender.uniqueId());
    }

    @Override
    protected @NotNull Identity createIdentity(UUID id) {
        if (chatterStubs.containsKey(id))
            return chatterStubs.get(id).identity();
        return Identity.identity(id, randomAlphabetic(10));
    }

    @Override
    protected Chatter.PermissionHandler createPermissionHandler(UUID id) {
        if (chatterStubs.containsKey(id))
            return chatterStubs.get(id).permissionHandler();
        return permission -> false;
    }

    @Override
    protected ViewConnector.Factory createViewConnector(UUID id) {
        return ViewConnector.Factory.empty();
    }
}
