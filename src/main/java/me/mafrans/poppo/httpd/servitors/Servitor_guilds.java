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
import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Servitor_guilds extends HTMLServitor {
    public Servitor_guilds(MaHTTPD maHTTPD) {
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
        User user = Main.jda.getUserById(userSession.getUser().getId());
        List<Guild> allowedGuilds = new ArrayList<>();
        for (Guild g : Main.jda.getGuilds()) {
            Rank rank = Rank.getRank(g.getMember(user));
            if (rank != null && rank.hasRank(Rank.BOT_COMMANDER)) {
                allowedGuilds.add(g);
            }
        }

        if(event.getSimpleParameters().containsKey("guild")) { // Display single guild
            Guild guild = null;
            for(Guild g : allowedGuilds) {
                if(Objects.equals(g.getId(), event.getSimpleParameters().get("guild"))) {
                    guild = g;
                }
            }

            if(guild == null) {
                try {
                    return notAuthenticated();
                }
                catch (IOException e) {
                    throw new HTTPInternalErrorException();
                }
            }

            VARIABLES.put("httpd_url", ConfigEntry.HTTPD_URL.getString());
            VARIABLES.put("guild_id", guild.getId());

            if(event.getSimpleParameters().containsKey("addlink")) {
                String linkToAdd = event.getSimpleParameters().get("addlink").toLowerCase();
                if(!linkToAdd.isEmpty()) {
                    if(ServerPrefs.TWITCH_LINKS.getString(guild) == null) {
                        try {
                            ServerPrefs.TWITCH_LINKS.setString(guild, linkToAdd);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String[] currentLinks = ServerPrefs.TWITCH_LINKS.getString(guild).toLowerCase().split(",");
                        if (!ArrayUtils.contains(currentLinks, linkToAdd)) {
                            try {
                                ServerPrefs.TWITCH_LINKS.setString(guild, StringUtils.join(currentLinks, ",") + "," + linkToAdd);
                                //event.getSession().execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if(event.getSimpleParameters().containsKey("deletelink")) {
                String linkToDelete = event.getSimpleParameters().get("deletelink").toLowerCase();
                if(!linkToDelete.isEmpty()) {
                    if(ServerPrefs.TWITCH_LINKS.getString(guild) != null) {
                        String[] currentLinks = ServerPrefs.TWITCH_LINKS.getString(guild).toLowerCase().split(",");
                        List<String> currentLinkList = new ArrayList<>();
                        for(String link : currentLinks) {
                            currentLinkList.add(link);
                        }

                        if(currentLinkList.contains(linkToDelete)) {
                            currentLinkList.remove(linkToDelete);
                        }

                        try {
                            ServerPrefs.TWITCH_LINKS.setString(guild, StringUtils.join(currentLinkList, ","));
                            //event.getSession().execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            String twitchLinkString = ServerPrefs.TWITCH_LINKS.getString(guild);
            String[] twitchLinkArray = new String[0];
            if(twitchLinkString != null && !twitchLinkString.isEmpty()) {
                twitchLinkArray = twitchLinkString.split(",");
            }
            System.out.println(twitchLinkString);
            System.out.println(Arrays.toString(twitchLinkArray));

            StringBuilder twitchLinkBuilder = new StringBuilder();
            for(String twitchLink : twitchLinkArray) {
                twitchLinkBuilder.append("<div class=\"twitchLinkContainer\">");
                twitchLinkBuilder.append("  <span class=\"twitchLinkName\">" + twitchLink + "</span>");
                twitchLinkBuilder.append("  <button type=\"button\" onclick=\"deleteLink(\'" + twitchLink + "\')\" class=\"twitchLinkDeleteButton\">Delete</button>");
                twitchLinkBuilder.append("</div>");
            }
            VARIABLES.put("twitch_link_list", twitchLinkBuilder.toString());

            try {
                return FileUtils.readFile(getSingleGuildDocument());
            }
            catch (IOException e) {
                throw new HTTPInternalErrorException();
            }
        }
        else { // Display Guild List
            StringBuilder guildHTMLBuilder = new StringBuilder();
            for (Guild guild : allowedGuilds) {
                guildHTMLBuilder.append("<a href=\"?guild=" + guild.getId() + "\">");
                guildHTMLBuilder.append("  <div class=\"guildContainer\">");
                guildHTMLBuilder.append("    <img class=\"guildImage\" src=\"" + guild.getIconUrl() + "\"></img>");
                guildHTMLBuilder.append("  </div>");
                guildHTMLBuilder.append("</a>");
            }

            VARIABLES.put("guild_list", guildHTMLBuilder.toString());

            VARIABLES.put("user_name", userSession.getUser().getUsername());
            VARIABLES.put("user_discriminator", userSession.getUser().getDiscriminator());
            VARIABLES.put("user_id", userSession.getUser().getId());
            VARIABLES.put("user_avatar_url", "https://cdn.discordapp.com/avatars/{variable.user_id}/" + userSession.getUser().getAvatar() + ".png");

            StringBuilder listBuilder = new StringBuilder();
            listBuilder.append("<ul>");
            for (TemmieGuild guild : userSession.getGuilds()) {
                listBuilder.append("<li>" + guild.getName() + (guild.isOwner() ? " (Owner)" : "") + "</li>");
            }
            listBuilder.append("</ul>");
            VARIABLES.put("guilds", listBuilder.toString());

            return event.getDocument();
        }
    }

    public static String notAuthenticated() throws IOException {
        File outFile = new File("documents/html/not_authenticated.html");
        FileUtils.createResource("documents/html/not_authenticated.html", outFile);
        return FileUtils.readFile(outFile);
    }

    @Override
    public File getDocument() {
        File outFile = new File("documents/html/guilds.html");

        try {
            FileUtils.createResource("documents/html/guilds.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    public File getSingleGuildDocument() {
        File outFile = new File("documents/html/single_guild.html");

        try {
            FileUtils.createResource("documents/html/single_guild.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    @Override
    public String getURL() {
        return "guilds";
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
