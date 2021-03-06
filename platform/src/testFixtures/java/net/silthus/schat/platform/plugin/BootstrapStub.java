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
package net.silthus.schat.platform.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.SneakyThrows;
import net.silthus.schat.platform.SchedulerMock;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.plugin.bootstrap.Platform;
import net.silthus.schat.platform.plugin.logging.JavaPluginLogger;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;

public class BootstrapStub implements Bootstrap {

    private final JavaPluginLogger logger = new JavaPluginLogger(Logger.getLogger("Test"));
    private final SchedulerMock scheduler = new SchedulerMock();

    @Override
    public PluginLogger pluginLogger() {
        return logger;
    }

    @Override
    public SchedulerAdapter scheduler() {
        return scheduler;
    }

    @SneakyThrows
    @Override
    public Path dataDirectory() {
        return Files.createTempDirectory("schat");
    }

    @Override
    public String version() {
        return "0";
    }

    @Override
    public Platform.Type type() {
        return Platform.Type.BUKKIT;
    }

    @Override
    public String serverBrand() {
        return "test";
    }

    @Override
    public String serverVersion() {
        return "0";
    }
}
