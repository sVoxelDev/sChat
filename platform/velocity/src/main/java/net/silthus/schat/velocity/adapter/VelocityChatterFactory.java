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

package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.view.Display;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

public class VelocityChatterFactory extends ChatterFactory {

    private final ProxyServer proxy;

    public VelocityChatterFactory(ProxyServer proxy, ViewProvider viewProvider) {
        super(viewProvider);
        this.proxy = proxy;
    }

    @Override
    protected @NotNull Identity getIdentity(UUID id) {
        return proxy.getPlayer(id)
            .map(VelocitySenderFactory::identity)
            .orElse(Identity.nil());
    }

    @Override
    protected Chatter.PermissionHandler getPermissionHandler(UUID id) {
        return permission -> proxy.getPlayer(id)
            .map(player -> player.hasPermission(permission))
            .orElse(false);
    }

    @Override
    protected Display getDisplay(UUID id) {
        return proxy.getPlayer(id)
            .map(this::display)
            .orElse(Display.empty());
    }

    private Display display(Player player) {
        return player::sendMessage;
    }
}
