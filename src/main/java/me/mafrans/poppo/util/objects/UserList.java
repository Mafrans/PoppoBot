package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.StringFormatter;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

public class UserList {
    Sql2o sql2o;
    List<DataUser> cache;

    private String table = Main.config.database_table;

    public void connect(String url, String user, String password) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        sql2o = new Sql2o("jdbc:sqlite:" + url, user, password);
        makeTables();

        try {
            updateCache();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void makeTables() {
        try(Connection con = sql2o.open()) {
            DatabaseMetaData dbm = con.getJdbcConnection().getMetaData();

            // check if table exists
            ResultSet tables = dbm.getTables(null, null, table, null);

            if (tables.next()) {
                return;
            }
            else {
                String query =  "CREATE TABLE " + table + " (" +
                        "names varchar," +
                        "uuid varchar(127)," +
                        "lastOnlineTag varchar(127)," +
                        "avatarUrl varchar(255)" +
                        ")";

                con.createQuery(query).executeUpdate();
                return;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean removeByUuid(String uuid) {
        return remove(new SQLDataUser(getUsersFrom("uuid", uuid).get(0)));
    }

    public boolean add(SQLDataUser dataUser) {
        if(getUsersFrom("uuid", dataUser.getUuid()).size() != 0) {
            if(!removeByUuid(dataUser.getUuid())) return false;
        }

        String keys = "";
        String values = "";

        for(String key : getFieldMap(dataUser).keySet()) {
            if(keys.isEmpty()) keys = key;
            else keys += ", " + key;

            if(values.isEmpty()) values = ":" + key;
            else values += ", :" + key;
        }

        String query = "INSERT INTO " + table + "(" + keys + ") values(" + values + ")";

        try (Connection con = sql2o.open()) {
            Query query1 = con.createQuery(query);

            for(String key : getFieldMap(dataUser).keySet()) {
                String value = getFieldMap(dataUser).get(key);
                query1.addParameter(key, value);
            }

            query1.executeUpdate();

            updateCache();
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(SQLDataUser dataUser) {
        String where = "";

        boolean b = true;
        for(String key : getFieldMap(dataUser).keySet()) {
            if(b) {
                b = false;
                where += key + " = :" + key;
            }
            else {
                where += " AND " + key + " = :" + key;
            }
        }

        String query = "DELETE FROM " + table + " WHERE " + where;

        try (Connection con = sql2o.open()) {
            Query query1 = con.createQuery(query);

            for(String key : getFieldMap(dataUser).keySet()) {
                String value = getFieldMap(dataUser).get(key);
                query1.addParameter(key, value);
            }

            query1.executeUpdate();

            updateCache();
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clear() {
        String query = "TRUNCATE TABLE " + table;

        try (Connection con = sql2o.open()) {
            con.createQuery(query).executeUpdate();
            updateCache();
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateCache() throws ParseException {
        List<SQLDataUser> sqlUsers = fetchSQLUsers();
        cache = parseUsers(sqlUsers);
    }

    public List<DataUser> getAllReports() {
        return cache;
    }

    public DataUser get(int index) {
        return cache.get(index);
    }

    public List<DataUser> getByName(String name) {
        List<DataUser> outUsers = new ArrayList<>();

        for(DataUser user : cache) {
            boolean isMatched = user.getNames().stream().anyMatch(name::equalsIgnoreCase);
            if(isMatched) {
                outUsers.add(user);
            }
        }

        return outUsers;
    }

    private List<SQLDataUser> fetchSQLUsers() {
        String query = "SELECT * FROM " + table;

        try(Connection con = sql2o.open()) {
            return con.createQuery(query).executeAndFetch(SQLDataUser.class);
        }
    }

    private List<DataUser> parseUsers(List<SQLDataUser> sqlDataUsers) {
        List<DataUser> dataUsers = new ArrayList<>();
        for(SQLDataUser sqlDataUser : sqlDataUsers) {
            dataUsers.add(DataUser.parse(sqlDataUser));
        }
        return dataUsers;
    }

    public List<DataUser> getUsersFrom(String field, String value) {
        String query = "SELECT * FROM " + table + " WHERE " + field + " = :" + field;

        try(Connection con = sql2o.open()) {
            return parseUsers(con.createQuery(query)
                    .addParameter(field, value)
                    .executeAndFetch(SQLDataUser.class));
        }
    }

    private Map<String, String> getFieldMap(DataUser dataUser) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("names", StringFormatter.arrayToString(dataUser.getNames().toArray(new String[0])));
        hashMap.put("uuid", dataUser.getUuid());
        hashMap.put("lastOnlineTag", dataUser.getLastOnlineTag());
        hashMap.put("avatarUrl", dataUser.getAvatarUrl());

        return hashMap;
    }

    Map<String, String> getFieldMap(SQLDataUser dataUser) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("names", dataUser.getNames());
        hashMap.put("uuid", dataUser.getUuid());
        hashMap.put("lastOnlineTag", dataUser.getLastOnlineTag());
        hashMap.put("avatarUrl", dataUser.getAvatarUrl());

        return hashMap;
    }
}
