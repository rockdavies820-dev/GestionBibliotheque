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

public class FenetreEtudiant {

    private TableView<ObservableList<String>> table = new TableView<>();

    public void afficher(Stage stage, String role) {
        stage.setTitle("Gestion des Étudiants");

        Text titre = new Text("Gestion des Étudiants");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtMatricule = new TextField(); txtMatricule.setPromptText("Matricule");
        TextField txtNom = new TextField(); txtNom.setPromptText("Nom");
        TextField txtPrenoms = new TextField(); txtPrenoms.setPromptText("Prénoms");
        TextField txtSexe = new TextField(); txtSexe.setPromptText("Sexe (M/F)");
        TextField txtClasse = new TextField(); txtClasse.setPromptText("Code Classe");

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRetour = new Button("Retour");
        Label lblMessage = new Label();

        btnAjouter.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        btnModifier.setStyle("-fx-background-color: darkorange; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        HBox formulaire = new HBox(10, txtMatricule, txtNom, txtPrenoms, txtSexe, txtClasse, btnAjouter, btnModifier, btnSupprimer);
        formulaire.setAlignment(Pos.CENTER);

        chargerDonnees();

        table.setOnMouseClicked(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtMatricule.setText(selected.get(0));
                txtNom.setText(selected.get(1));
                txtPrenoms.setText(selected.get(2));
                txtSexe.setText(selected.get(3));
                txtClasse.setText(selected.get(4));
                txtMatricule.setDisable(true);
            }
        });

        btnAjouter.setOnAction(e -> {
            txtMatricule.setDisable(false);
            try {
                Connection conn = ConnexionDB.getConnection();
                String sql = "INSERT INTO etudiant (matricule, nom, prenoms, sexe, code_cl, etablissement_id) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtMatricule.getText());
                ps.setString(2, txtNom.getText());
                ps.setString(3, txtPrenoms.getText());
                ps.setString(4, txtSexe.getText());
                ps.setString(5, txtClasse.getText());
                ps.setInt(6, ConnexionDB.getEtablissementId());
                ps.executeUpdate();
                lblMessage.setText("Étudiant ajouté !");
                viderFormulaire(txtMatricule, txtNom, txtPrenoms, txtSexe, txtClasse);
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
                alert.setHeaderText("Modifier l'étudiant");
                alert.setContentText("Voulez-vous vraiment modifier " + selected.get(1) + " ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection();
                            String sql = "UPDATE etudiant SET nom=?, prenoms=?, sexe=?, code_cl=? WHERE matricule=? AND etablissement_id=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, txtNom.getText());
                            ps.setString(2, txtPrenoms.getText());
                            ps.setString(3, txtSexe.getText());
                            ps.setString(4, txtClasse.getText());
                            ps.setString(5, txtMatricule.getText());
                            ps.setInt(6, ConnexionDB.getEtablissementId());
                            ps.executeUpdate();
                            lblMessage.setText("Étudiant modifié !");
                            txtMatricule.setDisable(false);
                            viderFormulaire(txtMatricule, txtNom, txtPrenoms, txtSexe, txtClasse);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez un étudiant d'abord !");
            }
        });

        btnSupprimer.setOnAction(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Supprimer l'étudiant");
                alert.setContentText("Voulez-vous vraiment supprimer " + selected.get(1) + " ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection();
                            String sql = "DELETE FROM etudiant WHERE matricule=? AND etablissement_id=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, selected.get(0));
                            ps.setInt(2, ConnexionDB.getEtablissementId());
                            ps.executeUpdate();
                            lblMessage.setText("Étudiant supprimé !");
                            txtMatricule.setDisable(false);
                            viderFormulaire(txtMatricule, txtNom, txtPrenoms, txtSexe, txtClasse);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez un étudiant d'abord !");
            }
        });

        btnRetour.setOnAction(e -> {
            txtMatricule.setDisable(false);
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
            String sql = "SELECT matricule, nom, prenoms, sexe, code_cl FROM etudiant WHERE etablissement_id=?";
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