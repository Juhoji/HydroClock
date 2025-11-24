package edu.sunmoon.hydroclock.core;

import java.util.concurrent.*;

public class ReminderService {
    private final Notifier notifier;
    private final Storage storage; // for logging
    private final ScheduledExecutorService exec =
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "hydroclock-reminder");
            t.setDaemon(true);
            return t;
        });
    private ScheduledFuture<?> task;

    public ReminderService(Notifier notifier) {
        this.notifier = notifier;
        this.storage = null; // optionally, you can inject Storage via constructor if you want to log from here
    }

    public synchronized void start(int intervalMin) {
        if (task == null) schedule(intervalMin);
    }

    public synchronized void restart(int intervalMin) {
        stop();
        schedule(intervalMin);
    }

    public synchronized void stop() {
        if (task != null) { task.cancel(false); task = null; }
    }

    public void shutdown() { stop(); exec.shutdownNow(); }

    public void setBeep(boolean on) { notifier.setBeep(on); }

    private void schedule(int min) {
        int m = Math.max(5, min);
        task = exec.scheduleAtFixedRate(() -> {
            String title = "HydroClock";
            String message = "물을 한 컵 마실 시간이에요 💧";
            // notify
            notifier.notify(title, message);
            // Try to log if storage accessible (optional)
            try {
                // if you want to log, add Storage injection and call storage.appendNotifyLog(title, message);
            } catch (Exception ignore) {}
        }, m, m, TimeUnit.MINUTES);
    }
}

