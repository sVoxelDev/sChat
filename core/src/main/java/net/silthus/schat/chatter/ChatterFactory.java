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

package net.silthus.schat.chatter;

import java.util.UUID;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.view.Display;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.chatter.Chatter.chatter;
import static net.silthus.schat.view.ViewConnector.createSimpleViewConnector;

public abstract class ChatterFactory {
    protected final ViewProvider viewProvider;

    public ChatterFactory(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public final Chatter createChatter(UUID id) {
        return chatter(getIdentity(id))
            .viewConnector(chatter -> createSimpleViewConnector(chatter, viewProvider, getDisplay(id)))
            .permissionHandler(getPermissionHandler(id))
            .create();
    }

    @NotNull
    protected abstract Identity getIdentity(UUID id);

    protected abstract Chatter.PermissionHandler getPermissionHandler(UUID id);

    protected abstract Display getDisplay(UUID id);
}
