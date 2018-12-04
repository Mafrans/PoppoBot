package me.mafrans.poppo.httpd.servitors;

import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.events.DocumentServeEvent;
import me.mafrans.mahttpd.exceptions.HTTPForbiddenException;
import me.mafrans.mahttpd.exceptions.HTTPInternalErrorException;
import me.mafrans.mahttpd.exceptions.HTTPNotFoundException;
import me.mafrans.mahttpd.servitors.HTMLServitor;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.commands.util.CommandHandler;
import me.mafrans.poppo.commands.util.ICommand;

import java.io.File;
import java.io.IOException;

public class Servitor_help extends HTMLServitor {
    public Servitor_help(MaHTTPD maHTTPD) {
        super(maHTTPD);
    }

    @Override
    public String serve(DocumentServeEvent event) throws HTTPNotFoundException, HTTPInternalErrorException, HTTPForbiddenException {
        StringBuilder stringBuilder = new StringBuilder();

        for(CommandCategory category : CommandCategory.values()) {
            stringBuilder.append("<h2><img src=\"" + category.getIconUrl() + "\"/>" + category.getName() + "</h2>" + "\n");
            stringBuilder.append("<ul class=\"command-list\">");
            for(ICommand cmd : CommandHandler.getCommands()) {
                if(cmd.getMeta().getCategory() == category) {
                    stringBuilder.append("<li class=\"command-list-entry\"><p class=\"command-name\">" + cmd.getName() + "<span class=\"command-usage\"> | " + cmd.getMeta().getUsage() + "</span></p></li>");
                }
            }
            stringBuilder.append("</ul>");
        }

        VARIABLES.put("command_list", stringBuilder.toString());

        return event.getDocument();
    }

    @Override
    public File getDocument() {
        File outFile = new File("documents/html/help.html");

        try {
            FileUtils.createResource("documents/html/help.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    public File getSingleCommandDocument() {
        File outFile = new File("documents/html/single_command.html");

        try {
            FileUtils.createResource("documents/html/single_command.html", outFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;
    }

    @Override
    public String getURL() {
        return "help";
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
