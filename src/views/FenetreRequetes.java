package views;

import utils.ExportPDF;
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

public class FenetreRequetes {

    private TableView<ObservableList<String>> table = new TableView<>();
    private String dernierSQL = "";

    public void afficher(Stage stage) {
        stage.setTitle("Requêtes - Gestion Bibliothèque");

        Text titre = new Text("Faire des Requêtes");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        ComboBox<String> cbRequetes = new ComboBox<>();
        cbRequetes.getItems().addAll(
                "Liste de tous les étudiants",
                "Liste de tous les livres",
                "Liste de tous les emprunts",
                "Emprunts en cours (non retournés)",
                "Livres empruntés par étudiant",
                "Nombre d'emprunts par étudiant"
        );
        cbRequetes.setPromptText("Choisir une requête...");
        cbRequetes.setMinWidth(300);

        TextArea txtSQL = new TextArea();
        txtSQL.setPromptText("Ou écris ta propre requête SQL ici...");
        txtSQL.setMaxHeight(80);

        Button btnExecuter = new Button("Exécuter");
        Button btnExporterPDF = new Button("Exporter en PDF");
        Button btnRetour = new Button("Retour");
        Label lblMessage = new Label();

        btnExecuter.setStyle("-fx-background-color: darkblue; -fx-text-fill: white;");
        btnExporterPDF.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");

        btnExecuter.setOnAction(e -> {
            String sql = "";
            if (cbRequetes.getValue() != null) {
                switch (cbRequetes.getValue()) {
                    case "Liste de tous les étudiants":
                        sql = "SELECT * FROM ETUDIANT";
                        break;
                    case "Liste de tous les livres":
                        sql = "SELECT * FROM LIVRE";
                        break;
                    case "Liste de tous les emprunts":
                        sql = "SELECT * FROM EMPRUNT";
                        break;
                    case "Emprunts en cours (non retournés)":
                        sql = "SELECT E.MATRICULE, E.NOM, E.PRENOMS, L.TITRE, EM.SORTIE " +
                                "FROM ETUDIANT E " +
                                "JOIN EMPRUNT EM ON E.MATRICULE = EM.MATRICULE " +
                                "JOIN LIVRE L ON EM.CODE_LIV = L.CODE_LIV " +
                                "WHERE EM.RETOUR IS NULL";
                        break;
                    case "Livres empruntés par étudiant":
                        sql = "SELECT E.MATRICULE, E.NOM, E.PRENOMS, L.TITRE, L.AUTEUR, EM.SORTIE, EM.RETOUR " +
                                "FROM ETUDIANT E " +
                                "JOIN EMPRUNT EM ON E.MATRICULE = EM.MATRICULE " +
                                "JOIN LIVRE L ON EM.CODE_LIV = L.CODE_LIV";
                        break;
                    case "Nombre d'emprunts par étudiant":
                        sql = "SELECT E.MATRICULE, E.NOM, E.PRENOMS, COUNT(EM.CODE_LIV) AS NB_EMPRUNTS " +
                                "FROM ETUDIANT E " +
                                "LEFT JOIN EMPRUNT EM ON E.MATRICULE = EM.MATRICULE " +
                                "GROUP BY E.MATRICULE, E.NOM, E.PRENOMS";
                        break;
                }
            } else if (!txtSQL.getText().isEmpty()) {
                sql = txtSQL.getText();
            }

            if (!sql.isEmpty()) {
                dernierSQL = sql;
                executerRequete(sql, lblMessage);
            } else {
                lblMessage.setText("Choisissez une requête ou écrivez du SQL !");
            }
        });

        btnExporterPDF.setOnAction(e -> {
            if (dernierSQL.isEmpty()) {
                lblMessage.setText("Exécutez d'abord une requête !");
                return;
            }
            String nomFichier = "C:\\Users\\rockd\\OneDrive\\Desktop\\rapport_" +
                    System.currentTimeMillis() + ".pdf";
            ExportPDF.exporterRequete(dernierSQL, nomFichier,
                    cbRequetes.getValue() != null ? cbRequetes.getValue() : "Requête personnalisée");
        });

        btnRetour.setOnAction(e -> new FenetreMenu().afficher(stage));

        HBox btnBox = new HBox(10, btnExecuter, btnExporterPDF, btnRetour);
        btnBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(15, titre, cbRequetes, txtSQL, btnBox, lblMessage, table);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void executerRequete(String sql, Label lblMessage) {
        table.getColumns().clear();
        table.getItems().clear();
        try {
            Connection conn = ConnexionDB.getConnection(null, null);
            ResultSet rs = conn.createStatement().executeQuery(sql);
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
            lblMessage.setText("Requête exécutée avec succès !");
        } catch (Exception e) {
            lblMessage.setText("Erreur : " + e.getMessage());
        }
    }
}