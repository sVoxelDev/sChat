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
        this.executor = r -> bootstrap.getProxy().getScheduler().buildTask(bootstrap, r).schedule();
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
        ScheduledTask t = this.bootstrap.getProxy().getScheduler().buildTask(this.bootstrap, task)
            .delay((int) delay, unit)
            .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledTask t = this.bootstrap.getProxy().getScheduler().buildTask(this.bootstrap, task)
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
