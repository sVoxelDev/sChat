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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class GatewayProviderRegistryTest {
    private static final String PROVIDER_NAME = "mock";

    private GatewayProviderRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new GatewayProviderRegistry();
    }

    @Test
    void unknown_provider_throws_illegal_argument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> registry.get("unknown"));
    }

    @Test
    void registered_provider_returns_by_name() {
        registry.register(PROVIDER_NAME, new MockMessagingGatewayProvider());
        assertThat(registry.get(PROVIDER_NAME)).isNotNull();
    }

    @Test
    void duplicate_provider_throws() {
        registry.register(PROVIDER_NAME, new MockMessagingGatewayProvider());
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> registry.register(PROVIDER_NAME, new MockMessagingGatewayProvider()));
    }

    @Test
    void gateway_name_is_lower_cased() {
        registry.register(PROVIDER_NAME, new MockMessagingGatewayProvider());
        assertThat(registry.get(PROVIDER_NAME.toUpperCase())).isNotNull();
    }
}
