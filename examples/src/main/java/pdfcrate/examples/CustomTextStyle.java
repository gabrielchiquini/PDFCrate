package pdfcrate.examples;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import pdfcrate.components.Paragraph;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static pdfcrate.examples.Common.LOREM_IPSUM_PARAGRAPH;

/** Modify the default text style */
public class CustomTextStyle {
  public static void main(String[] args) throws FileNotFoundException {
    final var document = new Document();
    document
        // using one of the 14 default fonts, to use a custom font
        // add it to the document with loadFont()
        .textStyle(document.textStyle().toBuilder().fontSize(20f).font(PDType1Font.COURIER).build())
        .margin(Edges.all(20))
        // will use the text style defined above
        .add(new Paragraph(LOREM_IPSUM_PARAGRAPH))
        // will modify the text styte only for this component
        .add(
            new Paragraph(
                LOREM_IPSUM_PARAGRAPH,
                document.textStyle().toBuilder()
                    .font(PDType1Font.TIMES_BOLD_ITALIC)
                    // line spacing, relative to font size
                    .leading(1.5f)
                    .textColor(new Color(0x234F1E))
                    .build()))
        .render(new FileOutputStream("test-output/custom-text-style.pdf"));
  }
}
