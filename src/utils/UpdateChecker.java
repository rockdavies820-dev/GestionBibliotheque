package utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class UpdateChecker {
    private static final String VERSION_ACTUELLE = "1.3.0";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/rockdavies820-dev/GestionBibliotheque/main/version.txt";

    public static void verifier() {
        new Thread(() -> {
            try {
                URL url = new URL(VERSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String versionEnLigne = reader.readLine().trim();
                reader.close();
                if (!versionEnLigne.equals(VERSION_ACTUELLE)) {
                    Platform.runLater(() -> afficherPopup(versionEnLigne));
                }
            } catch (Exception e) {
                System.out.println("Verification mise a jour echouee : " + e.getMessage());
            }
        }).start();
    }

    private static void afficherPopup(String nouvelleVersion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mise a jour disponible");
        alert.setHeaderText("Version " + nouvelleVersion + " disponible !");
        alert.setContentText("Voulez-vous mettre a jour maintenant ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                telechargerEtInstaller(nouvelleVersion);
            }
        });
    }

    private static void telechargerEtInstaller(String version) {
        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Telechargement");
                    info.setHeaderText("Telechargement en cours...");
                    info.setContentText("Veuillez patienter, ne fermez pas l'application.");
                    info.show();
                });

                String downloadUrl = "https://github.com/rockdavies820-dev/GestionBibliotheque/releases/download/v"
                        + version + "/GestionBibliotheque-" + version + ".exe";

                String tempPath = System.getenv("TEMP") + "\\GestionBibliotheque_update.exe";

                // Telecharger via Java natif avec suivi des redirections
                HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(120000);

                // Suivre manuellement les redirections (GitHub en a plusieurs)
                int status = conn.getResponseCode();
                while (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == 303) {
                    String newUrl = conn.getHeaderField("Location");
                    conn.disconnect();
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(120000);
                    status = conn.getResponseCode();
                }

                if (status != 200) {
                    throw new Exception("HTTP " + status + " pour " + downloadUrl);
                }

                // Ecrire le fichier
                try (InputStream in = conn.getInputStream()) {
                    Files.copy(in, Paths.get(tempPath), StandardCopyOption.REPLACE_EXISTING);
                }
                conn.disconnect();

                File fichier = new File(tempPath);
                if (!fichier.exists() || fichier.length() < 100000) {
                    throw new Exception("Fichier invalide : " + fichier.length() + " bytes");
                }

                // Lancer l'installateur et fermer l'app
                new ProcessBuilder(tempPath).start();
                Thread.sleep(2000);
                Platform.exit();

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert erreur = new Alert(Alert.AlertType.ERROR);
                    erreur.setTitle("Erreur mise a jour");
                    erreur.setContentText(e.getMessage());
                    erreur.show();
                });
            }
        }).start();
    }
}