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

public class FenetreMenuAdmin {

    public void afficher(Stage stage) {
        stage.setTitle("Menu Admin - Gestion Bibliothèque");

        Text titre = new Text("Menu Administrateur");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.DARKBLUE);

        Text role = new Text("Connecté en tant que : Admin");
        role.setFont(Font.font("Arial", 12));
        role.setFill(Color.GRAY);

        Button btnClasse   = new Button("Gérer les Classes");
        Button btnEtudiant = new Button("Gérer les Étudiants");
        Button btnLivre    = new Button("Gérer les Livres");
        Button btnEmprunt  = new Button("Gérer les Emprunts");
        Button btnRequetes = new Button("Faire des Requêtes");
        Button btnQuitter  = new Button("Quitter");

        String style = "-fx-min-width: 220px; -fx-background-color: darkblue; -fx-text-fill: white;";
        btnClasse.setStyle(style);
        btnEtudiant.setStyle(style);
        btnLivre.setStyle(style);
        btnEmprunt.setStyle(style);
        btnRequetes.setStyle(style);
        btnQuitter.setStyle("-fx-min-width: 220px; -fx-background-color: red; -fx-text-fill: white;");

        btnClasse.setOnAction(e   -> new FenetreClasse().afficher(stage, "admin"));
        btnEtudiant.setOnAction(e -> new FenetreEtudiant().afficher(stage, "admin"));
        btnLivre.setOnAction(e    -> new FenetreLivre().afficher(stage, "admin"));
        btnEmprunt.setOnAction(e  -> new FenetreEmprunt().afficher(stage, "admin"));
        btnRequetes.setOnAction(e -> new FenetreRequetes().afficher(stage, "admin"));
        btnQuitter.setOnAction(e  -> stage.close());

        VBox vbox = new VBox(15, titre, role, btnClasse, btnEtudiant, btnLivre, btnEmprunt, btnRequetes, btnQuitter);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40));

        Scene scene = new Scene(vbox, 350, 480);
        stage.setScene(scene);
        stage.show();
    }
}