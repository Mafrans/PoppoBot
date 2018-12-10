package me.mafrans.poppo.httpd;

import com.adamratzman.oauth.models.Guild;
import com.adamratzman.oauth.models.Identify;
import com.adamratzman.oauth.models.Token;
import com.mrpowergamerbr.temmiediscordauth.responses.CurrentUserResponse;
import com.mrpowergamerbr.temmiediscordauth.utils.TemmieGuild;

import java.util.List;

public class UserSession {
    private Token token = null;
    private Identify user = null;
    private Guild[] guilds = null;

    public UserSession(Token token, Identify user, Guild[] guilds) {
        this.token = token;
        this.user = user;
        this.guilds = guilds;
    }

    public Token getToken() {
        return token;
    }

    public Identify getUser() {
        return user;
    }

    public Guild[] getGuilds() {
        return guilds;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserSession && ((UserSession) obj).token.equals(token);
    }
}
