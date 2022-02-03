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
        return () -> {};
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
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
