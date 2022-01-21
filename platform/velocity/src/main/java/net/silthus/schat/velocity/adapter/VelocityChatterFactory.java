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
import java.util.Optional;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.ui.ViewProvider;

import static net.silthus.schat.velocity.adapter.VelocitySenderFactory.identity;

public class VelocityChatterFactory implements ChatterFactory {

    private final ProxyServer proxy;
    private final ViewProvider viewProvider;

    public VelocityChatterFactory(ProxyServer proxy, ViewProvider viewProvider) {
        this.proxy = proxy;
        this.viewProvider = viewProvider;
    }

    @Override
    public Chatter createChatter(UUID id) {
        final Optional<Player> optionalPlayer = proxy.getPlayer(id);
        if (optionalPlayer.isEmpty())
            return Chatter.empty();
        final Player player = optionalPlayer.get();
        return Chatter.chatter(identity(player))
            .messageHandler(
                (message, context) -> viewProvider.updateView(context.chatter(), player::sendMessage)
            ).permissionHandler(player::hasPermission)
            .create();
    }
}
