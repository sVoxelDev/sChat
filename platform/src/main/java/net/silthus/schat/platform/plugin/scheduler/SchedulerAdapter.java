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

package net.silthus.schat.platform.plugin.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * A scheduler for running tasks using the systems provided by the platform.
 */
public interface SchedulerAdapter {

    /**
     * Gets an async executor instance.
     *
     * @return an async executor instance
     */
    Executor async();

    /**
     * Gets a sync executor instance.
     *
     * @return a sync executor instance
     */
    Executor sync();

    /**
     * Executes a task async.
     *
     * @param task the task
     */
    default void executeAsync(Runnable task) {
        async().execute(task);
    }

    /**
     * Executes a task sync.
     *
     * @param task the task
     */
    default void executeSync(Runnable task) {
        sync().execute(task);
    }

    /**
     * Executes the given task with a delay.
     *
     * @param task the task
     * @param delay the delay
     * @param unit the unit of delay
     * @return the resultant task instance
     */
    SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit);

    /**
     * Executes the given task repeatedly at a given interval.
     *
     * @param task the task
     * @param interval the interval
     * @param unit the unit of interval
     * @return the resultant task instance
     */
    SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit);

    /**
     * Shuts down the scheduler instance.
     *
     * <p>{@link #asyncLater(Runnable, long, TimeUnit)} and {@link #asyncRepeating(Runnable, long, TimeUnit)}.</p>
     */
    void shutdownScheduler();

    /**
     * Shuts down the executor instance.
     *
     * <p>{@link #async()} and {@link #executeAsync(Runnable)}.</p>
     */
    void shutdownExecutor();

}
