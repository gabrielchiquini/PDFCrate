package pdfcrate.examples;

import pdfcrate.components.Lines;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;
import pdfcrate.util.Point;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/** Add vector graphics to a document. It shows two paths that overlap together. */
public class Drawing {
  public static void main(String[] args) throws FileNotFoundException {
    final var document = new Document();
    document
        .margin(Edges.all(20))
        .add(new Lines(List.of(new Point(0, 100), new Point(50, 0), new Point(100, 100))))
        .add(
            // closed path with custom style
            new Lines(
                List.of(
                    new Point(0, 0),
                    new Point(100, 0),
                    new Point(100, 100),
                    new Point(0, 100),
                    new Point(0, 0)),
                document.lineStyle().toBuilder()
                    // 4pt on, 1pt off
                    .dashPattern(new float[] {4, 3})
                    // round cap
                    .capStyle(1)
                    .color(new Color(0x234F1E))
                    .build()))
        .render(new FileOutputStream("test-output/drawing.pdf"));
  }
}
