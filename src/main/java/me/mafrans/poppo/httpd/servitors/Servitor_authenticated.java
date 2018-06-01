package me.mafrans.poppo.httpd.servitors;

import com.mrpowergamerbr.temmiediscordauth.utils.TemmieGuild;
import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.events.DocumentServeEvent;
import me.mafrans.mahttpd.exceptions.HTTPForbiddenException;
import me.mafrans.mahttpd.exceptions.HTTPInternalErrorException;
import me.mafrans.mahttpd.exceptions.HTTPNotFoundException;
import me.mafrans.mahttpd.servitors.HTMLServitor;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.httpd.Machine;
import me.mafrans.poppo.httpd.SessionHandler;
import me.mafrans.poppo.httpd.UserSession;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Servitor_authenticated extends HTMLServitor {
    public Servitor_authenticated(MaHTTPD maHTTPD) {
        super(maHTTPD);
    }

    @Override
    public String serve(DocumentServeEvent event) throws HTTPNotFoundException, HTTPInternalErrorException, HTTPForbiddenException {
        SessionHandler handler = Main.sessionHandler;
        Map<Machine, UserSession> loadedSessions = handler.getLoadedSessions();
        Machine thisMachine = handler.getMachine(event.getSession());

        if(!handler.contains(thisMachine)) { // Not Authenticated
            try {
                return notAuthenticated();
            }
            catch (IOException e) {
                throw new HTTPInternalErrorException();
            }
        }

        UserSession userSession = loadedSessions.get(thisMachine);

        VARIABLES.put("user_name", userSession.getUser().getUsername());
        VARIABLES.put("user_discriminator", userSession.getUser().getDiscriminator());
        VARIABLES.put("user_id", userSession.getUser().getId());
        VARIABLES.put("user_avatar_url", "https://cdn.discordapp.com/avatars/{variable.user_id}/" + userSession.getUser().getAvatar() + ".png");

        StringBuilder listBuilder = new StringBuilder();
        listBuilder.append("<ul>");
        for(TemmieGuild guild : userSession.getGuilds()) {
            listBuilder.append("<li>" + guild.getName() + (guild.isOwner() ? " (Owner)" : "") + "</li>");
        }
        listBuilder.append("</ul>");
        VARIABLES.put("guilds", listBuilder.toString());

        return event.getDocument();
    }

    public static String notAuthenticated() throws IOException {
        File outFile = new File("documents/html/not_authenticated.html");
        FileUtils.createResource("documents/html/not_authenticated.html", outFile);
        return FileUtils.readFile(outFile);
    }

    @Override
    public File getDocument() {
        File outFile = new File("documents/html/authenticated.html");

        try {
            FileUtils.createResource("documents/html/authenticated.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    @Override
    public String getURL() {
        return "auth";
    }

    @Override
    public File getHeader() {
        File outFile = new File("documents/html/header.html");
        try {
            FileUtils.createResource("documents/html/header.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
    }
}
