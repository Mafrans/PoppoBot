package me.mafrans.poppo.httpd.servitors;

import fi.iki.elonen.NanoHTTPD;
import me.mafrans.mahttpd.HTTPDServer;
import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.events.DocumentServeEvent;
import me.mafrans.mahttpd.exceptions.HTTPForbiddenException;
import me.mafrans.mahttpd.exceptions.HTTPInternalErrorException;
import me.mafrans.mahttpd.exceptions.HTTPNotFoundException;
import me.mafrans.mahttpd.exceptions.ServerNotStartedException;
import me.mafrans.mahttpd.servitors.HTMLServitor;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.httpd.Machine;
import me.mafrans.poppo.httpd.SessionHandler;
import me.mafrans.poppo.httpd.UserSession;
import me.mafrans.poppo.util.Feature;
import me.mafrans.poppo.util.GUtil;
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

public class Servitor_features extends HTMLServitor {
    public Servitor_features(MaHTTPD maHTTPD) {
        super(maHTTPD);
    }

    @Override
    public HTTPDServer getServer() throws ServerNotStartedException {
        return super.getServer();
    }

    @Override
    public String getURL() {
        return "features";
    }

    @Override
    public String serve(DocumentServeEvent event) throws HTTPNotFoundException, HTTPInternalErrorException, HTTPForbiddenException {
        VARIABLES.put("oauth_url", "https://discordapp.com/oauth2/authorize?redirect_uri=" + Main.config.httpd_url + "/redirect&scope=identify%20guilds&response_type=code&client_id=" + Main.config.client_id);
        VARIABLES.put("add_url", "https://discordapp.com/api/oauth2/authorize?client_id=" + Main.config.client_id + "&permissions=8&redirect_uri=" + Main.config.httpd_url+ "/redirect&scope=bot");

        SessionHandler handler = Main.sessionHandler;
        Map<Machine, UserSession> loadedSessions = handler.getLoadedSessions();
        Machine thisMachine = handler.getMachine(event.getSession());

        if(!handler.contains(thisMachine)) { // Not Authenticated
            return SessionHandler.redirect(Main.config.httpd_url + "/guilds");
        }

        UserSession userSession = loadedSessions.get(thisMachine);
        System.out.println(userSession);
        System.out.println(userSession.getUser());
        User user = Main.jda.getUserById(userSession.getUser().getId());
        System.out.println(user);

        List<Guild> allowedGuilds = Servitor_guilds.getAllowedGuilds(user);

        if(event.getSimpleParameters().containsKey("guild")) { // Display single guild
            Guild guild = null;
            for(Guild g : allowedGuilds) {
                if(Objects.equals(g.getId(), event.getSimpleParameters().get("guild"))) {
                    guild = g;
                }
            }

            if(guild == null) {
                return SessionHandler.redirect(Main.config.httpd_url + "/guilds");
            }

            VARIABLES.put("httpd_url", Main.config.httpd_url);
            VARIABLES.put("guild_id", guild.getId());
            VARIABLES.put("save_banner_visibility", "hidden");

            if(!event.getCookies().read("enabled").isEmpty() || event.getCookies().read("disabled").isEmpty()) {
                if (!event.getCookies().read("enabled").isEmpty()) {
                    String[] enabledIds = event.getCookies().read("enabled").split(",");
                    for (String id : enabledIds) {
                        new Feature(guild, id).setEnabled(true);
                    }
                }
                if(event.getCookies().read("disabled").isEmpty()) {
                    String[] disabledIds = event.getCookies().read("disabled").split(",");
                    for (String id : disabledIds) {
                        new Feature(guild, id).setEnabled(false);
                    }
                }
                event.getCookies().delete("enabled");
                event.getCookies().delete("disabled");
                return SessionHandler.redirect(Main.config.httpd_url + "/features?guild=" + guild.getId() + "&saved=true");
            }

            if(event.getSimpleParameters().containsKey("saved")) {
                VARIABLES.put("save_banner_visibility", event.getSimpleParameters().get("saved").equalsIgnoreCase("true") ? "" : "hidden");
            }

            StringBuilder stringBuilder = new StringBuilder();
            Map<Class<?>, Id> features = Feature.getFeatureCache();
            Map<String, Map<Class<?>, Id>> categories = new HashMap<>();

            for(Class<?> clazz : features.keySet()) {
                Id id = features.get(clazz);
                String category = GUtil.capitalize(id.value().split("::")[0]);

                Map<Class<?>, Id> map = new HashMap<>();
                if(categories.containsKey(category)) {
                    map = categories.get(category);
                }
                map.put(clazz, id);
                categories.put(category, map);
            }


            for (String category : categories.keySet()) {
                stringBuilder.append("<fieldset class=\"feature-list form-group\">");
                stringBuilder.append("<legend>").append(category).append("</legend>");
                for (Class clazz : categories.get(category).keySet()) {
                    Id id = categories.get(category).get(clazz);

                    if(new Feature(guild, clazz).isEnabled()) {
                        stringBuilder.append("<div class=\"individual-feature\">");
                        stringBuilder.append("    <input class=\"form-check-input checked\" type=\"checkbox\" value=\"").append(id.value()).append("\" checked /><label class=\"form-check-label\" for=\"").append(id.value()).append("\"> ").append(GUtil.capitalize(id.value().split("::")[1])).append("</label>");
                        stringBuilder.append("</div>");
                    }
                    else {
                        stringBuilder.append("<div class=\"individual-feature\">");
                        stringBuilder.append("    <input class=\"form-check-input\" type=\"checkbox\" value=\"\"").append(id.value()).append("\"\"><label class=\"form-check-label\" for=\"").append(id.value()).append("\"> ").append(GUtil.capitalize(id.value().split("::")[1])).append("</label>");
                        stringBuilder.append("</div>");
                    }
                }
                stringBuilder.append("</fieldset>");
            }
            VARIABLES.put("features", stringBuilder.toString());
            return event.getDocument();
        }
        return SessionHandler.redirect(Main.config.httpd_url + "/guilds");
    }

    @Override
    public File getDocument() {
        File outFile = new File("documents/html/features.html");

        try {
            FileUtils.createResource("documents/html/features.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    private static String notAuthenticated() throws IOException {
        File outFile = new File("documents/html/not_authenticated.html");
        FileUtils.createResource("documents/html/not_authenticated.html", outFile);
        return FileUtils.readFile(outFile);
    }

    @Override
    public File[] getStylesheets() {
        File outFile = new File("documents/css/features.css");

        try {
            FileUtils.createResource("documents/css/features.css", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new File[] { outFile };
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
