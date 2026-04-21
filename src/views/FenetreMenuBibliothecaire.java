package views;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FenetreMenuBibliothecaire {
    public void afficher(Stage stage) {
        stage.setTitle("Menu Bibliothecaire - Gestion Bibliotheque");

        Text titre = new Text("Menu Bibliothecaire");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.DARKBLUE);

        Text role = new Text("Connecte en tant que : Bibliothecaire");
        role.setFont(Font.font("Arial", 12));
        role.setFill(Color.GRAY);

        Button btnEmprunt  = new Button("Gerer les Emprunts");
        Button btnRequetes = new Button("Faire des Requetes");
        Button btnQuitter  = new Button("Quitter");

        String style = "-fx-min-width: 220px; -fx-background-color: darkblue; -fx-text-fill: white;";
        btnEmprunt.setStyle(style);
        btnRequetes.setStyle(style);
        btnQuitter.setStyle("-fx-min-width: 220px; -fx-background-color: red; -fx-text-fill: white;");

        btnEmprunt.setOnAction(e  -> new FenetreEmprunt().afficher(stage, "bibliothecaire"));
        btnRequetes.setOnAction(e -> new FenetreRequetes().afficher(stage, "bibliothecaire"));
        btnQuitter.setOnAction(e  -> stage.close());

        VBox vbox = new VBox(15, titre, role, btnEmprunt, btnRequetes, btnQuitter);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40));

        Scene scene = new Scene(vbox, 350, 320);
        stage.setScene(scene);
        stage.show();
    }
}