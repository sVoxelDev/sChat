/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.schat.velocity.adapter;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.platform.factories.AbstractChatterFactory;
import net.silthus.schat.view.ViewConnector;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

public class VelocityChatterFactory extends AbstractChatterFactory {

    private final ProxyServer proxy;

    public VelocityChatterFactory(ProxyServer proxy, ViewProvider viewProvider) {
        super(viewProvider);
        this.proxy = proxy;
    }

    @Override
    protected @NotNull Identity createIdentity(UUID id) {
        return proxy.getPlayer(id)
            .map(VelocitySenderFactory::identity)
            .orElse(Identity.nil());
    }

    @Override
    protected Chatter.PermissionHandler createPermissionHandler(UUID id) {
        return permission -> proxy.getPlayer(id)
            .map(player -> player.hasPermission(permission))
            .orElse(false);
    }

    @Override
    protected ViewConnector.Factory createViewConnector(UUID id) {
        return chatter -> ViewConnector.createSimpleViewConnector(chatter, viewProvider, getViewOut(id));
    }

    protected ViewConnector.Out getViewOut(UUID id) {
        return proxy.getPlayer(id)
            .map(this::display)
            .orElse(ViewConnector.Out.empty());
    }

    private ViewConnector.Out display(Player player) {
        return player::sendMessage;
    }
}
