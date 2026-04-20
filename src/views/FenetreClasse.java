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

public class FenetreClasse {

    private TableView<ObservableList<String>> table = new TableView<>();

    public void afficher(Stage stage, String role) {
        stage.setTitle("Gestion des Classes");

        Text titre = new Text("Gestion des Classes");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtCode = new TextField(); txtCode.setPromptText("Code Classe");
        TextField txtIntitule = new TextField(); txtIntitule.setPromptText("Intitulé");
        TextField txtEffectif = new TextField(); txtEffectif.setPromptText("Effectif");

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnRetour = new Button("Retour");
        Label lblMessage = new Label();

        btnAjouter.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        btnModifier.setStyle("-fx-background-color: darkorange; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        HBox formulaire = new HBox(10, txtCode, txtIntitule, txtEffectif, btnAjouter, btnModifier, btnSupprimer);
        formulaire.setAlignment(Pos.CENTER);

        chargerDonnees();

        table.setOnMouseClicked(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtCode.setText(selected.get(0));
                txtIntitule.setText(selected.get(1));
                txtEffectif.setText(selected.get(2));
                txtCode.setDisable(true);
            }
        });

        btnAjouter.setOnAction(e -> {
            txtCode.setDisable(false);
            try {
                Connection conn = ConnexionDB.getConnection(null, null);
                String sql = "INSERT INTO classe VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtCode.getText());
                ps.setString(2, txtIntitule.getText());
                ps.setInt(3, Integer.parseInt(txtEffectif.getText()));
                ps.executeUpdate();
                lblMessage.setText("Classe ajoutée !");
                viderFormulaire(txtCode, txtIntitule, txtEffectif);
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
                alert.setHeaderText("Modifier la classe");
                alert.setContentText("Voulez-vous vraiment modifier " + selected.get(0) + " ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection(null, null);
                            String sql = "UPDATE classe SET intitule=?, effectif=? WHERE code_cl=?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, txtIntitule.getText());
                            ps.setInt(2, Integer.parseInt(txtEffectif.getText()));
                            ps.setString(3, txtCode.getText());
                            ps.executeUpdate();
                            lblMessage.setText("Classe modifiée !");
                            txtCode.setDisable(false);
                            viderFormulaire(txtCode, txtIntitule, txtEffectif);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez une classe d'abord !");
            }
        });

        btnSupprimer.setOnAction(e -> {
            ObservableList<String> selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Supprimer la classe");
                alert.setContentText("Voulez-vous vraiment supprimer " + selected.get(0) + " ?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            Connection conn = ConnexionDB.getConnection(null, null);
                            String sql = "DELETE FROM classe WHERE code_cl = ?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setString(1, selected.get(0));
                            ps.executeUpdate();
                            lblMessage.setText("Classe supprimée !");
                            txtCode.setDisable(false);
                            viderFormulaire(txtCode, txtIntitule, txtEffectif);
                            chargerDonnees();
                        } catch (Exception ex) {
                            lblMessage.setText("Erreur : " + ex.getMessage());
                        }
                    }
                });
            } else {
                lblMessage.setText("Sélectionnez une classe d'abord !");
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

        Scene scene = new Scene(vbox, 700, 500);
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
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM classe");
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