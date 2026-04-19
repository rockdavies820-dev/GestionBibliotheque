package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import database.ConnexionDB;

import java.io.FileOutputStream;
import java.sql.*;

public class ExportPDF {

    public static void exporterRequete(String sql, String nomFichier, String titreRequete) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(nomFichier));
            document.open();

            // Titre
            Font fontTitre = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph titre = new Paragraph(titreRequete, fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(20);
            document.add(titre);

            // Date
            Font fontDate = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Paragraph date = new Paragraph("Généré le : " + new java.util.Date(), fontDate);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Données
            Connection conn = ConnexionDB.getConnection(null, null);
            ResultSet rs = conn.createStatement().executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            PdfPTable table = new PdfPTable(cols);
            table.setWidthPercentage(100);

            // En-têtes
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            for (int i = 1; i <= cols; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(meta.getColumnName(i), fontHeader));
                cell.setBackgroundColor(new BaseColor(0, 0, 139));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Lignes
            Font fontData = new Font(Font.FontFamily.HELVETICA, 10);
            boolean pair = false;
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(
                            rs.getString(i) != null ? rs.getString(i) : "", fontData));
                    cell.setPadding(6);
                    if (pair) cell.setBackgroundColor(new BaseColor(230, 230, 250));
                    table.addCell(cell);
                }
                pair = !pair;
            }

            document.add(table);
            document.close();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("PDF exporté avec succès !\n" + nomFichier);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur export PDF : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void exporter(String nomTable, String nomFichier) {
        exporterRequete("SELECT * FROM " + nomTable, nomFichier, "Liste - " + nomTable);
    }
}