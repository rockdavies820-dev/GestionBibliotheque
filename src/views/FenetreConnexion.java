package views;

import database.ConnexionDB;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FenetreConnexion {

    public void afficher(Stage stage) {
        stage.setTitle("Connexion - Gestion Bibliothèque");

        Text titre = new Text("Gestion Bibliothèque");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titre.setFill(Color.DARKBLUE);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label lblUser = new Label("Utilisateur :");
        TextField txtUser = new TextField();
        txtUser.setPromptText("ex: system");

        Label lblPass = new Label("Mot de passe :");
        PasswordField txtPass = new PasswordField();

        Label lblMessage = new Label();
        lblMessage.setTextFill(Color.RED);

        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");

        grid.add(lblUser, 0, 0);
        grid.add(txtUser, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(txtPass, 1, 1);
        grid.add(btnConnexion, 1, 2);
        grid.add(lblMessage, 1, 3);

        btnConnexion.setOnAction(e -> {
            String user = txtUser.getText();
            String pass = txtPass.getText();
            try {
                ConnexionDB.getConnection(user, pass);
                lblMessage.setTextFill(Color.GREEN);
                lblMessage.setText("Connexion réussie !");
                // Ouvrir le menu principal
                FenetreMenu menu = new FenetreMenu();
                menu.afficher(stage);
            } catch (Exception ex) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Erreur : " + ex.getMessage());
            }
        });

        VBox vbox = new VBox(20, titre, grid);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}