package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnexionDB {

    private static Connection connection = null;

    private static final String URL  = "jdbc:postgresql://aws-0-eu-west-1.pooler.supabase.com:5432/postgres";
    private static final String USER = "postgres.pobuvgjcebwqfquoietr";
    private static final String PASSWORD = "K93VsRCc58fNeTBT";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    // Garde la compatibilité avec l'ancien code
    public static Connection getConnection(String user, String password) throws SQLException {
        return getConnection();
    }

    public static String verifierUtilisateur(String login, String password) throws SQLException {
        String sql = "SELECT role FROM utilisateur WHERE login = ? AND password = ?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, login);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("role");
        }
        return null; // login ou mot de passe incorrect
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