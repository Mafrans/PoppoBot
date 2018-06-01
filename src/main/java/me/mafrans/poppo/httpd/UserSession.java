package me.mafrans.poppo.httpd;

import com.mrpowergamerbr.temmiediscordauth.responses.CurrentUserResponse;
import com.mrpowergamerbr.temmiediscordauth.utils.TemmieGuild;

import java.util.List;

public class UserSession {
    private String token = null;
    private CurrentUserResponse user = null;
    private List<TemmieGuild> guilds = null;

    public UserSession(String token, CurrentUserResponse user, List<TemmieGuild> guilds) {
        this.token = token;
        this.user = user;
        this.guilds = guilds;
    }

    public String getToken() {
        return token;
    }

    public CurrentUserResponse getUser() {
        return user;
    }

    public List<TemmieGuild> getGuilds() {
        return guilds;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserSession && ((UserSession) obj).token.equals(token);
    }
}
