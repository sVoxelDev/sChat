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

package net.silthus.schat.platform.messaging;

import java.util.HashMap;
import java.util.Map;
import net.silthus.schat.messaging.MessengerGatewayProvider;
import org.jetbrains.annotations.NotNull;

public final class GatewayProviderRegistry implements MessengerGatewayProvider.Registry {

    private final Map<String, MessengerGatewayProvider> providerMap = new HashMap<>();

    @Override
    public MessengerGatewayProvider get(String name) {
        final String key = key(name);
        if (!providerMap.containsKey(key))
            throw new IllegalArgumentException("The provider '" + name + "' is not registered!");
        else
            return providerMap.get(key);
    }

    @Override
    public void register(MessengerGatewayProvider provider) {
        final String key = key(provider.getName());
        if (providerMap.containsKey(key))
            throw new IllegalArgumentException("A provider with the name " + provider.getName()
                + " already exists: " + providerMap.get(key).getClass().getCanonicalName());
        else
            providerMap.put(key, provider);
    }

    @NotNull
    private String key(String key) {
        return key.toLowerCase();
    }
}
