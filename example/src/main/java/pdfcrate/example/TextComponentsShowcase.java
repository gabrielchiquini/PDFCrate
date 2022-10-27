package pdfcrate.example;

import pdfcrate.components.FlexibleText;
import pdfcrate.components.Paragraph;
import pdfcrate.components.Spacer;
import pdfcrate.components.Text;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/** Shows the behavior of the multiple text components existing in the library */
public class TextComponentsShowcase {
  public static void main(String[] args) throws FileNotFoundException {
    new Document()
        .margin(Edges.all(20))
        // Will print text line by line, overlapping boundaries if too wide
        .add(new Text("Text:"))
        .add(new Text(Common.LOREM_IPSUM_MULTIPLE_PARAGRAPHS))
        .add(new Spacer(0, 10))
        // Will reduce font size to fit one line of the text inside boundaries
        .add(new Text("Flexible Text:"))
        .add(new FlexibleText(Common.LOREM_IPSUM_PARAGRAPH))
        .add(new Spacer(0, 10))
        // Will break lines when reach the boundaries, but it creates a little overhead checking if
        // words fit in document
        // Also, it ignores spacing between words and line breaks
        .add(new Paragraph("Paragraph:"))
        .add(new Paragraph(Common.LOREM_IPSUM_MULTIPLE_PARAGRAPHS))
        .render(new FileOutputStream("test-output/text-components.pdf"));
  }
}
