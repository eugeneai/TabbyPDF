package ru.icc.cells.debug.visual;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import ru.icc.cells.common.*;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;

public class PdfBoxWriter implements PdfWriter, Closeable {

    private PDDocument          document;
    private PDPageContentStream contentStream;
    private int                 pageNumber;
    private boolean             showChunkOrder;

    public PdfBoxWriter(PDDocument document) throws IOException {
        this.document = document;
        this.contentStream =
                new PDPageContentStream(document, document.getPage(pageNumber), PDPageContentStream.AppendMode.APPEND,
                                        true, true);
    }

    public void setPage(int pageNumber) throws IOException {
        if (this.pageNumber != pageNumber) {
            this.pageNumber = pageNumber;
            contentStream.closeAndStroke();
            contentStream.close();
            contentStream = new PDPageContentStream(document, document.getPage(pageNumber),
                                                    PDPageContentStream.AppendMode.APPEND, true, true);
        }
    }

    @Override
    public void close() {
        try {
            contentStream.closeAndStroke();
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setShowChunkOrder(boolean showChunkOrder) {
        this.showChunkOrder = showChunkOrder;
    }

    @Override
    public void setColor(Color color) {
        try {
            contentStream.setStrokingColor(color);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends ru.icc.cells.common.Rectangle & Orderable> void drawRect(T rect) {
        try {
            contentStream.addRect(rect.getStartLocation().get(0), rect.getStartLocation().get(1),
                                  Math.abs(rect.getRightTopPoint().get(0) - rect.getStartLocation().get(0)),
                                  Math.abs(rect.getRightTopPoint().get(1) - rect.getStartLocation().get(1)));
            contentStream.stroke();
            if (showChunkOrder) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(rect.getStartLocation().get(0), rect.getRightTopPoint().get(1));
                contentStream.showText(String.valueOf(rect.getOrder()));
                contentStream.endText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawRuling(Ruling ruling) {
        try {
            contentStream.moveTo((float) ruling.getStartLocation().getX(), (float) ruling.getStartLocation().getY());
            contentStream.lineTo((float) ruling.getEndLocation().getX(), (float) ruling.getEndLocation().getY());
            contentStream.stroke();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}