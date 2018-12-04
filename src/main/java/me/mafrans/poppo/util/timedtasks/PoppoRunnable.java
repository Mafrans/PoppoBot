package me.mafrans.poppo.util.timedtasks;

public abstract class PoppoRunnable implements Runnable {
    public Object[] arguments;
    public PoppoRunnable(Object[] arguments) {
        this.arguments = arguments;
    }

    public void run() {

    }
}
