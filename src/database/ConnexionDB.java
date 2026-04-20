package database;
import java.sql.*;
import java.io.File;

public class ConnexionDB {
    private static Connection connection = null;
    private static int etablissementId = -1;
    private static String roleConnecte = null;

    private static String getDBPath() {
        String appData = System.getProperty("user.home") + File.separator + "GestionBibliotheque";
        new File(appData).mkdirs();
        return appData + File.separator + "bibliotheque.db";
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite non trouvé !", e);
            }
            String url = "jdbc:sqlite:" + getDBPath();
            connection = DriverManager.getConnection(url);
            initialiserBase();
        }
        return connection;
    }

    public static Connection getConnection(String user, String password) throws SQLException {
        return getConnection();
    }

    private static void initialiserBase() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS etablissement (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT NOT NULL," +
                "type TEXT NOT NULL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS utilisateur (" +
                "login TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "etablissement_id INTEGER," +
                "FOREIGN KEY (etablissement_id) REFERENCES etablissement(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS livre (" +
                "code_liv TEXT NOT NULL," +
                "titre TEXT," +
                "auteur TEXT," +
                "genre TEXT," +
                "prix REAL," +
                "etablissement_id INTEGER," +
                "FOREIGN KEY (etablissement_id) REFERENCES etablissement(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS etudiant (" +
                "matricule TEXT NOT NULL," +
                "nom TEXT," +
                "prenoms TEXT," +
                "sexe TEXT," +
                "code_cl TEXT," +
                "etablissement_id INTEGER," +
                "FOREIGN KEY (etablissement_id) REFERENCES etablissement(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS classe (" +
                "code_cl TEXT NOT NULL," +
                "intitule TEXT," +
                "effectif INTEGER," +
                "etablissement_id INTEGER," +
                "FOREIGN KEY (etablissement_id) REFERENCES etablissement(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS emprunt (" +
                "matricule TEXT NOT NULL," +
                "code_liv TEXT NOT NULL," +
                "sortie DATE," +
                "retour DATE," +
                "etablissement_id INTEGER," +
                "FOREIGN KEY (etablissement_id) REFERENCES etablissement(id))");

        stmt.close();
    }

    public static int getEtablissementId() {
        return etablissementId;
    }

    public static String getRoleConnecte() {
        return roleConnecte;
    }

    public static int inscrireEtablissement(String nom, String type, String passAdmin, String passBiblio) throws SQLException {
        String sqlEtab = "INSERT INTO etablissement (nom, type) VALUES (?, ?)";
        PreparedStatement stmtEtab = getConnection().prepareStatement(sqlEtab, Statement.RETURN_GENERATED_KEYS);
        stmtEtab.setString(1, nom);
        stmtEtab.setString(2, type);
        stmtEtab.executeUpdate();
        ResultSet rs = stmtEtab.getGeneratedKeys();
        rs.next();
        int etabId = rs.getInt(1);

        String sqlAdmin = "INSERT INTO utilisateur (login, password, role, etablissement_id) VALUES (?, ?, 'admin', ?)";
        PreparedStatement stmtAdmin = getConnection().prepareStatement(sqlAdmin);
        stmtAdmin.setString(1, "admin_" + etabId);
        stmtAdmin.setString(2, passAdmin);
        stmtAdmin.setInt(3, etabId);
        stmtAdmin.executeUpdate();

        String sqlBiblio = "INSERT INTO utilisateur (login, password, role, etablissement_id) VALUES (?, ?, 'bibliothecaire', ?)";
        PreparedStatement stmtBiblio = getConnection().prepareStatement(sqlBiblio);
        stmtBiblio.setString(1, "biblio_" + etabId);
        stmtBiblio.setString(2, passBiblio);
        stmtBiblio.setInt(3, etabId);
        stmtBiblio.executeUpdate();

        return etabId;
    }

    public static String verifierUtilisateur(String login, String password) throws SQLException {
        String sql = "SELECT role, etablissement_id FROM utilisateur WHERE login = ? AND password = ?";
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setString(1, login);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            roleConnecte = rs.getString("role");
            etablissementId = rs.getInt("etablissement_id");
            return roleConnecte;
        }
        return null;
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