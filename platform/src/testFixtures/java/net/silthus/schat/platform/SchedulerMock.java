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

package net.silthus.schat.platform;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.platform.plugin.scheduler.SchedulerTask;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerMock implements SchedulerAdapter {

    private boolean executedAsync = false;

    @Override
    public Executor async() {
        executedAsync = true;
        return Runnable::run;
    }

    @Override
    public Executor sync() {
        return Runnable::run;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        task.run();
        return () -> {};
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        task.run();
        return () -> {};
    }

    @Override
    public void shutdownScheduler() {

    }

    @Override
    public void shutdownExecutor() {

    }

    public void assertExecutedAsync() {
        assertThat(executedAsync).isTrue();
    }
}
