package me.mafrans.poppo.util.timedtasks;

import me.mafrans.poppo.Main;

public class CleanRunner implements Runnable {
    @Override
    public void run() {
        int lastLength = Main.userList.length();

        Main.userList.removeBy("names", "");
        Main.userList.removeBy("uuid", "");

        System.out.println("CleanRunner: Removed " + (lastLength - Main.userList.length()) + " invalid entries.");
    }
}
