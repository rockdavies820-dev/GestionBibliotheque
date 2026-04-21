package utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class UpdateChecker {
    private static final String VERSION_ACTUELLE = "1.4.0";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/rockdavies820-dev/GestionBibliotheque/main/version.txt";

    public static void verifier() {
        new Thread(() -> {
            try {
                URL url = new URL(VERSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
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

                String tempPath = System.getenv("TEMP") + "\\GestionBibliotheque_update.exe";
                String downloadUrl = "https://github.com/rockdavies820-dev/GestionBibliotheque/releases/download/v"
                        + version + "/GestionBibliotheque-" + version + ".exe";

                // Suivre toutes les redirections manuellement avec headers complets
                String currentUrl = downloadUrl;
                HttpURLConnection conn = null;
                int maxRedirects = 10;

                for (int i = 0; i < maxRedirects; i++) {
                    conn = (HttpURLConnection) new URL(currentUrl).openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                    conn.setRequestProperty("Accept", "application/octet-stream,*/*");
                    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(120000);

                    int status = conn.getResponseCode();

                    if (status == 200) {
                        break; // On a le bon fichier
                    } else if (status == 301 || status == 302 || status == 303 || status == 307 || status == 308) {
                        currentUrl = conn.getHeaderField("Location");
                        conn.disconnect();
                    } else {
                        throw new Exception("HTTP " + status + " pour " + currentUrl);
                    }
                }

                if (conn == null) throw new Exception("Trop de redirections");

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