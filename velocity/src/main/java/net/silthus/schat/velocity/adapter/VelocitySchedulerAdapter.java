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

import com.velocitypowered.api.scheduler.ScheduledTask;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.silthus.schat.platform.plugin.scheduler.SchedulerAdapter;
import net.silthus.schat.platform.plugin.scheduler.SchedulerTask;
import net.silthus.schat.velocity.VelocityBootstrap;

public final class VelocitySchedulerAdapter implements SchedulerAdapter {

    private final VelocityBootstrap bootstrap;
    private final Executor executor;
    private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public VelocitySchedulerAdapter(VelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.executor = r -> bootstrap.proxy().getScheduler().buildTask(bootstrap, r).schedule();
    }

    @Override
    public Executor async() {
        return this.executor;
    }

    @Override
    public Executor sync() {
        return this.executor;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledTask t = this.bootstrap.proxy().getScheduler().buildTask(this.bootstrap, task)
            .delay((int) delay, unit)
            .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledTask t = this.bootstrap.proxy().getScheduler().buildTask(this.bootstrap, task)
            .delay((int) interval, unit)
            .repeat((int) interval, unit)
            .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public void shutdownScheduler() {
        for (ScheduledTask task : this.tasks) {
            try {
                task.cancel();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void shutdownExecutor() {
        // do nothing
    }
}
