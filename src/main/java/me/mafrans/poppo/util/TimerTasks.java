package me.mafrans.poppo.util;

import me.mafrans.poppo.util.timedtasks.AutoInsult;
import me.mafrans.poppo.util.timedtasks.TwitchEvents;

public class TimerTasks {
    private static boolean running = true;
    private static int iteration = 0;

    public static void stop() {
        iteration = 0;
        running = false;
    }

    public static void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while(running) {

                    queueTask(TwitchEvents.getRunner(), 10);
                    queueTask(new AutoInsult(), 120); // 3h

                    try {
                        Thread.sleep(1 * 60 * 1000);
                    }
                    catch (InterruptedException e) {
                        return;
                    }

                    iteration++;
                }
            }
        });
        thread.start();
    }

    public static void queueTask(Runnable task, int minutes) {
        if(iteration == 0) return;

        if(iteration % minutes == 0) {
            task.run();
        }
    }
}