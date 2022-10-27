package pdfcrate.examples;

import pdfcrate.components.Center;
import pdfcrate.components.Column;
import pdfcrate.components.Spacer;
import pdfcrate.components.Text;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Example of Row component, creates a PDF with two text components disposed vertically, separated
 * by 10pt and centered on the document
 */
public class MultipleComponentsCentered {
  public static void main(String[] args) throws FileNotFoundException {
    final var column =
        new Column(new Text("FIRST ITEM\nFIRST ITEM"), new Spacer(0, 10), new Text("SECOND ITEM"));
    new Document()
        .margin(Edges.all(20))
        .add(new Center(column))
        .render(new FileOutputStream("test-output/multiple-components-centered.pdf"));
  }
}
