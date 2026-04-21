package utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class UpdateChecker {
    private static final String VERSION_ACTUELLE = "1.5.0";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/rockdavies820-dev/GestionBibliotheque/main/version.txt";
    private static final String API_RELEASE_URL = "https://api.github.com/repos/rockdavies820-dev/GestionBibliotheque/releases/latest";

    public static void verifier() {
        new Thread(() -> {
            try {
                URL url = new URL(VERSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "GestionBibliotheque");
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

    private static String getDownloadUrl(String version) throws Exception {
        // Appel API GitHub pour obtenir l'URL directe du .exe
        URL url = new URL(API_RELEASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "GestionBibliotheque");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) json.append(line);
        reader.close();

        // Parser manuellement le JSON pour trouver browser_download_url du .exe
        String jsonStr = json.toString();
        String marker = "\"browser_download_url\":\"";
        int idx = jsonStr.indexOf(marker);
        while (idx != -1) {
            int start = idx + marker.length();
            int end = jsonStr.indexOf("\"", start);
            String dlUrl = jsonStr.substring(start, end);
            if (dlUrl.endsWith(".exe")) {
                return dlUrl;
            }
            idx = jsonStr.indexOf(marker, end);
        }
        throw new Exception("URL de telechargement introuvable dans la Release");
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

                // Obtenir l'URL directe via l'API GitHub
                String downloadUrl = getDownloadUrl(version);
                String tempPath = System.getenv("TEMP") + "\\GestionBibliotheque_update.exe";

                // Telecharger depuis l'URL directe (browser_download_url)
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", "GestionBibliotheque");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(120000);

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