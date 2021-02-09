package dev.mruniverse.rigoxrftb.core.files;

import java.sql.ResultSet;
import java.util.List;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;

@SuppressWarnings("unused")
public class DataStorage {
    private final RigoxRFTB plugin;
    private final MySQL mySQL;
    private final SQL sql;
    public DataStorage(RigoxRFTB main) {
        plugin = main;
        mySQL = new MySQL(main);
        sql = new SQL(main);
    }

    public void createMultiTable(String tableName, List<String> intLists, List<String> sLists) {
        String intList = "";
        for (String text : intLists)
            intList = intList + ", " + text + " INT(255)";
        String vList = "";
        for (String string : sLists)
            vList = vList + ", " + string + " VARCHAR(255)";
        mySQL.Update("CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY" + vList + intList + ")");
    }

    public void setInt(String tableName, String column, String where, String what, int integer) {
        mySQL.Update("UPDATE " + tableName + " SET " + column + "= '" + integer + "' WHERE " + where + "= '" + what + "';");
    }

    public void setString(String tableName, String path, String where, String what, String integer) {
        mySQL.Update("UPDATE " + tableName + " SET " + path + "= '" + integer + "' WHERE " + where + "= '" + what + "';");
    }

    public String getString(String tableName, String column, String where, String what) {
        String string = "";
        try {
            ResultSet rs = mySQL.Query("SELECT " + column + " FROM " + tableName + " WHERE " + where + "= '" + what + "';");
            while (rs.next())
                string = rs.getString(1);
            rs.close();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get a string from the database.");
            plugin.getLogs().error(throwable);
        }
        return string;
    }

    public Integer getInt(String tableName, String column, String where, String what) {
        int integer = 0;
        try {
            ResultSet rs = mySQL.Query("SELECT " + column + " FROM " + tableName + " WHERE " + where + "= '" + what + "';");
            while (rs.next())
                integer = rs.getInt(1);
            rs.close();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get an integer from the database.");
            plugin.getLogs().error(throwable);
        }
        return integer;
    }

    public boolean isRegistered(String tableName, String where, String what) {
        boolean is = false;
        try {
            ResultSet rs = mySQL.Query("SELECT id FROM " + tableName + " WHERE " + where + "= '" + what + "';");
            while (rs.next())
                is = true;
            rs.close();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't check internal data in database.");
            plugin.getLogs().error(throwable);
        }
        return is;
    }

    public void register(String tableName, List<String> values) {
        String names = "";
        String names2 = "";
        for (String value : values) {
            String[] valSplit = value.split("-");
            names = names + valSplit[0] + ", ";
            names2 = names2 + "'" + valSplit[1] + "', ";
        }
        names = names.substring(0, names.length() - 2);
        names2 = names2.substring(0, names2.length() - 2);
        mySQL.Update("INSERT INTO " + tableName + " (" + names + ") VALUES (" + names2 + ")");
    }

    public void loadDatabase() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            mySQL.connect(plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.host"),plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.database"),plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.username"),plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.password"));
        } else {
            sql.loadData();
            plugin.getLogs().info("MySQL is disabled, using data.yml");
        }
    }

    public void disableDatabase() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            mySQL.close();
        } else {
            sql.putData();
        }
    }
    public MySQL getMySQL() {
        return mySQL;
    }
    public SQL getSQL() {
        return sql;
    }
}
