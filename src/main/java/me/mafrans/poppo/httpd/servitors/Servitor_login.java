package me.mafrans.poppo.httpd.servitors;

import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.events.DocumentServeEvent;
import me.mafrans.mahttpd.exceptions.HTTPForbiddenException;
import me.mafrans.mahttpd.exceptions.HTTPInternalErrorException;
import me.mafrans.mahttpd.exceptions.HTTPNotFoundException;
import me.mafrans.mahttpd.exceptions.ServerNotStartedException;
import me.mafrans.mahttpd.servitors.HTMLServitor;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.Main;

import java.io.File;
import java.io.IOException;

public class Servitor_login extends HTMLServitor {
    public Servitor_login(MaHTTPD maHTTPD) {
        super(maHTTPD);
    }

    @Override
    public String serve(DocumentServeEvent event) throws HTTPNotFoundException, HTTPInternalErrorException, HTTPForbiddenException {
        if(event.getDocument() == null || event.getDocument().isEmpty()) {
            throw new HTTPNotFoundException();
        }

        VARIABLES.put("oauth_url", "https://discordapp.com/oauth2/authorize?redirect_uri=" + Main.config.httpd_url + "/redirect&scope=identify%20guilds&response_type=code&client_id=" + Main.config.client_id);
        VARIABLES.put("add_url", "https://discordapp.com/api/oauth2/authorize?client_id=" + Main.config.client_id + "&permissions=8&redirect_uri=" + Main.config.httpd_url+ "/redirect&scope=bot");
        try {
            return generateScripts(event.getDocument());
        } catch (IOException | ServerNotStartedException e) {
            e.printStackTrace();
        }
        return event.getDocument();
    }

    @Override
    public File getDocument() {
        try {

            File outFile = new File("documents/html/login.html");
            FileUtils.createResource("documents/html/login.html", outFile);
            return outFile;

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File[] getScripts() {
        File scripts = new File("documents/js/scripts.js");
        try {
            FileUtils.createResource("documents/js/scripts.css", scripts);
            return new File[] {scripts};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getURL() {
        return "login";
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
