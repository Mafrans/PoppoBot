package me.mafrans.poppo;


import me.mafrans.poppo.commands.*;
import me.mafrans.poppo.listeners.CommandListener;
import me.mafrans.poppo.listeners.SelectionListener;
import me.mafrans.poppo.listeners.TwitchEvents;
import me.mafrans.poppo.util.ConfigEntry;
import me.mafrans.poppo.util.ServerPrefs;
import me.mafrans.poppo.util.TimerTasks;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

public class Main {
    public static JDA jda;

    public static void main(String args[]) throws LoginException, InterruptedException, RateLimitedException, IOException {
        ConfigEntry.load(new File("config.properties"));

        jda = new JDABuilder(AccountType.BOT).setToken(ConfigEntry.TOKEN.getString()).buildBlocking();
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new SelectionListener());

        ServerPrefs.initPreferences();
        TimerTasks.start();

        CommandHandler.addCommand(new Command_ping());
        CommandHandler.addCommand(new Command_getinfo());
        CommandHandler.addCommand(new Command_define());
        CommandHandler.addCommand(new Command_config());
        CommandHandler.addCommand(new Command_debug());
    }
}
