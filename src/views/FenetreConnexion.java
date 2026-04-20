package views;
import database.ConnexionDB;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FenetreConnexion {

    public void afficher(Stage stage) {
        afficherAccueil(stage);
    }

    // PAGE 1 — Accueil
    private void afficherAccueil(Stage stage) {
        stage.setTitle("Gestion Bibliotheque");

        Text titre = new Text("Gestion Bibliotheque");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setFill(Color.DARKBLUE);

        Text sousTitre = new Text("Systeme de gestion de bibliotheque scolaire");
        sousTitre.setFont(Font.font("Arial", 14));
        sousTitre.setFill(Color.GRAY);

        Button btnInscrire = new Button("S'inscrire");
        btnInscrire.setStyle("-fx-background-color: darkblue; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        btnInscrire.setPrefWidth(200);

        Button btnConnecter = new Button("Se connecter");
        btnConnecter.setStyle("-fx-background-color: white; -fx-text-fill: darkblue; -fx-font-size: 14px; -fx-padding: 10 30; -fx-border-color: darkblue; -fx-border-width: 2;");
        btnConnecter.setPrefWidth(200);

        btnInscrire.setOnAction(e -> afficherInscription(stage));
        btnConnecter.setOnAction(e -> afficherConnexion(stage));

        VBox vbox = new VBox(20, titre, sousTitre, btnInscrire, btnConnecter);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(50));

        Scene scene = new Scene(vbox, 450, 350);
        stage.setScene(scene);
        stage.show();
    }

    // PAGE 2 — Inscription
    private void afficherInscription(Stage stage) {
        stage.setTitle("Inscription - Gestion Bibliotheque");

        Text titre = new Text("Inscription");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.DARKBLUE);

        Text sousTitre = new Text("Creez les comptes de votre etablissement");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setFill(Color.GRAY);

        // Choix etablissement
        ToggleGroup groupEtab = new ToggleGroup();
        RadioButton rbEcole = new RadioButton("Ecole");
        RadioButton rbAutre = new RadioButton("Autre etablissement");
        rbEcole.setToggleGroup(groupEtab);
        rbAutre.setToggleGroup(groupEtab);
        HBox hboxEtab = new HBox(20, rbEcole, rbAutre);
        hboxEtab.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label lblNom = new Label("Nom etablissement :");
        TextField txtNom = new TextField();
        txtNom.setPromptText("ex: Lycee Victor Hugo");
        txtNom.setPrefWidth(200);

        Label lblAdmin = new Label("Mot de passe Admin :");
        PasswordField txtAdmin = new PasswordField();
        txtAdmin.setPromptText("Choisir un mot de passe");
        txtAdmin.setPrefWidth(200);

        Label lblBiblio = new Label("Mot de passe Bibliothecaire :");
        PasswordField txtBiblio = new PasswordField();
        txtBiblio.setPromptText("Choisir un mot de passe");
        txtBiblio.setPrefWidth(200);

        Label lblMessage = new Label();
        lblMessage.setTextFill(Color.RED);

        Button btnInscrire = new Button("S'inscrire");
        btnInscrire.setStyle("-fx-background-color: darkblue; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20;");

        Button btnRetour = new Button("Retour");
        btnRetour.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        btnRetour.setOnAction(e -> afficherAccueil(stage));

        grid.add(lblNom, 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(lblAdmin, 0, 1);
        grid.add(txtAdmin, 1, 1);
        grid.add(lblBiblio, 0, 2);
        grid.add(txtBiblio, 1, 2);
        grid.add(btnInscrire, 1, 3);
        grid.add(lblMessage, 1, 4);

        btnInscrire.setOnAction(e -> {
            if (groupEtab.getSelectedToggle() == null) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Veuillez choisir un type d'etablissement !");
                return;
            }
            String nom = txtNom.getText().trim();
            String type = rbEcole.isSelected() ? "Ecole" : "Autre";
            String passAdmin = txtAdmin.getText().trim();
            String passBiblio = txtBiblio.getText().trim();

            if (nom.isEmpty() || passAdmin.isEmpty() || passBiblio.isEmpty()) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Veuillez remplir tous les champs !");
                return;
            }

            try {
                int etabId = ConnexionDB.inscrireEtablissement(nom, type, passAdmin, passBiblio);
                lblMessage.setTextFill(Color.GREEN);
                lblMessage.setText("Inscription reussie ! ID: " + etabId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Inscription reussie !");
                alert.setHeaderText("Vos identifiants de connexion :");
                alert.setContentText(
                        "Login Admin : admin_" + etabId + "\n" +
                                "Mot de passe Admin : " + passAdmin + "\n\n" +
                                "Login Bibliothecaire : biblio_" + etabId + "\n" +
                                "Mot de passe Bibliothecaire : " + passBiblio + "\n\n" +
                                "Notez bien ces identifiants !"
                );
                alert.showAndWait();
                afficherConnexion(stage);

            } catch (Exception ex) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Erreur : " + ex.getMessage());
            }
        });

        VBox vbox = new VBox(15, titre, sousTitre, hboxEtab, grid, btnRetour);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));

        Scene scene = new Scene(vbox, 500, 480);
        stage.setScene(scene);
        stage.show();
    }

    // PAGE 3 — Connexion
    private void afficherConnexion(Stage stage) {
        stage.setTitle("Connexion - Gestion Bibliotheque");

        Text titre = new Text("Connexion");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.DARKBLUE);

        Text sousTitre = new Text("Entrez vos identifiants");
        sousTitre.setFont(Font.font("Arial", 13));
        sousTitre.setFill(Color.GRAY);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label lblUser = new Label("Login :");
        TextField txtUser = new TextField();
        txtUser.setPromptText("ex: admin_1");
        txtUser.setPrefWidth(200);

        Label lblPass = new Label("Mot de passe :");
        PasswordField txtPass = new PasswordField();
        txtPass.setPrefWidth(200);

        Label lblMessage = new Label();
        lblMessage.setTextFill(Color.RED);

        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setStyle("-fx-background-color: darkblue; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20;");

        Button btnRetour = new Button("Retour");
        btnRetour.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        btnRetour.setOnAction(e -> afficherAccueil(stage));

        grid.add(lblUser, 0, 0);
        grid.add(txtUser, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(txtPass, 1, 1);
        grid.add(btnConnexion, 1, 2);
        grid.add(lblMessage, 1, 3);

        btnConnexion.setOnAction(e -> {
            String login = txtUser.getText().trim();
            String pass = txtPass.getText().trim();

            if (login.isEmpty() || pass.isEmpty()) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Veuillez remplir tous les champs !");
                return;
            }

            try {
                String role = ConnexionDB.verifierUtilisateur(login, pass);
                if (role != null) {
                    lblMessage.setTextFill(Color.GREEN);
                    lblMessage.setText("Connexion reussie !");
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

        VBox vbox = new VBox(15, titre, sousTitre, grid, btnRetour);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));

        Scene scene = new Scene(vbox, 400, 350);
        stage.setScene(scene);
        stage.show();
    }
}