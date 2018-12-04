package me.mafrans.poppo;


import me.mafrans.mahttpd.HTTPDServer;
import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.commands.*;
import me.mafrans.poppo.commands.util.CommandHandler;
import me.mafrans.poppo.httpd.SessionHandler;
import me.mafrans.poppo.httpd.servitors.Servitor_guilds;
import me.mafrans.poppo.httpd.servitors.Servitor_login;
import me.mafrans.poppo.httpd.servitors.Servitor_loginprocess;
import me.mafrans.poppo.listeners.CommandListener;
import me.mafrans.poppo.listeners.SelectionListener;
import me.mafrans.poppo.listeners.UserListener;
import me.mafrans.poppo.util.TimerTasks;
import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.objects.Rank;
import me.mafrans.poppo.util.objects.UserList;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

public class Main {
    public static JDA jda;
    public static SessionHandler sessionHandler;
    public static MaHTTPD maHTTPD;
    public static HTTPDServer httpdServer;
    public static UserList userList;

    public static void main(String args[]) throws LoginException, InterruptedException, RateLimitedException, IOException, ClassNotFoundException {
        ConfigEntry.load(new File("config.properties"));

        File databaseFile = new File("userdata/users.db");
        if(!databaseFile.getParentFile().exists()) {
            databaseFile.getParentFile().mkdirs();
        }
        userList = new UserList();
        userList.connect(databaseFile.getAbsolutePath(), ConfigEntry.DATABASE_USERNAME.getString(), ConfigEntry.DATABASE_PASSWORD.getString());

        sessionHandler = new SessionHandler(ConfigEntry.HTTPD_URL.getString() + "/redirect", ConfigEntry.CLIENT_ID.getString(), ConfigEntry.CLIENT_SECRET.getString());

        jda = new JDABuilder(AccountType.BOT).setToken(ConfigEntry.TOKEN.getString()).buildBlocking();
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new SelectionListener());
        jda.addEventListener(new UserListener());

        ServerPrefs.initPreferences();
        TimerTasks.start();

        CommandHandler.addCommand(new Command_ping());
        CommandHandler.addCommand(new Command_getinfo());
        CommandHandler.addCommand(new Command_define());
        CommandHandler.addCommand(new Command_config());
        CommandHandler.addCommand(new Command_debug());
        CommandHandler.addCommand(new Command_identify());
        CommandHandler.addCommand(new Command_help());
        CommandHandler.addCommand(new Command_roll());
        CommandHandler.addCommand(new Command_8ball());
        CommandHandler.addCommand(new Command_move());
        CommandHandler.addCommand(new Command_timeout());
        CommandHandler.addCommand(new Command_shutdown());

        for(ConfigEntry entry : ConfigEntry.values()) {
            System.out.println(entry.getKey() + ": " + entry.getString());
        }

        System.out.println("MaHTTPD Web Server Started");
        maHTTPD = new MaHTTPD();

        httpdServer = maHTTPD.startServer(8081);
        httpdServer.registerServitor(new Servitor_login(maHTTPD));
        httpdServer.registerServitor(new Servitor_loginprocess(maHTTPD));
        httpdServer.registerServitor(new Servitor_guilds(maHTTPD));
        httpdServer.setHomePage(new Servitor_login(maHTTPD));

        // Create Header and stylesheet file
        File stylesheet = new File("documents/css/stylesheet.css");
        FileUtils.createResource("documents/css/stylesheet.css", stylesheet);

        httpdServer.addGlobalStylesheet(stylesheet);

        for(Rank rank : Rank.values()) {
            rank.initialize();
        }
    }
}
