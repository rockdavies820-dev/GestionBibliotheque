package views;

import database.ConnexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class FenetreLivre {

    private TableView<ObservableList<String>> table = new TableView<>();

    public void afficher(Stage stage, String role) {
        stage.setTitle("Gestion des Livres");

        Text titre = new Text("Gestion des Livres");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtCode = new TextField(); txtCode.setPromptText("Code Livre");
        TextField txtTitre = new TextField(); txtTitre.setPromptText("Titre");
        TextField txtAuteur = new TextField(); txtAuteur.setPromptText("Auteur");
        TextField txtGenre = new TextField(); txtGenre.setPromptText("Genre");
        TextField txtPrix = new TextField(); txtPrix.setPromptText("Prix");

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRetour = new Button("Retour");
        Label lblMessage = new Label();

        btnAjouter.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        btnModifier.setStyle("-fx-background-color: darkorange; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        HBox formulaire = new HBox(10, txtCode, txtTitre, txtAuteur, txtGenre, txtPrix, btnAjouter, btnModifier, btnSupprimer);
        formulaire.setAlignment(Pos.CENTER);

        chargerDonnees();

        table.setOnMouseClicked(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtCode.setText(selected.get(0));
                txtTitre.setText(selected.get(1));
                txtAuteur.setText(selected.get(2));
                txtGenre.setText(selected.get(3));
                txtPrix.setText(selected.get(4));
                txtCode.setDisable(true);
            }
        });

        btnAjouter.setOnAction(e -> {
            txtCode.setDisable(false);
            try {
                Connection conn = ConnexionDB.getConnection();
                String sql = "INSERT INTO livre (code_liv, titre, auteur, genre, prix, etablissement_id) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtCode.getText());
                ps.setString(2, txtTitre.getText());
                ps.setString(3, txtAuteur.getText());
                ps.setString(4, txtGenre.getText());
                ps.setDouble(5, Double.parseDouble(txtPrix.getText()));
                ps.setInt(6, ConnexionDB.getEtablissementId());
                ps.executeUpdate();
                lblMessage.setText("Livre ajouté !");
                viderFormulaire(txtCode, txtTitre, txtAuteur, txtGenre, txtPrix);
                chargerDonnees();
            } catch (Exception ex) {
                lblMessage.setText("Erreur : " + ex.getMessage());
            }
        });

        btnModifier.setOnAction(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Modifier le livre");
                alert.setContentText("Voulez-vous vraiment modifier " + selected.get(1) + " ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection();
                            String sql = "UPDATE livre SET titre=?, auteur=?, genre=?, prix=? WHERE code_liv=? AND etablissement_id=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, txtTitre.getText());
                            ps.setString(2, txtAuteur.getText());
                            ps.setString(3, txtGenre.getText());
                            ps.setDouble(4, Double.parseDouble(txtPrix.getText()));
                            ps.setString(5, txtCode.getText());
                            ps.setInt(6, ConnexionDB.getEtablissementId());
                            ps.executeUpdate();
                            lblMessage.setText("Livre modifié !");
                            txtCode.setDisable(false);
                            viderFormulaire(txtCode, txtTitre, txtAuteur, txtGenre, txtPrix);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez un livre d'abord !");
            }
        });

        btnSupprimer.setOnAction(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Supprimer le livre");
                alert.setContentText("Voulez-vous vraiment supprimer " + selected.get(1) + " ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection();
                            String sql = "DELETE FROM livre WHERE code_liv=? AND etablissement_id=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, selected.get(0));
                            ps.setInt(2, ConnexionDB.getEtablissementId());
                            ps.executeUpdate();
                            lblMessage.setText("Livre supprimé !");
                            txtCode.setDisable(false);
                            viderFormulaire(txtCode, txtTitre, txtAuteur, txtGenre, txtPrix);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez un livre d'abord !");
            }
        });

        btnRetour.setOnAction(e -> {
            txtCode.setDisable(false);
            if (role.equals("admin")) {
                new FenetreMenuAdmin().afficher(stage);
            } else {
                new FenetreMenuBibliothecaire().afficher(stage);
            }
        });

        VBox vbox = new VBox(15, titre, formulaire, lblMessage, table, btnRetour);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 900, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void viderFormulaire(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    private void chargerDonnees() {
        table.getColumns().clear();
        table.getItems().clear();
        try {
            Connection conn = ConnexionDB.getConnection();
            String sql = "SELECT code_liv, titre, auteur, genre, prix FROM livre WHERE etablissement_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ConnexionDB.getEtablissementId());
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                final int idx = i - 1;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(meta.getColumnName(i));
                col.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(idx)));
                table.getColumns().add(col);
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= cols; i++) row.add(rs.getString(i));
                table.getItems().add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}