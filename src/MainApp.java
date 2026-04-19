import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import views.FenetreConnexion;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Icône de l'application
        try {
            Image icon = new Image("file:C:/Users/rockd/IdeaProjects/GestionBibliotheque/GestionBibliotheque/src/icon.png");
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Icône non chargée : " + e.getMessage());
        }

        // Titre de la fenêtre
        primaryStage.setTitle("Gestion Bibliothèque");

        FenetreConnexion connexion = new FenetreConnexion();
        connexion.afficher(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}