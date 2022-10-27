package pdfcrate.examples;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import pdfcrate.components.Paragraph;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static pdfcrate.examples.Common.LOREM_IPSUM_PARAGRAPH;

/** Modify the default text style */
public class CustomTextStyle {
  public static void main(String[] args) throws IOException {
    final var document = new Document();
    final var customFont = document.loadFont(Objects.requireNonNull(fontInputStream()));
    document
        // using one of the 14 default fonts
        .textStyle(document.textStyle().toBuilder().fontSize(20f).font(PDType1Font.COURIER).build())
        .margin(Edges.all(20))
        // will use the text style defined above
        .add(new Paragraph(LOREM_IPSUM_PARAGRAPH))
        // will modify the text styte only for this component
        .add(
            new Paragraph(
                LOREM_IPSUM_PARAGRAPH,
                document.textStyle().toBuilder()
                    // using a custom font
                    .font(customFont)
                    // line spacing, relative to font size
                    .leading(1.5f)
                    .textColor(new Color(0x234F1E))
                    .build()))
        .render(new FileOutputStream("test-output/custom-text-style.pdf"));
  }

  // loading the font file from resources directory
  public static InputStream fontInputStream() throws IOException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final var url = classloader.getResource("Roboto-Regular.ttf");
    if (url == null) {
      return null;
    }
    return url.openStream();
  }
}
