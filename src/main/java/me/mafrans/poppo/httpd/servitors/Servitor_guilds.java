package me.mafrans.poppo.httpd.servitors;

import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.events.DocumentServeEvent;
import me.mafrans.mahttpd.exceptions.HTTPInternalErrorException;
import me.mafrans.mahttpd.exceptions.HTTPNotFoundException;
import me.mafrans.mahttpd.servitors.HTMLServitor;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.httpd.Machine;
import me.mafrans.poppo.httpd.SessionHandler;
import me.mafrans.poppo.httpd.UserSession;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Rank;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
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
    public String serve(DocumentServeEvent event) throws HTTPInternalErrorException, HTTPNotFoundException {
        VARIABLES.put("oauth_url", "https://discordapp.com/oauth2/authorize?redirect_uri=" + Main.config.httpd_url + "/redirect&scope=identify%20guilds&response_type=code&client_id=" + Main.config.client_id);
        VARIABLES.put("add_url", "https://discordapp.com/api/oauth2/authorize?client_id=" + Main.config.client_id + "&permissions=8&redirect_uri=" + Main.config.httpd_url+ "/redirect&scope=bot");

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
        List<Guild> allowedGuilds = getAllowedGuilds(user);

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

            JSONObject prefs;
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
                twitchLinkBuilder.append("  <span class=\"twitchLinkName\">").append(twitchLink).append("</span>");
                twitchLinkBuilder.append("  <button type=\"button\" onclick=\"deleteLink(\'").append(twitchLink).append("\')\" class=\"twitchLinkDeleteButton\">Delete</button>");
                twitchLinkBuilder.append("</div>");
            }
            VARIABLES.put("twitch_link_list", twitchLinkBuilder.toString());

            StringBuilder messageChannelOptionBuilder = new StringBuilder();
            for(TextChannel channel : guild.getTextChannels()) {
                if(channel.getId().equals(prefs.getString("twitch_message_channel"))) {
                    messageChannelOptionBuilder.append("<option value=\"").append(channel.getId()).append("\" selected>#").append(channel.getName()).append("</option>");
                }
                else {
                    messageChannelOptionBuilder.append("<option value=\"").append(channel.getId()).append("\">#").append(channel.getName()).append("</option>");
                }
            }
            VARIABLES.put("channel_options", messageChannelOptionBuilder.toString());

            File singleGuildDoc = getSingleGuildDocument();
            if(singleGuildDoc != null) {
                try {

                    return FileUtils.readFile(singleGuildDoc);

                }
                catch (IOException e) { throw new HTTPInternalErrorException(); }
            }
        }
        else { // Display Guild List
            StringBuilder guildHTMLBuilder = new StringBuilder();
            String unknownIcon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAQAAABIkb+zAAACQ0lEQVR4AezZNZAUURSF4a7NyHB3t5npd08RIRHukOLu7k6GS4ITQoa7Q4i7a4o7rO/DZce6b+2pjt534r/67qzveo7jOI7jOI7jOI7jaDSvZEbgMO7LZ/mE+3LQjIhVIPTRMPWwCQWwpSd52Bivze/p/D7Ihc2wXL8Pv6fCFNjsk4n8nkZ6SkngASWmN6XnMzXxBTZ48ileldDzyQ7YkNtO6NniDWDDTopNTULPZabDhp8ZS+i5cEJzgOwn9FzySHXAdULPhS+qA15yewL5rDrgBbcnwGPVATe4PYGc1H8S8noCzNB/GeT1BLGGmm9EiVrsngA7Q5+wjd8TJGrpfxhj9gSmd4jHl/id+D2NTAt6PMbzeyrpn/VXwu78ng519b+UM3uKVhX//7MIDmFkrAK/dxzHcRy2HMRkjKyX3XIZr2B/7xUuyG5ZL2Mk7uV4oaCvvAjuaRJNMVfOyTMUwGaf5MsznMU8v4kXwHTDF3WvJ3FZipuw+uHW9zLmZYF28knRa8UqYJE8hC3b5CEWZf45x3RAQaheK15V1uIrLGlfZW2m37LMYH0f/MqvQS4seblYk/6VlIO6PoAMxIfkmLYPMjDtPz5yFX12GEc4U/0/MCxX9+nJRMaJ+hNQGQWEN8EMgo1mZrCXRHar+2TNK+E9bER737ySVwrGq/tkZiFsdDMLvVL8Nuo+Ga7xztP/BwY11H0y5MJGuFyvlNrl1H0y2GhX1ud/a38OBAAAABgG+Vvf4yuDBAQEBAQEvgIAAAAQM/51H4xXwfgaAAAAAElFTkSuQmCC";

            for (Guild guild : allowedGuilds) {
                guildHTMLBuilder.append("<a href=\"?guild=").append(guild.getId()).append("\">");
                guildHTMLBuilder.append("<div class=\"guild-container\">");
                guildHTMLBuilder.append("    <img class=\"guild-image\" src=\"").append(guild.getIconUrl() == null ? unknownIcon : guild.getIconUrl()).append("\"></img>");
                guildHTMLBuilder.append("    <span class=\"guild-name\">").append(guild.getName()).append("</span>");
                guildHTMLBuilder.append("</div>");
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
                listBuilder.append("<li>").append(guild.getName()).append(guild.getOwner() ? " (Owner)" : "").append("</li>");
            }
            listBuilder.append("</ul>");
            VARIABLES.put("guilds", listBuilder.toString());

            return event.getDocument();
        }
        throw new HTTPNotFoundException();
    }

    private static String notAuthenticated() throws IOException {
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

    @Override
    public File[] getStylesheets() {
        File outFile1 = new File("documents/css/guilds.css");
        File outFile2 = new File("documents/css/single_guild.css");

        try {
            FileUtils.createResource("documents/css/guilds.css", outFile1);
            FileUtils.createResource("documents/css/single_guild.css", outFile2);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new File[] { outFile1, outFile2 };
    }

    private File getSingleGuildDocument() {
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

    public static List<Guild> getAllowedGuilds(User user) {
        List<Guild> allowedGuilds = new ArrayList<>();
        for (Guild g : Main.jda.getGuilds()) {
            if(user == null) break;
            if(user.getMutualGuilds() == null || user.getMutualGuilds().size() == 1) continue;
            if(user.getMutualGuilds().contains(g)) {
                Rank rank = Rank.getRank(g.getMember(user));
                if ((rank != null && rank.hasRank(Rank.BOT_COMMANDER)) || g.getOwner().getUser().getId().equals(user.getId())) {
                    allowedGuilds.add(g);
                }
            }
        }
        return allowedGuilds;
    }
}
