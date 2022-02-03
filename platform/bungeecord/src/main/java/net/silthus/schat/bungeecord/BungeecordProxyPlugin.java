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

package net.silthus.schat.bungeecord;

import lombok.Getter;
import net.silthus.schat.bungeecord.adapter.BungeecordEventBus;
import net.silthus.schat.bungeecord.adapter.BungeecordMessengerGateway;
import net.silthus.schat.bungeecord.adapter.BungeecordSenderFactory;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.plugin.AbstractSChatProxyPlugin;
import net.silthus.schat.platform.sender.Sender;

import static net.silthus.schat.bungeecord.adapter.BungeecordMessengerGateway.GATEWAY_TYPE;

@Getter
public final class BungeecordProxyPlugin extends AbstractSChatProxyPlugin {

    private final BungeecordBootstrap bootstrap;
    private BungeecordSenderFactory senderFactory;

    BungeecordProxyPlugin(BungeecordBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected EventBus createEventBus() {
        return new BungeecordEventBus(getBootstrap().getProxy());
    }

    @Override
    public Sender getConsole() {
        return senderFactory.wrap(getBootstrap().getProxy().getConsole());
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BungeecordSenderFactory(bootstrap.getLoader());
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return null;
    }

    @Override
    protected void registerMessengerGateway(MessengerGatewayProvider.Registry registry) {
        registry.register(GATEWAY_TYPE, in -> new BungeecordMessengerGateway(getBootstrap()));
    }

    @Override
    protected void registerListeners() {

    }
}
