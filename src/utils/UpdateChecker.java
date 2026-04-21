package utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class UpdateChecker {
    private static final String VERSION_ACTUELLE = "1.1.0";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/rockdavies820-dev/GestionBibliotheque/main/version.txt";

    public static void verifier() {
        new Thread(() -> {
            try {
                URL url = new URL(VERSION_URL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
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
                    info.setContentText("Veuillez patienter.");
                    info.show();
                });

                // URL dynamique basee sur la version recue
                String downloadUrl = "https://github.com/rockdavies820-dev/GestionBibliotheque/releases/latest/download/GestionBibliotheque-" + version + ".exe";
                String tempPath = System.getProperty("java.io.tmpdir") + "GestionBibliotheque_update.exe";

                URL url = new URL(downloadUrl);
                try (InputStream in = url.openStream()) {
                    Files.copy(in, Paths.get(tempPath), StandardCopyOption.REPLACE_EXISTING);
                }

                // Lancer l'installateur et fermer l'app
                Runtime.getRuntime().exec(tempPath);
                Platform.exit();

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert erreur = new Alert(Alert.AlertType.ERROR);
                    erreur.setTitle("Erreur");
                    erreur.setContentText("Telechargement echoue : " + e.getMessage());
                    erreur.show();
                });
            }
        }).start();
    }
}