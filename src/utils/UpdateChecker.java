package utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.*;
import java.net.*;

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

                String downloadUrl = "https://github.com/rockdavies820-dev/GestionBibliotheque/releases/download/v"
                        + version + "/GestionBibliotheque-" + version + ".exe";

                // Utiliser le dossier TEMP de Windows — toujours accessible
                String tempPath = System.getenv("TEMP") + "\\GestionBibliotheque_update.exe";

                ProcessBuilder pb = new ProcessBuilder(
                        "C:\\Windows\\System32\\curl.exe",
                        "-L",
                        "-A", "Mozilla/5.0",
                        "-o", tempPath,
                        downloadUrl
                );
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) output.append(line).append("\n");

                int exitCode = process.waitFor();
                File fichier = new File(tempPath);

                if (exitCode != 0 || !fichier.exists() || fichier.length() < 1000) {
                    throw new Exception("Echec telechargement (code " + exitCode + ")\n"
                            + "Taille: " + (fichier.exists() ? fichier.length() : 0) + " bytes\n"
                            + output.toString().substring(0, Math.min(output.length(), 300)));
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