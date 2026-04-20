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

        Label lblUser = new Label("Login :");
        TextField txtUser = new TextField();
        txtUser.setPromptText("ex: admin");

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
            String login = txtUser.getText().trim();
            String pass  = txtPass.getText().trim();

            if (login.isEmpty() || pass.isEmpty()) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Veuillez remplir tous les champs !");
                return;
            }

            try {
                String role = ConnexionDB.verifierUtilisateur(login, pass);
                if (role != null) {
                    lblMessage.setTextFill(Color.GREEN);
                    lblMessage.setText("Connexion réussie !");
                    if (role.equals("admin")) {
                        new FenetreMenuAdmin().afficher(stage);
                    } else {
                        new FenetreMenuBibliothecaire().afficher(stage);
                    }
                } else {
                    lblMessage.setTextFill(Color.RED);
                    lblMessage.setText("Login ou mot de passe incorrect !");
                }
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