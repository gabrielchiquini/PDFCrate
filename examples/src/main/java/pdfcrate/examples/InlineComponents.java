package pdfcrate.examples;

import pdfcrate.components.Row;
import pdfcrate.components.Spacer;
import pdfcrate.components.Text;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Example of Row component, creates a PDF with two inline text components, separated by 10pt
 */
public class InlineComponents {
  public static void main(String[] args) throws FileNotFoundException {
    final var row =
        new Row(new Text("FIRST ITEM, FIRST LINE\nFIRST ITEM, SECOND LINE"), new Spacer(30, 0), new Text("SECOND ITEM"));
    new Document()
        .margin(Edges.all(20))
        .add(row)
        .render(new FileOutputStream("test-output/inline-components.pdf"));
  }
}
