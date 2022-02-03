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

package net.silthus.schat.velocity;

import java.io.File;
import lombok.Getter;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;
import net.silthus.schat.platform.plugin.AbstractSChatProxyPlugin;
import net.silthus.schat.platform.sender.Sender;
import net.silthus.schat.velocity.adapter.VelocityEventBus;
import net.silthus.schat.velocity.adapter.VelocityMessengerGateway;
import net.silthus.schat.velocity.adapter.VelocitySenderFactory;

import static net.silthus.schat.velocity.adapter.VelocityMessengerGateway.GATEWAY_TYPE;

@Getter
public final class SChatVelocityProxy extends AbstractSChatProxyPlugin {

    private final VelocityBootstrap bootstrap;
    private VelocitySenderFactory senderFactory;

    public SChatVelocityProxy(VelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public Sender getConsole() {
        return getSenderFactory().wrap(bootstrap.getProxy().getConsoleCommandSource());
    }

    @Override
    protected ConfigurationAdapter createConfigurationAdapter() {
        return ConfigurationAdapters.YAML.create(new File(bootstrap.getConfigDirectory().toFile(), "config.yml"));
    }

    @Override
    protected EventBus createEventBus() {
        return new VelocityEventBus();
    }

    @Override
    protected void setupSenderFactory() {
        senderFactory = new VelocitySenderFactory(bootstrap.getProxy());
    }

    @Override
    protected void registerMessengerGateway(MessengerGatewayProvider.Registry registry) {
        registry.register(GATEWAY_TYPE, consumer -> new VelocityMessengerGateway(getBootstrap()));
    }

    @Override
    protected void registerListeners() {
    }
}
