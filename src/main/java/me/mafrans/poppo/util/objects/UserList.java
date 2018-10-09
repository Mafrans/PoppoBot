package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserList {
    Sql2o sql2o;
    List<DataUser> cache;

    private final String table = ConfigEntry.DATABASE_TABLE.getString();

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
        return remove(new SQLDataUser(getByUuid(uuid).get(0)));
    }

    public boolean add(SQLDataUser dataUser) {
        if(getByUuid(dataUser.getUuid()).size() != 0) {
            if(!removeByUuid(dataUser.getUuid())) return false;
        }

        String query = "INSERT INTO " + table + "(names, uuid, lastOnlineTag, avatarUrl) values(:names, :uuid, :lastOnlineTag, :avatarUrl)";

        try (Connection con = sql2o.open()) {
            con.createQuery(query)
                    .addParameter("names", dataUser.getNames())
                    .addParameter("uuid", dataUser.getUuid())
                    .addParameter("lastOnlineTag", dataUser.getLastOnlineTag())
                    .addParameter("avatarUrl", dataUser.getAvatarUrl())
                    .executeUpdate();

            updateCache();
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(SQLDataUser dataUser) {
        String query = "DELETE FROM " + table + " WHERE names = :names AND uuid = :uuid AND lastOnlineTag = :lastOnlineTag AND avatarUrl = :avatarUrl";

        try (Connection con = sql2o.open()) {
            con.createQuery(query)
                    .addParameter("names", dataUser.getNames())
                    .addParameter("uuid", dataUser.getUuid())
                    .addParameter("lastOnlineTag", dataUser.getLastOnlineTag())
                    .addParameter("avatarUrl", dataUser.getAvatarUrl())
                    .executeUpdate();

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

    public List<DataUser> getByUuid(String uuid) {
        String query = "SELECT * FROM " + table + " WHERE uuid = :uuid";

        try(Connection con = sql2o.open()) {
            return parseUsers(con.createQuery(query)
                    .addParameter("uuid", uuid)
                    .executeAndFetch(SQLDataUser.class));
        }
    }

    public List<DataUser> getOnline() {
        String query = "SELECT * FROM " + table + " WHERE lastOnlineTag = :lastOnlineTag";

        try(Connection con = sql2o.open()) {
            return parseUsers(con.createQuery(query)
                    .addParameter("lastOnlineTag", "Currently Online")
                    .executeAndFetch(SQLDataUser.class));
        }
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
}
