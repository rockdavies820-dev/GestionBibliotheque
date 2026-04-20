import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import views.FenetreConnexion;
import utils.UpdateChecker;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Vérifier les mises à jour
        UpdateChecker.verifier();

        try {
            Image icon = new Image("file:C:/Users/rockd/IdeaProjects/GestionBibliotheque/GestionBibliotheque/src/icon.png");
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Icône non chargée : " + e.getMessage());
        }
        primaryStage.setTitle("Gestion Bibliothèque");
        FenetreConnexion connexion = new FenetreConnexion();
        connexion.afficher(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}