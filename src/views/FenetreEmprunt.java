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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FenetreEmprunt {

    private TableView<ObservableList<String>> table = new TableView<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void afficher(Stage stage, String role) {
        stage.setTitle("Gestion des Emprunts");

        Text titre = new Text("Gestion des Emprunts");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtMatricule = new TextField(); txtMatricule.setPromptText("Matricule");
        TextField txtCodeLiv = new TextField(); txtCodeLiv.setPromptText("Code Livre");
        TextField txtSortie = new TextField(); txtSortie.setPromptText("Sortie (DD/MM/YYYY)");
        TextField txtRetour = new TextField(); txtRetour.setPromptText("Retour (DD/MM/YYYY)");

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRetour = new Button("Retour");
        Label lblMessage = new Label();

        btnAjouter.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        btnModifier.setStyle("-fx-background-color: darkorange; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        HBox formulaire = new HBox(10, txtMatricule, txtCodeLiv, txtSortie, txtRetour, btnAjouter, btnModifier, btnSupprimer);
        formulaire.setAlignment(Pos.CENTER);

        chargerDonnees();

        table.setOnMouseClicked(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtMatricule.setText(selected.get(0));
                txtCodeLiv.setText(selected.get(1));
                txtSortie.setText(selected.get(2) != null ? selected.get(2) : "");
                txtRetour.setText(selected.get(3) != null ? selected.get(3) : "");
                txtMatricule.setDisable(true);
                txtCodeLiv.setDisable(true);
            }
        });

        btnAjouter.setOnAction(e -> {
            txtMatricule.setDisable(false);
            txtCodeLiv.setDisable(false);
            try {
                Connection conn = ConnexionDB.getConnection();
                String sortieISO = LocalDate.parse(txtSortie.getText(), FMT).toString();
                String sql;
                PreparedStatement ps;
                if (txtRetour.getText().isEmpty()) {
                    sql = "INSERT INTO emprunt (matricule, code_liv, sortie, retour, etablissement_id) VALUES (?, ?, ?, NULL, ?)";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, txtMatricule.getText());
                    ps.setString(2, txtCodeLiv.getText());
                    ps.setString(3, sortieISO);
                    ps.setInt(4, ConnexionDB.getEtablissementId());
                } else {
                    String retourISO = LocalDate.parse(txtRetour.getText(), FMT).toString();
                    sql = "INSERT INTO emprunt (matricule, code_liv, sortie, retour, etablissement_id) VALUES (?, ?, ?, ?, ?)";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, txtMatricule.getText());
                    ps.setString(2, txtCodeLiv.getText());
                    ps.setString(3, sortieISO);
                    ps.setString(4, retourISO);
                    ps.setInt(5, ConnexionDB.getEtablissementId());
                }
                ps.executeUpdate();
                lblMessage.setText("Emprunt ajouté !");
                viderFormulaire(txtMatricule, txtCodeLiv, txtSortie, txtRetour);
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
                alert.setHeaderText("Modifier l'emprunt");
                alert.setContentText("Voulez-vous vraiment modifier cet emprunt ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection();
                            String sortieISO = LocalDate.parse(txtSortie.getText(), FMT).toString();
                            String sql;
                            PreparedStatement ps;
                            if (txtRetour.getText().isEmpty()) {
                                sql = "UPDATE emprunt SET sortie=?, retour=NULL WHERE matricule=? AND code_liv=? AND etablissement_id=?";
                                ps = conn.prepareStatement(sql);
                                ps.setString(1, sortieISO);
                                ps.setString(2, txtMatricule.getText());
                                ps.setString(3, txtCodeLiv.getText());
                                ps.setInt(4, ConnexionDB.getEtablissementId());
                            } else {
                                String retourISO = LocalDate.parse(txtRetour.getText(), FMT).toString();
                                sql = "UPDATE emprunt SET sortie=?, retour=? WHERE matricule=? AND code_liv=? AND etablissement_id=?";
                                ps = conn.prepareStatement(sql);
                                ps.setString(1, sortieISO);
                                ps.setString(2, retourISO);
                                ps.setString(3, txtMatricule.getText());
                                ps.setString(4, txtCodeLiv.getText());
                                ps.setInt(5, ConnexionDB.getEtablissementId());
                            }
                            ps.executeUpdate();
                            lblMessage.setText("Emprunt modifié !");
                            txtMatricule.setDisable(false);
                            txtCodeLiv.setDisable(false);
                            viderFormulaire(txtMatricule, txtCodeLiv, txtSortie, txtRetour);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez un emprunt d'abord !");
            }
        });

        btnSupprimer.setOnAction(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Supprimer l'emprunt");
                alert.setContentText("Voulez-vous vraiment supprimer cet emprunt ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection();
                            String sql = "DELETE FROM emprunt WHERE matricule=? AND code_liv=? AND etablissement_id=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, selected.get(0));
                            ps.setString(2, selected.get(1));
                            ps.setInt(3, ConnexionDB.getEtablissementId());
                            ps.executeUpdate();
                            lblMessage.setText("Emprunt supprimé !");
                            txtMatricule.setDisable(false);
                            txtCodeLiv.setDisable(false);
                            viderFormulaire(txtMatricule, txtCodeLiv, txtSortie, txtRetour);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez un emprunt d'abord !");
            }
        });

        btnRetour.setOnAction(e -> {
            txtMatricule.setDisable(false);
            txtCodeLiv.setDisable(false);
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
            String sql = "SELECT matricule, code_liv, " +
                    "strftime('%d/%m/%Y', sortie) AS sortie, " +
                    "strftime('%d/%m/%Y', retour) AS retour " +
                    "FROM emprunt WHERE etablissement_id=?";
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