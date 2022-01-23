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
import java.util.Optional;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.view.ViewProvider;

import static net.silthus.schat.velocity.adapter.VelocitySenderFactory.identity;
import static net.silthus.schat.view.ViewConnector.createSimpleViewConnector;

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
            .viewConnector(createSimpleViewConnector(viewProvider, (chatter, renderedView) -> player.sendMessage(renderedView)))
            .permissionHandler(player::hasPermission)
            .create();
    }
}
