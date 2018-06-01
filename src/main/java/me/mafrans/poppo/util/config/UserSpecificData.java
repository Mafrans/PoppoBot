package me.mafrans.poppo.util.config;

import me.mafrans.mahttpd.util.FileUtils;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;
import java.util.Random;

public class UserSpecificData {
    private File dataFile;
    private String dataFilePath;
    public UserSpecificData(File dataFile) throws IOException {
        if(!dataFile.exists()) {
            FileUtils.initializeFile(dataFile, "[{names:[\"username\"],uuid:\"123456789101112131\",lastonline:\"idk man\", avatar:\"http://localhost\"}]", false);
            System.out.println("File: " + dataFile.getPath());
            System.out.println("FilePath: " + dataFilePath);
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(dataFile));
        if(!reader.readLine().startsWith("[")) {
            dataFile.delete();
            FileUtils.initializeFile(dataFile, "[{names:[\"username\"],uuid:\"123456789101112131\",lastonline:\"idk man\", avatar:\"http://localhost\"}]", false);
        }
        reader.close();
        this.dataFilePath = dataFile.getAbsolutePath();
        this.dataFile = dataFile;
    }

    public void setJson(String jsonText) throws IOException {
        dataFile.delete();

        BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
        writer.write(jsonText);
        writer.close();
    }

    public JSONArray getJson() throws IOException {
        return new JSONArray(FileUtils.readFile(dataFile));
    }

    public DataUser getUser(String userid) throws IOException {
        System.out.println("File: " + dataFile.getPath());
        for(int i = 0; i < getJson().length(); i++) {
            if(getJson().getJSONObject(i).getString("uuid").equals(userid)) {
                return DataUser.parse(getJson().getJSONObject(i));
            }
        }
        return null;
    }

    public void setUser(User user, String lastOnlineDate) throws IOException {
        String name = user.getName();
        String uuid = user.getId();
        String avatar = user.getAvatarUrl();
        JSONArray json = getJson();
        boolean exists = false;
        int position = 0;

        for(int i = 0; i < getJson().length(); i++) {
            String jsonUuid = json.getJSONObject(i).getString("uuid");
            if(json.getJSONObject(i).has("uuid")) {
                if (jsonUuid.equalsIgnoreCase(uuid)) {
                    exists = true;
                    position = i;
                    break;
                }
            }
        }

        if(!exists) {
            json.put(new JSONObject("{names:[\"" + name + "\"],uuid:\"" + uuid + "\",lastonline:\"" + lastOnlineDate + "\", avatar:\"" + avatar + "\"}"));
            setJson(json.toString());
            return;
        }

        JSONObject userObject = json.getJSONObject(position);

        DataUser dataUser = DataUser.parse(userObject);
        List<String> names = dataUser.getNames();
        if(!names.contains(name)) {
            names.add(name);
        }

        dataUser.setNames(names);
        dataUser.setLastOnlineTag(lastOnlineDate);

        json.remove(position);
        json.put(dataUser.encode());
        setJson(json.toString());
    }

    public void setUserOnly(User user) throws IOException {
        String name = user.getName();
        String uuid = user.getId();
        JSONArray json = getJson();
        boolean exists = false;
        int position = 0;

        for(int i = 0; i < getJson().length(); i++) {
            String jsonUuid = json.getJSONObject(i).getString("uuid");
            if(json.getJSONObject(i).has("uuid")) {
                if (jsonUuid.equalsIgnoreCase(uuid)) {
                    exists = true;
                    position = i;
                    break;
                }
            }
        }

        if(!exists) {
            getJson().put(new JSONObject("{names:[\"" + name + "\"],uuid:\"" + uuid + "\",lastonline:\"unknown\"}"));
            setJson(getJson().toString());
            return;
        }
        JSONObject userObject = json.getJSONObject(position);

        DataUser dataUser = DataUser.parse(userObject);
        List<String> names = dataUser.getNames();
        names.add(name);

        dataUser.setNames(names);

        json.remove(position);
        json.put(dataUser.encode());
        setJson(json.toString());
    }
}

