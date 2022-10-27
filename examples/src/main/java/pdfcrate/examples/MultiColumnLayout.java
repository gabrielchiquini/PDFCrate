package pdfcrate.examples;

import pdfcrate.components.HorizontalLayout;
import pdfcrate.components.HorizontalLayoutColumn;
import pdfcrate.components.Paragraph;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static pdfcrate.examples.Common.LOREM_IPSUM_PARAGRAPH;

/**
 * Renders a three column layout where the first takes half of the page and the other two are split
 * in two, equally
 */
public class MultiColumnLayout {
  public static void main(String[] args) throws FileNotFoundException {
    final var layout =
        new HorizontalLayout(
            HorizontalLayoutColumn.proportional(new Paragraph(LOREM_IPSUM_PARAGRAPH), 2),
            HorizontalLayoutColumn.proportional(new Paragraph(LOREM_IPSUM_PARAGRAPH), 1),
            HorizontalLayoutColumn.proportional(new Paragraph(LOREM_IPSUM_PARAGRAPH), 1));
    new Document()
        .margin(Edges.all(20))
        .add(layout)
        .render(new FileOutputStream("test-output/multi-column-layout.pdf"));
  }
}
