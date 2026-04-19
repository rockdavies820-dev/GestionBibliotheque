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

public class FenetreEmprunt {

    private TableView<ObservableList<String>> table = new TableView<>();

    public void afficher(Stage stage) {
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
                txtSortie.setText(selected.get(2));
                txtRetour.setText(selected.get(3) != null ? selected.get(3) : "");
                txtMatricule.setDisable(true);
                txtCodeLiv.setDisable(true);
            }
        });

        btnAjouter.setOnAction(e -> {
            txtMatricule.setDisable(false);
            txtCodeLiv.setDisable(false);
            try {
                Connection conn = ConnexionDB.getConnection(null, null);
                String sql = "INSERT INTO EMPRUNT VALUES (?, ?, TO_DATE(?, 'DD/MM/YYYY'), " +
                        (txtRetour.getText().isEmpty() ? "NULL" : "TO_DATE(?, 'DD/MM/YYYY')") + ")";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtMatricule.getText());
                ps.setString(2, txtCodeLiv.getText());
                ps.setString(3, txtSortie.getText());
                if (!txtRetour.getText().isEmpty()) ps.setString(4, txtRetour.getText());
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
                            Connection conn = ConnexionDB.getConnection(null, null);
                            String sql = "UPDATE EMPRUNT SET SORTIE=TO_DATE(?, 'DD/MM/YYYY'), " +
                                    "RETOUR=" + (txtRetour.getText().isEmpty() ? "NULL" : "TO_DATE(?, 'DD/MM/YYYY')") +
                                    " WHERE MATRICULE=? AND CODE_LIV=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, txtSortie.getText());
                            if (!txtRetour.getText().isEmpty()) {
                                ps.setString(2, txtRetour.getText());
                                ps.setString(3, txtMatricule.getText());
                                ps.setString(4, txtCodeLiv.getText());
                            } else {
                                ps.setString(2, txtMatricule.getText());
                                ps.setString(3, txtCodeLiv.getText());
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
                            Connection conn = ConnexionDB.getConnection(null, null);
                            String sql = "DELETE FROM EMPRUNT WHERE MATRICULE = ? AND CODE_LIV = ?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, selected.get(0));
                            ps.setString(2, selected.get(1));
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
            new FenetreMenu().afficher(stage);
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
            Connection conn = ConnexionDB.getConnection(null, null);
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM EMPRUNT");
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