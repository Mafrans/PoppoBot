package me.mafrans.poppo.util.timedtasks;

import me.mafrans.javadins.Javadins;
import me.mafrans.javadins.SessionInvalidException;
import me.mafrans.poppo.Main;

import java.io.IOException;
import java.text.ParseException;

@Deprecated
public class HirezRefreshRunner implements Runnable {
    @Override
    public void run() {
        try {
            Main.javadins.updateConnection();
            Main.smiteForge.updateConnection();
        }
        catch (IOException | SessionInvalidException | ParseException | me.mafrans.smiteforge.SessionInvalidException e) {
            e.printStackTrace();
        }
    }
}
