package me.mafrans.poppo.httpd;

import com.adamratzman.oauth.main.DiscordAuthenticator;
import com.adamratzman.oauth.models.Guild;
import com.adamratzman.oauth.models.Identify;
import com.adamratzman.oauth.models.Token;
import com.mrpowergamerbr.temmiediscordauth.TemmieDiscordAuth;
import com.mrpowergamerbr.temmiediscordauth.responses.CurrentUserResponse;
import com.mrpowergamerbr.temmiediscordauth.utils.TemmieGuild;
import fi.iki.elonen.NanoHTTPD;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.StringFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionHandler {
    private Map<Machine, UserSession> loadedSessions = null;
    private String callback = null;
    private String clientID = null;
    private String clientSecret = null;

    public SessionHandler(String callback, String clientID, String clientSecret) {
        this.callback = callback;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        loadedSessions = new HashMap<>();
    }

    public Machine getMachine(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.CookieHandler cookieHandler = session.getCookies();
        String id = cookieHandler.read("machine_id");
        if(id == null || id.isEmpty()) {
            id = StringFormatter.getRandom(32);
            cookieHandler.set(new NanoHTTPD.Cookie("machine_id",id));
        }

        Machine machine = new Machine(id, session.getRemoteIpAddress());
        for(Machine m : loadedSessions.keySet()) {
            if(m.equals(machine)) {
                return m;
            }
        }

        return machine;
    }

    public static String redirect(String url) {
        String js = "(function() { window.open('" + url + "','_self'); })();";
        return "<html><body><script>" + js + "</script></body></html>";
    }

    public UserSession makeSession(String auth) {
        DiscordAuthenticator authenticator = new DiscordAuthenticator.Builder()
                .setOauthConfiguration(Main.config.httpd_url, callback)
                .setBotConfiguration("Poppo", clientID, clientSecret, Main.config.token)
                .build();
        Token token = authenticator.getToken(auth);

        if(token == null) {
            return null;
        }

        //String token = temmie.getAccessToken();
        Identify user = authenticator.getUser(token);
        Guild[] guilds = authenticator.getUserGuilds(token);

        return new UserSession(token, user, guilds);
    }

    public boolean contains(Machine machine) {
        for(Machine m : getLoadedSessions().keySet()) {
            if(m.equals(machine)) {
                return true;
            }
        }
        return false;
    }

    public void loadSession(Machine machine, UserSession session) {
        loadedSessions.put(machine, session);
    }

    public void unloadSession(Machine machine) {
        loadedSessions.remove(machine);
    }

    public void unloadSession(UserSession session) {
        for(Machine machine : loadedSessions.keySet()) {
            UserSession us = loadedSessions.get(machine);

            if(session.equals(us)) {
                loadedSessions.remove(machine);
            }
        }
    }

    public void clearSessions() {
        loadedSessions.clear();
    }

    public Map<Machine, UserSession> getLoadedSessions() {
        return loadedSessions;
    }
}
