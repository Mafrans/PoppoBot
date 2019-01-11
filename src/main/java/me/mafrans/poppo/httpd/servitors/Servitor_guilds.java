package me.mafrans.poppo.httpd.servitors;

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
import me.mafrans.poppo.util.config.ServerPrefs;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
        System.out.println(userSession);
        System.out.println(userSession.getUser());
        User user = Main.jda.getUserById(userSession.getUser().getId());
        System.out.println(user);

        List<Guild> allowedGuilds = new ArrayList<>();
        for (Guild g : Main.jda.getGuilds()) {
            if(user == null) break;
            if(user.getMutualGuilds() == null || user.getMutualGuilds().size() == 1) continue;
            if(user.getMutualGuilds().contains(g)) {
                Rank rank = Rank.getRank(g.getMember(user));
                if (rank != null && rank.hasRank(Rank.BOT_COMMANDER)) {
                    allowedGuilds.add(g);
                }
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

            JSONObject prefs = null;
            try {
                prefs = Main.serverPrefs.getPreferences(guild);
            } catch (IOException e) {
                throw new HTTPInternalErrorException();
            }

            VARIABLES.put("httpd_url", Main.config.httpd_url);
            VARIABLES.put("guild_id", guild.getId());

            if(event.getSimpleParameters().containsKey("addlink")) {
                String linkToAdd = event.getSimpleParameters().get("addlink").toLowerCase();
                if(!linkToAdd.isEmpty()) {
                    JSONArray links = new JSONArray();
                    if(prefs.has("twitch_links")) {
                        links = prefs.getJSONArray("twitch_links");
                    }

                    if (!ArrayUtils.contains(links.toList().toArray(), linkToAdd)) {
                        links.put(linkToAdd);
                    }
                    prefs.put("twitch_links", links);
                    try {
                        Main.serverPrefs.savePreferences(guild, prefs);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(event.getSimpleParameters().containsKey("deletelink")) {
                String linkToDelete = event.getSimpleParameters().get("deletelink").toLowerCase();
                if(!linkToDelete.isEmpty()) {
                    JSONArray links = new JSONArray();
                    if(prefs.has("twitch_links")) {
                        links = prefs.getJSONArray("twitch_links");
                    }
                    if(links.length() > 0) {
                        for(int i = 0; i < links.length(); i++) {
                            if(links.getString(i).equalsIgnoreCase(linkToDelete)) {
                                links.remove(i);
                            }
                        }
                    }
                    prefs.put("twitch_links", links);
                    try {
                        Main.serverPrefs.savePreferences(guild, prefs);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(event.getSimpleParameters().containsKey("setchannel")) {
                String channel = event.getSimpleParameters().get("setchannel").toLowerCase();
                prefs.put("twitch_message_channel", channel);
                try {
                    Main.serverPrefs.savePreferences(guild, prefs);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder twitchLinkBuilder = new StringBuilder();
            for(int i = 0; i < prefs.getJSONArray("twitch_links").length(); i++) {
                String twitchLink = prefs.getJSONArray("twitch_links").getString(i);
                twitchLinkBuilder.append("<div class=\"twitchLinkContainer\">");
                twitchLinkBuilder.append("  <span class=\"twitchLinkName\">" + twitchLink + "</span>");
                twitchLinkBuilder.append("  <button type=\"button\" onclick=\"deleteLink(\'" + twitchLink + "\')\" class=\"twitchLinkDeleteButton\">Delete</button>");
                twitchLinkBuilder.append("</div>");
            }
            VARIABLES.put("twitch_link_list", twitchLinkBuilder.toString());

            StringBuilder messageChannelOptionBuilder = new StringBuilder();
            for(TextChannel channel : guild.getTextChannels()) {
                messageChannelOptionBuilder.append("<option value=\"" + channel.getId() + "\">#" + channel.getName() + "</option>");
            }
            VARIABLES.put("channel_options", messageChannelOptionBuilder.toString());

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
            for (com.adamratzman.oauth.models.Guild guild : userSession.getGuilds()) {
                listBuilder.append("<li>" + guild.getName() + (guild.getOwner() ? " (Owner)" : "") + "</li>");
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
