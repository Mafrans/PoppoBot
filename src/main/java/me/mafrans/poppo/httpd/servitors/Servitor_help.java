package me.mafrans.poppo.httpd.servitors;

import me.mafrans.mahttpd.MaHTTPD;
import me.mafrans.mahttpd.events.DocumentServeEvent;
import me.mafrans.mahttpd.exceptions.HTTPInternalErrorException;
import me.mafrans.mahttpd.servitors.HTMLServitor;
import me.mafrans.mahttpd.util.FileUtils;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Servitor_help extends HTMLServitor {
    public Servitor_help(MaHTTPD maHTTPD) {
        super(maHTTPD);
    }

    @Override
    public String serve(DocumentServeEvent event) throws HTTPInternalErrorException {

        File script = new File("documents/js/single_help.js");
        try {
            FileUtils.createResource("documents/js/single_help.js", script);
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        if(event.getSimpleParameters().containsKey("command")) {
            String commandString = event.getSimpleParameters().get("command");
            ICommand command = CommandHandler.getCommand(commandString);

            if(command == null) {
                VARIABLES.put("command_list", generateCommandList());
                return event.getDocument();
            }

            VARIABLES.put("command_name", command.getName());

            CommandMeta meta = command.getMeta();
            VARIABLES.put("command_usage", meta.getUsage());
            VARIABLES.put("command_description", meta.getDescription());
            VARIABLES.put("command_category_name", meta.getCategory().getName());
            VARIABLES.put("command_category_icon", meta.getCategory().getIconUrl());
            VARIABLES.put("command_aliases", generateAliasList(meta.getAliases()));

            try {
                return FileUtils.readFile(Objects.requireNonNull(getSingleCommandDocument()));
            } catch (IOException e) {
                throw new HTTPInternalErrorException();
            }
        }
        else {
            VARIABLES.put("command_list", generateCommandList());
        }

        return event.getDocument();
    }

    @Override
    public File[] getScripts() {
        return new File[] {new File("documents/js/single_help.js")};
    }

    private String generateAliasList(String[] aliases) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String alias : aliases) {
            stringBuilder.append("<div class='command-alias'>").append(alias).append("</div>");
        }
        return stringBuilder.toString();
    }

    private String generateCommandList() {
        StringBuilder stringBuilder = new StringBuilder();
        for(CommandCategory category : CommandCategory.values()) {
            System.out.println(stringBuilder.toString());
            stringBuilder.append("<div class='command-category-container>");
            stringBuilder.append("<h2 class='command-category-name'><img class='command-category-image' src='").append(category.getIconUrl()).append("'/>").append(category.getName()).append("</h2>").append("\n");
            stringBuilder.append("<ul class='command-list'>");
            for(ICommand cmd : CommandHandler.getCommandList()) {
                if(cmd.getMeta().getCategory() == category) {
                    stringBuilder.append("<li class='command-list-entry' href='").append(Main.config.httpd_url).append("/help?command=").append(cmd.getName()).append("'><p class='command-name'>").append(cmd.getName()).append("<span class='command-usage'> | ").append(cmd.getMeta().getUsage()).append("</span></p></li>");
                }
            }
            stringBuilder.append("</ul>");
            stringBuilder.append("</div>");
        }
        return stringBuilder.toString();
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

    private File getSingleCommandDocument() {
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
