package co.edu.uniquindio.mindsport.mindsportpro.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utilidad para exportar reportes simples a PDF.
 */
public class PdfExporter {

    /**
     * Genera un PDF en src/main/resources/reportes con tabla y una grafica opcional.
     */
    public static Path exportar(String titulo, String nombreArchivo, String[] headers, List<String[]> filas, BufferedImage grafica) throws IOException, DocumentException {
        Path carpeta = Paths.get("src", "main", "resources", "reportes");
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve(nombreArchivo);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        doc.open();

        doc.add(new Paragraph(titulo));
        doc.add(new Paragraph("Generado: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
        doc.add(new Paragraph(" "));

        if (headers != null && filas != null) {
            PdfPTable tabla = new PdfPTable(headers.length);
            tabla.setWidthPercentage(100);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h));
                cell.setGrayFill(0.9f);
                tabla.addCell(cell);
            }
            for (String[] row : filas) {
                for (String c : row) {
                    tabla.addCell(new Phrase(c != null ? c : ""));
                }
            }
            tabla.setSpacingAfter(10f);
            doc.add(tabla);
        }

        if (grafica != null) {
            doc.newPage();
            Image img = Image.getInstance(grafica, null);
            img.scaleToFit(PageSize.A4.getWidth() - 40, PageSize.A4.getHeight() - 80);
            img.setAlignment(Image.MIDDLE);
            doc.add(new Paragraph("Grafica"));
            doc.add(img);
        }

        doc.close();
        writer.close();

        Files.write(destino, baos.toByteArray());
        return destino;
    }
}
