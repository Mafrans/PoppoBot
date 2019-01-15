package me.mafrans.poppo;


//import me.mafrans.javadins.Javadins;
//import me.mafrans.javadins.SessionInvalidException;
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
import me.mafrans.poppo.listeners.PollListener;
import me.mafrans.poppo.listeners.SelectionListener;
import me.mafrans.poppo.listeners.UserListener;
import me.mafrans.poppo.util.MusicManager;
import me.mafrans.poppo.util.TimerTasks;
import me.mafrans.poppo.util.config.ConfigManager;
import me.mafrans.poppo.util.config.ConfigObject;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.objects.*;
import me.mafrans.poppo.util.web.YoutubeSearcher;
//import me.mafrans.smiteforge.SmiteForge;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class Main {
    public static JDA jda;
    public static SessionHandler sessionHandler;
    public static MaHTTPD maHTTPD;
    public static HTTPDServer httpdServer;
    public static UserList userList;
    public static ConfigObject config;
    public static YoutubeSearcher youtubeSearcher;
    public static MusicManager musicManager;
    //public static Javadins javadins;
    //public static SmiteForge smiteForge;
    public static ServerPrefs serverPrefs;

    public static void main(String args[]) throws LoginException, InterruptedException, RateLimitedException, IOException, ClassNotFoundException, KeyManagementException, NoSuchAlgorithmException, ParseException {//, SessionInvalidException, me.mafrans.smiteforge.SessionInvalidException {

        config = ConfigManager.load();
        youtubeSearcher = new YoutubeSearcher();
        musicManager = new MusicManager();
        //javadins = new Javadins(config.hirez_dev_id, config.hirez_auth_key);
        //smiteForge = new SmiteForge(config.hirez_dev_id, config.hirez_auth_key);

        File databaseFile = new File("userdata/users.db");
        if(!databaseFile.getParentFile().exists()) {
            databaseFile.getParentFile().mkdirs();
        }
        userList = new UserList();
        userList.connect(databaseFile.getAbsolutePath(), config.database_username, config.database_username);

        sessionHandler = new SessionHandler(config.httpd_url + "/redirect", config.client_id, config.client_secret);

        jda = new JDABuilder(AccountType.BOT).setToken(config.token).buildBlocking();
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new SelectionListener());
        jda.addEventListener(new UserListener());
        jda.addEventListener(new PollListener());

        TimerTasks.start();

        CommandHandler.addCommand(new Command_avatar());
        CommandHandler.addCommand(new Command_beautify());
        CommandHandler.addCommand(new Command_cat());
        CommandHandler.addCommand(new Command_config());
        CommandHandler.addCommand(new Command_debug());
        CommandHandler.addCommand(new Command_define());
        CommandHandler.addCommand(new Command_dog());
        CommandHandler.addCommand(new Command_endpoll());
        CommandHandler.addCommand(new Command_flip());
        CommandHandler.addCommand(new Command_generate());
        CommandHandler.addCommand(new Command_get());
        CommandHandler.addCommand(new Command_getinfo());
        CommandHandler.addCommand(new Command_help());
        CommandHandler.addCommand(new Command_identify());
        CommandHandler.addCommand(new Command_mal());
        CommandHandler.addCommand(new Command_mention());
        CommandHandler.addCommand(new Command_move());
        CommandHandler.addCommand(new Command_mute());
        //CommandHandler.addCommand(new Command_paladins());
        CommandHandler.addCommand(new Command_ping());
        CommandHandler.addCommand(new Command_play());
        CommandHandler.addCommand(new Command_poll());
        CommandHandler.addCommand(new Command_roll());
        CommandHandler.addCommand(new Command_rule34());
        CommandHandler.addCommand(new Command_shutdown());
        CommandHandler.addCommand(new Command_skip());
        //CommandHandler.addCommand(new Command_smite());
        CommandHandler.addCommand(new Command_timeout());
        CommandHandler.addCommand(new Command_unmute());
        CommandHandler.addCommand(new Command_untimeout());
        CommandHandler.addCommand(new Command_8ball());

        System.out.println("MaHTTPD Web Server Started");
        maHTTPD = new MaHTTPD();

        httpdServer = maHTTPD.startServer(config.httpd_port);
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

        serverPrefs = new ServerPrefs(new File("servers"));
        for(Guild guild : jda.getGuilds()) {
            System.out.println(guild);
            serverPrefs.saveDefaults(guild);
        }

        Main.jda.getPresence().setStatus(OnlineStatus.ONLINE);

        CatBreed.cacheBreeds();
        CatCategory.cacheCategories();
        DogBreed.cacheBreeds();

        System.out.println("PoppoBot is Ready!");
    }
}
