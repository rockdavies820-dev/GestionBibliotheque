import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import views.FenetreConnexion;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Icone depuis les ressources (PNG requis par JavaFX)
        try {
            Image icon = new Image(MainApp.class.getResourceAsStream("/favicon.png"));
            if (icon != null && !icon.isError()) {
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.out.println("Icone non chargee : " + e.getMessage());
        }

        primaryStage.setTitle("Gestion Bibliotheque");

        // Taille minimale fixe — empeche le retrecissement
        primaryStage.setMinWidth(450);
        primaryStage.setMinHeight(350);

        FenetreConnexion connexion = new FenetreConnexion();
        connexion.afficher(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}