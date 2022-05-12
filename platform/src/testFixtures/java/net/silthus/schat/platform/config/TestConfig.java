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
package net.silthus.schat.platform.config;

import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.key.ConfigKey;

import static net.silthus.schat.eventbus.EventBusMock.eventBusMock;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static org.mockito.Mockito.when;

public class TestConfig extends SChatConfig {

    public static TestConfig testConfig() {
        return new TestConfig(testConfigAdapter(), eventBusMock());
    }

    public static TestConfig testConfig(EventBus eventBus) {
        return new TestConfig(testConfigAdapter(), eventBus);
    }

    private TestConfig(ConfigurationAdapter adapter, EventBus eventBus) {
        super(adapter, eventBus);
    }

    @Override
    public <T> void set(ConfigKey<T> key, T value) {
        try {
            super.set(key, value);
        } catch (Exception e) {
            when(this.get(key)).thenReturn(value);
        }
    }
}
