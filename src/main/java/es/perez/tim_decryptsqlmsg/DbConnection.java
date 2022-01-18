package es.perez.tim_decryptsqlmsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    public static Connection getConnection(String fpath) throws SQLException {
        if ((fpath = fpath.toLowerCase()).indexOf("\\") != -1)
            fpath = fpath.replaceAll("\\\\", "/");
        String result = "";
        if (fpath.indexOf("/") >= 0 || fpath.indexOf("\\") >= 0) {
            result = String.valueOf("jdbc:sqlite://") + fpath;
        } else {
            result = String.valueOf("jdbc:sqlite:") + fpath;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(result);
        } catch (ClassNotFoundException cne) {
            cne.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection cnn) {
        if(cnn != null) {
            try {
                cnn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

}
