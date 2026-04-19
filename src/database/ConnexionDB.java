package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {

    private static Connection connection = null;

    public static Connection getConnection(String user, String password) throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (user != null && password != null) {
                String url = "jdbc:oracle:thin:@localhost:1521/XE";
                connection = DriverManager.getConnection(url, user, password);
            }
        }
        return connection;
    }

    public static void fermer() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}