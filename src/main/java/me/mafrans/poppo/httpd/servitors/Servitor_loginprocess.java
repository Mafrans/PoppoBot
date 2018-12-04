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

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Servitor_loginprocess extends HTMLServitor {
    public Servitor_loginprocess(MaHTTPD maHTTPD) {
        super(maHTTPD);
    }

    @Override
    public String serve(DocumentServeEvent event) throws HTTPNotFoundException, HTTPInternalErrorException, HTTPForbiddenException {

        Map<String, String> parameters = event.getSimpleParameters();
        if(!parameters.containsKey("code")) {
            try {
                return noCodeProvided();
            }
            catch (IOException e) {
                throw new HTTPInternalErrorException();
            }
        }
        SessionHandler handler = Main.sessionHandler;

        Machine machine = handler.getMachine(event.getSession());
        UserSession userSession = handler.makeSession(parameters.get("code"));

        handler.unloadSession(userSession); // Unload previous sessions
        handler.loadSession(machine, userSession); // Load new session

        VARIABLES.put("httpd_url", Main.config.httpd_url);

        return event.getDocument();
    }

    public String noCodeProvided() throws IOException {
        File outFile = new File("documents/html/login_process_nocode.html");
        FileUtils.createResource("documents/html/login_process_nocode.html", outFile);
        return FileUtils.readFile(outFile);
    }

    @Override
    public File getDocument() {
        File outFile = new File("documents/html/login_process.html");

        try {
            FileUtils.createResource("documents/html/login_process.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    @Override
    public String getURL() {
        return "redirect";
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
