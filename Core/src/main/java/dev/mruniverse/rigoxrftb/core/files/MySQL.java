package dev.mruniverse.rigoxrftb.core.files;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {
    private final RigoxRFTB plugin;
    public MySQL(RigoxRFTB main) {
        plugin = main;
    }
    public Connection con;

    public void connect(String host, String db, String user, String password) {
        try {
            String url= plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.jdbc-url");
            int port = plugin.getStorage().getControl(RigoxFiles.MYSQL).getInt("mysql.port");
            if(url == null) url = "jdbc:mysql://" + host + ":" + plugin.getStorage().getControl(RigoxFiles.MYSQL).getInt("mysql.port") + "/" + db + "?autoReconnect=true";
            url = url.replace("[host]",host)
            .replace("[port]",port + "")
            .replace("[db]",db);
            con = DriverManager.getConnection(url,user,password);
            plugin.getLogs().info("Connected with MySQL! creating tables");
            List<String> integers = new ArrayList<>();
            integers.add("Kills");
            integers.add("Deaths");
            integers.add("Score");
            integers.add("Wins");
            integers.add("Coins");
            integers.add("LevelXP");
            List<String> strings = new ArrayList<>();
            strings.add("Player");
            integers.add("Rank");
            plugin.getData().createMultiTable(plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table"), integers, strings);
            plugin.getLogs().info("Tables created!");
        } catch (SQLException e) {
            plugin.getLogs().error("Plugin can't connect to MySQL or cant initialize tables.");
            plugin.getLogs().error(e);
            plugin.getLogs().error("Using SQL instead MySQL.");
            plugin.getLogs().error("-------------------------");
            plugin.getData().getSQL().loadData();
        }
    }

    public void close() {
        if (con != null)
            try {
                con.close();
            } catch (SQLException e) {
                plugin.getLogs().error("Can't close mysql connection!");
                plugin.getLogs().error(e);
            }
    }

    public void Update(String qry) {
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(qry);
        } catch (SQLException e) {
            plugin.getLogs().error("Can't update query(s)!");
            plugin.getLogs().error(e);
        }
    }

    public ResultSet Query(String qry) {
        ResultSet rs = null;
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(qry);
        } catch (SQLException e) {
            plugin.getLogs().error("Can't execute query(s)!");
            plugin.getLogs().error(e);
        }
        return rs;
    }
}
